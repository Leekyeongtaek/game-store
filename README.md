# Game-Store 프로젝트
- 플레이스테이션 스토어를 참고해서 만든 게임 사이트로 게임 관리 기능, 할인 게임 목록 조회 기능을 제공하고 있습니다.
- 핵심 목표: 테이블 설계부터 프론트, 백엔드 파트를 혼자 힘으로 제작하고 운영 서버 실행까지 해보는 것

## AWS EC2 운영 서버 주소
- [15.165.177.116:8080](http://15.165.177.116:8080/)

## 목차
- [기술 스택](#기술-스택)
- [테이블 ERD](#테이블-ERD)
- [프로젝트 개선 사항 기록](#프로젝트-개선-사항-기록)
- [멤버십 기능 테스트 코드](#멤버십-기능-테스트-코드)

## 기술 스택

### 백엔드
- Java 17
- SpringBoot 3.4.1
- JPA (Spring Data JPA, QueryDSL)
- MySql 8.0.32

### 프론트엔드
- Thymeleaf

### 개발 도구
- IntelliJ IDEA

## 테이블 ERD

### 게임 스토어 관련 테이블
<img width="800" alt="game_stroe_erd" src="https://github.com/user-attachments/assets/4608589b-2476-43ea-b1bb-1b13f171c846">

|테이블|인덱스명|컬럼|용도|
|------|---|---|---|
|game|idx_game_discount_id|game_discount_id|할인 게임 목록 조회시 game 테이블과 game_discount 테이블 조인시 사용|
|game|idx_type|type|필터에서 게임 유형 선택|
|game_discount|idx_discount_price|discount_price|필터에서 금액 범위 선택|
|game_genre|idx_game_id_genre_id|game_id, genre_id|필터에서 장르 선택|

- 할인 게임 목록 조회 쿼리에서 사용되는 인덱스

### 멤버십 관련 테이블
<img width="800" alt="game_stroe_erd" src="https://github.com/user-attachments/assets/b7e6c63a-8127-42e7-b277-0d6282c9f687">

## 프로젝트 개선 사항 기록

### 1. 레디스 캐시 적용
__개선 전__
- 게임 등록 관련 화면 진입시마다 변경 가능성 없는 동일한 데이터를 DB에서 반복 조회하는 문제 발생
```java
@Controller
GameController {
  ...
  @ModelAttribute("gameGroups")
  public List<GameGroupResponse> gameGroups() {...}
  @ModelAttribute("publishers")
  public List<PublisherResponse> publishers() {...}
  @ModelAttribute("languages")
  public List<LanguageResponse> languages() {...}
  @ModelAttribute("platforms")
  public List<PlatformResponse> platforms() {...}
  @ModelAttribute("genres")
  public List<GenreResponse> genres() {...}
}
```
__개선 후__
- RedisCacheManager 클래스를 생성 후 빈으로 등록
- 해당 데이터를 레디스 캐시에 저장해서 DB 조회를 줄이고 응답 속도 향상
__의문점과 해결__
- 웹서버 입장에서는 데이터를 조회하는 대상이 단순히 데이터베이스에서 레디스 서버로 변경된 것 아닐까?
- DB는 디스크 기반 I/O, Redis는 메모리 기반 I/O 속도 차이가 10배 정도 발생
- DB 커넥션 풀 기본 개수는 10개인데, 트랙피 많이 발생하는 시점에 변경 가능성이 적은 데이터를 자주 조회하면 DB 부하 발생 가능성이 높다
```java
@RequiredArgsConstructor
@Component
public class RedisCacheManager {
  private final StringRedisTemplate stringRedisTemplate;
  private final PublisherRepository publisherRepository;
  private final LanguageRepository languageRepository;
  private final PlatformRepository platformRepository;
  ...
  public List<GameGroupResponse> getCacheGameGroups() {
        final String KEY = "list:gameGroups";
        String jsonData = stringRedisTemplate.opsForValue().get(KEY);

        if (jsonData != null) {
            return deserializeJson(jsonData, new TypeReference<ArrayList<GameGroupResponse>>() {
            });
        }

        List<GameGroupResponse> gameGroupResponses = gameGroupRepository.findAll().stream()
                .sorted(Comparator.comparing(GameGroup::getName))
                .map(GameGroupResponse::new)
                .toList();

        stringRedisTemplate.opsForValue().set(KEY, serializeJson(gameGroupResponses), 24, TimeUnit.HOURS);

        return gameGroupResponses;
    }
    ...
    // 객체 → 바이트 스트림(파일, 네트워크 전송용 데이터)으로 변환
    private <T> String serializeJson(T data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    // 바이트 스트림 → 객체로 변환
    private <T> T deserializeJson(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

@Controller
GameController {
  private final RedisCacheManager redisCacheManager;

  @ModelAttribute("gameGroups")
  public List<GameGroupResponse> gameGroups() {
      return redisCacheManager.getCacheGameGroups();
  }
  ...
}
```

### 2. HTTP 통신 설정
```java
@Bean
public RestTemplate restTemplate() {
  SimpleClientHttpRequestFactory httpRequestFactory = new SimpleClientHttpRequestFactory();
  httpRequestFactory.setConnectTimeout(3000); //(연결시) 서버와 연결(Connection) 시도 최대 대기 시간
  httpRequestFactory.setReadTimeout(5000); //(응답시) socketTimeout, 서버 응답 대기 시간
}
/**
 * RestTemplate 커넥션 타임아웃용 테스트 API
 *
 * @return
 */
@GetMapping("/connection-test")
public ResponseEntity<Void> testConnection() {
    try {
        restTemplate.getForObject("http://10.255.255.1", String.class);
    } catch (ResourceAccessException e) {
        log.info("ConnectTimeout:: {}", e.getMessage());
    }
    return new ResponseEntity<>(HttpStatus.OK);
}

/**
 * HTTPBin HTTP 테스트용 API
 * @return
 */
@GetMapping("/read-test")
public ResponseEntity<Void> testReadTimeout() {
    try {
        restTemplate.getForObject("https://httpbin.org/delay/6", String.class);
    } catch (ResourceAccessException e) {
        log.info("ReadTimeout:: {}", e.getMessage());
    }
    return new ResponseEntity<>(HttpStatus.OK);
}
```
__타임아웃 옵션__
- HTTP 통신 시 연결 시간(connect timeout)이나 응답 대기 시간(read timeout)을 설정하지 않으면, 네트워크 지연이나 서버 응답이 없는 상황에서도 요청을 기다리게 된다<br>
- 결국 쓰레드 풀에 쓰레드가 반납되지 않게 되어 서버가 다운되는 상황이 발생하게 된다

### 3. 쿼리 실행 계획, 인덱스 설정
```sql
#할인 중인 게임 목록 조회 쿼리
SELECT g.*
FROM game g JOIN game_discount gd 
	ON g.game_discount_id = gd.game_discount_id
```
__인덱스 설정 관련 학습사항__
- game_discount_id 컬럼에 인덱스를 생성하기 전에는 game 테이블의 rows 수가 약 476건으로 표시되었으나, 인덱스 생성 후에는 약 268건으로 줄어들어 쿼리 성능이 향상됨
- 인덱스가 없을 경우 전체 테이블 스캔이 발생하는 반면, 인덱스가 존재하면 필요한 데이터만 빠르게 조회하기 때문이다
- 옵티마이저는 인덱스를 통해 더 효율적인 조인 순서를 선택한다
- 복합 인덱스는 왼쪽 순서부터 인덱스가 적용된다

#### 실행 계획 타입
|이름|용도|
|-----|-----|
|All|테이블 전체 스캔|
|Const|단일 테이블 조회, 1건 매칭, primary, unique key|
|Eq-ref|조인 조회, 1건 매칭, primary, unique key|
|Ref|일반 인덱스(중복 허용), game 테이블의 type 컬럼 인덱스처럼 중복 존재(PRODUCT, DLC, ITEM, COSTUME, CHARATOR)|
|Range|인덱스에 범위 검색(ex:Between)|
|Index|인덱스를 전체 스캔|

#### 외래키 관련 학습 내용
- 외래키 설정시 참조 무결성(외래키 값은 반드시 해당 테이블에 존재해야함) 제약 조건 때문에 데이터 삽입/수정 실패하는 상황이 발생한다
- 레코드 삭제/수정시 순서에 제약이 생긴다
- 급하게 수정사항 발생시 참조 대상이 없으면 데이터 삽입 실패 발생할 수 있다

### 스프링 AOP 관련 학습

#### 프로젝트 적용 코드
```java
@Slf4j
@Component
@Aspect
public class MyAspect {

  @Around("execution(* com.mrlee.game_store.service.GameService.*(..))")
  public Object monitorExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

    log.info("Execution method: {}", joinPoint.getSignature().getName());

    Object[] args = joinPoint.getArgs();
    for (int i = 0; i < args.length; i++) {
      log.info("Execution Parameter [{}]: {}", i + 1, args[i]);
    }

    long startTime = System.currentTimeMillis();

    Object result = joinPoint.proceed();

    long endTime = System.currentTimeMillis();
    long executionTime = endTime - startTime;

    if (executionTime > 1000) {
      log.warn("[PERFORMANCE WARNING] Execution time = {}ms", executionTime);
    }

    return result;
  }

  @Around("@annotation(httpRetry)")
  public Object handleHttpRetry(ProceedingJoinPoint joinPoint, HttpRetry httpRetry) throws Throwable {

    int maxRetryCount = httpRetry.value();
    Exception exception = null;

    log.info("handleHttpRetry: {}", joinPoint.getSignature().getName());

    for (int i = 0; i <= maxRetryCount; i++) {
      try {
	log.info("handleHttpRetry Count={}/{}", i, maxRetryCount);
	return joinPoint.proceed();
      } catch (Exception e) {
	log.info("HTTP 통신 오류:: {}", e.getMessage());
	exception = e;
	if (i == (maxRetryCount - 1)) {
	    Thread.sleep(1000);
	}
      }
    }

    throw exception != null ? exception : new RuntimeException("http 재시도 호출 로직에서 오류 발생.");
  }
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpRetry {
  int value() default 1;
}

@Slf4j
@RequiredArgsConstructor
@Component
public class RestTemplateUtil {

  @HttpRetry
  public void post(String url, Object request) {
    HttpEntity<Object> httpEntity = createHttpEntity(request);
    callIamPort(httpEntity);
  }
}
```
- 쿼리 실행 시간이 1초가 초과하면 해당되는 메서드와 파라미터 조건 로그 기록
- HTTP 통신 오류 발생시 재시도 한 번 더 실행

#### AOP 기술에 대한 학습 내용 정리
- 핵심 목표: 핵심 기능과 부가 기능을 분리하여, 변하지 않는 핵심 기능과 자주 변경될 수 있는 부가 기능을 명확히 구분한다. 부가 기능에 변경이 생기면 해당 부분만 수정하면 되므로 단일 책임 원칙을 준수한다
- 포인트컷으로 부가 기능 적용 대상 여부를 판단하고, 스프링 빈 후처리기가 대상 객체를 프록시 팩토리를 통해 프록시 객체로 생성하고 원본 객체를 대신해서 스프링 빈으로 등록한다
- 주요 구성 요소는 Advisor = Advice(부가 기능) + Pointcut(프록시 적용 대상 체크)
- 타임 체크 어드바이스에서 invocation.proceed()를 기준으로 측정한 시간은 그 안에서 실행된 (모든 어드바이스 + 대상)메서드의 실행 시간까지 포함된다
- 핵심 기능과 부가 기능 분리를 위한 디자인 패턴 적용 과정<br>

|이름|용도|문제점|
|---|---|---|
|템플릿 메서드|변하는 부분을 추상 클래스 상속을 통해 해결|상속으로 인한 강한 결합도, 부모 클래스 변경 사항 발생시 하위 클래스에 영향이 생김|
|전략(템플릿 콜백)|인터페이스를 통해 템플릿 메서드의 결합도 문제를 해결하고, 변경 부분을 실행 시점에 변경 가능|부가 기능 적용을 위해 핵심 코드를 수정 해야함|
|프록시|대상 객체를 호출하기 전에 부가 기능, 접근 제어 기능 수행, 클라이언트는 대상 객체가 프록시인지 원본 객체인지 모름|프록시 객체를 직접 만들려면 엄청난 수의 클래스 생성이 필요|
```java
//템플릿 메서드 패턴
public abstract class AbstractTemplate<T> {

  public T execute() {
    //실행 시간 측정
    T result = call();
    //실행 시간 측정
    return result;
  }
  protected abstract T call();
}

//전략 패턴(템플릿 콜백)
public interface Callback<T> {
  T call();
}

public class Template {

  public <T> T execute(Callback<T> callback) {
    //실행 시간 측정
    T result = callback.call():
    //실행 시간 측정
    return result;
  }
}
```

- 사용 되는 기술<br>

|이름|용도|
|---|----|
|쓰레드 로컬|쓰레드마다 가지고 있는 개인 저장 공간|
|자바 리플렉션|런타임 시점에 조건에 따라 동적으로 메서드 호출이 가능|
|JDK 동적 프록시|인터페이스 기반 프록시를 동적으로 생성|
|CGLIB 프록시|구체 클래스 기반의 프록시를 동적으로 생성|
|프록시 팩토리|인터페이스가 있으면 JDK 동적 프록시 사용, 구체 클래스만 존재하면 CGLIB 프록시 사용|
|빈 후처리기|스프링 빈 등록시 대상 객체를 프록시 객체로 생성해서 대신 등록하는 역할|

#### 자바 리플렉션과 AOP
```
public class MyService {
  public void save() {...}
  public void call() {...}
}

//ReflectiveMethodInvocation 인터셉터 호출 체인(Advice 체인)을 순차적으로 실행하기 위한 객체
public class TimeAdvice MethodInterceptor {
  //invocation 객체 내부에 대상 객체, 메서드, 인자 정보가 들어 있다
  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    //타임 체크 시작
    invocation.proceed(); // 인터셉터 호출 체인, 리플렉션으로 대상 메서드 호출
    //타임 체크 종료
  }
}
//invocation: 호출
public class ReflectiveMethodInvocation implements MethodInvocation {
    private final Method method;
    private final Object[] arguments;
    private final Object target;
    private List<MethodInterceptor> interceptors; // 적용할 어드바이스 목록
    ...
    
    public Object proceed() throws Throwable {
        ...
      if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
	//Method: 메서드에 대한 메타 정보 보관(이름, 클래스, 반환 타입...)
        //method.invoke(): target의 해당 메서드를 인자와 함께 실행
  	//런타임 시점에 메서드를 유연하게 실행 가능
	//MyService 객체의 call() 메서드 호출하라
        return method.invoke(target, arguments); // <- 리플렉션 사용해서 대상 메서드 호출
      }
      Object interceptorOrInterceptionAdvice = this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
      MethodInterceptor interceptor = (MethodInterceptor) interceptorOrInterceptionAdvice;
      return interceptor.invoke(this);
    }
}

@Test
void testProxy() {
  //1. 대상 객체 생성
  MyService target = new MyService();

  //2. 포인트컷 생성
  NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
  pointcut.setMappedName("call"); //메서드 이름이 'call'인 경우만 어드바이스 적용

  //3. 어드바이스 생성
  TimeAdvice advice = new TimeAdvice();

  //4. 어드바이저 생성
  Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);

  //5. 프록시 생성
  ProxyFactory factory = new ProxyFactory(target); //대상 객체 전달
  factory.addAdvisor(advisor);

  MyService proxy = (MyService) factory.getProxy();

  //6. 프록시 메서드 호출
  proxy.save(); //프록시 방식에 따라 리플렉션 호출 여부는 다름, 어드바이스 적용 X
  //ReflectiveMethodInvocation 객체 생성과 어드바이스 목록 파라미터 전달
  proxy.call(); //리플렉션을 통한 메서드 호출, 어드바이스 적용 O
}
```

### 멤버십 기능 개선 과정
#### 기능 요약
- 멤버십은 FREE, BRONZE, SILVER, GOLD 등급이 존재
- 결제일로부터 일정 기간 이내는 전액 환불, 이후는 하루 사용 금액 차감 후 환불
- 환불 요청 시 등급에 따른 환불 정책 적용
- 주요 학습 목표: 멤버십 등급에 따른 환불 금액 계산

#### 초기 구조의 한계점
- MemberSubscription 엔티티 내부에서 비즈니스 로직을 처리하면서 지연 로딩 문제, 엔티티의 책임이 너무 커지는 상황 발생

```java
public class MemberSubscription {

	private static final int FULL_REFUND_PERIOD = 7;
	private static final int NON_REFUNDABLE_AFTER = 15;

	private Long id;
	private Membership membership;
    ...
	public long calculateRefundFee(LocalDate refundRequestDate) {
		validateRefundable();
		long usedDays = calculateUsedDays(refundRequestDate);
		validateRefundableDays(usedDays);
		if (isFullRefundEligible(usedDays)) {
			return membership.getFullRefundAmount();
		}
		return calculateRefundableFee(usedDays);
	}

	private void validateRefundable() {
		if (membership.isFree()) {
			throw new IllegalArgumentException("무료 멤버십은 환불 금액 계산이 불가능합니다.");
		}
	}

	private long calculateUsedDays(LocalDate refundRequestDate) {
        return ChronoUnit.DAYS.between(startDate, refundRequestDate);
    }

    private long calculateRefundableFee(long usedDays) {
        long usedFee = membership.calculateDailyUsageFee() * usedDays;
        return membership.getFullRefundAmount() - usedFee;
    }

    private void validateRefundableDays(long usedDays) {
        if (usedDays > NON_REFUNDABLE_AFTER) {
            throw new IllegalArgumentException("멤버십 사용 기간이 " + NON_REFUNDABLE_AFTER + "일 지나면 환불이 불가능합니다.");
        }
    }

    private boolean isFullRefundEligible(long days) {
        return days < FULL_REFUND_PERIOD;
    }
}
```
#### 개선 방향
- RefundPolicy 인터페이스 전략 패턴 도입과 환불 관련 정책 역할 부여
- 등급별 환불 정책 클래스를 별도로 구현
- 공통 로직은 RefundPolicy 인터페이스의 default 메서드로 제공하여 중복을 없애고, 각 등급별 정책 클래스는 예외 처리와 금액 계산 방식만 오버라이딩하도록 구성
- 맵 자료구조를 통해 구현 클래스를 가져오는 방법을 사용해서 if-else 문을 사용하지 않고 가독성을 높이도록 구성

```java
public interface RefundPolicy {

    void validateRefundable(long usedDays);

    long calculateRefundAmount(RefundInfo refundInfo);

    default RefundType isFullRefundable(long usedDays) {
        if (usedDays < 3) {
            return RefundType.FULL;
        }
        return RefundType.PARTIAL;
    }

    default void validateRefundAmount(long refundAmount) {
        if (refundAmount <= 0) {
            throw new IllegalArgumentException("환불 예상 금액이 0원 이하입니다.");
        }
    }

    default long processRefundAmount(RefundInfo refundInfo) {
        validateRefundable(refundInfo.getUsedDays());
        long refundAmount = calculateRefundAmount(refundInfo);
        validateRefundAmount(refundAmount);
        return refundAmount;
    }
}

public class DefaultRefundPolicy implements RefundPolicy {

    private static final int NON_REFUNDABLE_AFTER = 15;

    @Override
    public void validateRefundable(long usedDays) {
        if (usedDays > NON_REFUNDABLE_AFTER) {
            throw new IllegalArgumentException(NON_REFUNDABLE_AFTER + "일 경과후에는 환불 요청이 불가능합니다.");
        }
    }

    @Override
    public long calculateRefundAmount(RefundInfo refundInfo) {
        if (isFullRefundable(refundInfo.getUsedDays()) == RefundType.FULL) {
            return refundInfo.getFullRefundAmount();
        }
        return Math.max(0, refundInfo.getFullRefundAmount() - (refundInfo.getDailyUsageFee() * refundInfo.getUsedDays()));
    }
}

public class GoldRefundPolicy implements RefundPolicy {

    private static final int NON_REFUNDABLE_AFTER = 20;

    @Override
    public void validateRefundable(long usedDays) {
        if (usedDays > NON_REFUNDABLE_AFTER) {
            throw new IllegalArgumentException(NON_REFUNDABLE_AFTER + "일 경과후에는 환불 요청이 불가능합니다.");
        }
    }

    @Override
    public long calculateRefundAmount(RefundInfo refundInfo) {
        if (isFullRefundable(refundInfo.getUsedDays()) == RefundType.FULL) { //enum은 싱글턴이라 == 비교가 더 안전하고 성능도 좋다, 안전하다는 의미는 NPE가 발생하지 않음
            return refundInfo.getFullRefundAmount();
        }
        return Math.max(0, refundInfo.getFullRefundAmount() - (refundInfo.getDailyUsageFee() * refundInfo.getUsedDays()));
    }
}

public class RefundService {

	private static final Map<MembershipType, RefundPolicy> REFUND_POLICY_MAP = new HashMap<>();

	static {
		REFUND_POLICY_MAP.put(MembershipType.BRONZE, new DefaultRefundPolicy());
		REFUND_POLICY_MAP.put(MembershipType.SILVER, new DefaultRefundPolicy());
		REFUND_POLICY_MAP.put(MembershipType.GOLD, new GoldRefundPolicy());
	}

	public void processRefund(MemberSubscription memberSubscription, String reason) {
        Payment payment = paymentService.getPaymentById(memberSubscription.getPaymentId());
        long refundAmount = calculateRefundAmount(memberSubscription);
        requestRefund(new RefundRequest(payment, refundAmount, reason));
    }

	private long calculateRefundAmount(MemberSubscription memberSubscription) {
        Membership membership = memberSubscription.getMembership();
        RefundPolicy refundPolicy = getRefundPolicy(membership.getName());
        return refundPolicy.processRefundAmount(new RefundInfo(memberSubscription));
    }

	private void requestRefund(RefundRequest form) {
        restTemplateUtil.post("https://api.iamport.kr/payment/cancel/", new CancelRequest(form.getMerchantId(), form.getRefundAmount()));
        refundRepository.save(new Refund(form.getPaymentId(), (int) form.getRefundAmount(), form.getReason()));
    }

	private RefundPolicy getRefundPolicy(MembershipType membershipType) {
        return Optional.ofNullable(REFUND_POLICY_MAP.get(membershipType))
                .orElseThrow(() -> new IllegalArgumentException("환불 정책을 찾을 수 없습니다: " + membershipType));
    }
}
```
### 멤버십 기능 테스트 코드
```java
public class TestFixtures {

    public static Membership getFreeMembership() {
        return Membership.builder()
                .name(Membership.MembershipType.FREE)
                .price(0)
                .durationDays(-1) // -1이 "무제한"을 의미하는 이유는, 많은 시스템에서 -1을 “제한 없음”이나 “끝이 없음”을 나타내는 값으로 사용하기 때문
                .build();
    }

    public static Membership getBronzeMembership() {
        return Membership.builder()
                .name(Membership.MembershipType.BRONZE)
                .price(10000)
                .durationDays(30)
                .build();
    }

    public static Membership getSilverMembership() {
        return Membership.builder()
                .name(Membership.MembershipType.SILVER)
                .price(20000)
                .durationDays(30)
                .build();
    }

    public static Membership getGoldMembership() {
        return Membership.builder()
                .name(Membership.MembershipType.GOLD)
                .price(30000)
                .durationDays(30)
                .build();
    }

    public static MemberSubscription createSubscription(Long memberId, Membership membership, LocalDate startDate) {
        return MemberSubscription.builder()
                .memberId(memberId)
                .membership(membership)
                .startDate(startDate)
                .build();
    }

    /**
     * RefundInfo 객체를 생성한다
     *
     * @param fullRefundAmount 전체 환불 금액을 의미합니다.
     *                         예) 전액 환불이 가능한 경우의 환불 예상 금액.
     * @param usedDays         사용일수를 의미합니다.
     *                         예) 멤버십을 사용한 총 일수.
     * @param dailyUsageFee    일일 사용 요금을 의미합니다.
     *                         예) 하루 사용 시 차감되는 금액.
     * @return 생성된 RefundInfo 객체.
     */
    public static RefundInfo createRefundInfo(long fullRefundAmount, long usedDays, long dailyUsageFee) {
        return RefundInfo.builder()
                .fullRefundAmount(fullRefundAmount)
                .usedDays(usedDays)
                .dailyUsageFee(dailyUsageFee)
                .build();
    }
}

class MembershipTest {

    //멤버십_하루_사용료는_멤버십_가격_나누기_멤버십_기간을_통해_구할_수_있다
    @Test
    void 멤버십_하루_사용료를_계산할_수_있다() {
        //given
        Membership bronzeMembership = TestFixtures.getBronzeMembership();

        //when
        long dailyUsageFee = bronzeMembership.calculateDailyUsageFee();

        //then
        //int expectedFee = 10000 / 30; // 나눗셈 연산 후 소수점을 버리는(내림 처리)
        int expectedFee = bronzeMembership.getPrice() / bronzeMembership.getDurationDays();
        Assertions.assertThat(dailyUsageFee).isEqualTo(expectedFee);
    }
}

class MemberSubscriptionTest {

    //Should-When 패턴
    //회원_구독_기간은_구독_시작일로부터_요청일_차이를_통해_구할_수_있다.
    @Test
    void 구독_사용_일수를_계산할_수_있다() {
        //given
        Membership bronzeMembership = TestFixtures.getBronzeMembership();
        LocalDate startDate = LocalDate.of(2025, 3, 10);
        LocalDate requestDate = LocalDate.of(2025, 3, 20);

        MemberSubscription memberSubscription = TestFixtures.createSubscription(
                1L, bronzeMembership, startDate);

        //when
        long usedDays = memberSubscription.calculateUsedDays(requestDate);

        //then
        assertThat(usedDays).isEqualTo(10);
    }

}

class DefaultRefundPolicyTest {

    private RefundPolicy defaultRefundPolicy;

    @BeforeEach
    public void setUp() {
        defaultRefundPolicy = new DefaultRefundPolicy();
    }

    @Test
    void 환불_가능_기간일_경우는_예외가_발생하지_않는다() {
        //when & then
        assertDoesNotThrow(() -> defaultRefundPolicy.validateRefundable(0));
        assertDoesNotThrow(() -> defaultRefundPolicy.validateRefundable(10));
        assertDoesNotThrow(() -> defaultRefundPolicy.validateRefundable(15));
    }

    @Test
    void 환불_가능_기간_이후일_경우는_예외가_발생한다() {
        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> defaultRefundPolicy.validateRefundable(16));

        //then
        assertEquals("15일 경과후에는 환불 요청이 불가능합니다.", exception.getMessage());
    }

    @Test
    void 전액_환불_가능_기간인_경우_전액_환불_해준다() {
        //given
        RefundInfo refundInfo = TestFixtures.createRefundInfo(20000, 2, 666);

        //when
        long refundAmount = defaultRefundPolicy.calculateRefundAmount(refundInfo);
        long fullRefundAmount = refundInfo.getFullRefundAmount();

        //then
        assertThat(refundAmount).isEqualTo(fullRefundAmount);
    }

    @Test
    void 전액_환불_기간이_경과하면_부분_환불_해준다() {
        //given
        RefundInfo refundInfo = TestFixtures.createRefundInfo(20000, 10, 666);

        //when
        long refundAmount = defaultRefundPolicy.calculateRefundAmount(refundInfo);
        long expectedRefundAmount = refundInfo.getFullRefundAmount() - (refundInfo.getDailyUsageFee() * refundInfo.getUsedDays());

        //then
        assertThat(refundAmount).isEqualTo(expectedRefundAmount);
    }

    @Test
    void 환불_프로세스_정상_케이스() {
        //given
        RefundInfo refundInfo = TestFixtures.createRefundInfo(20000, 2, 666);

        //when
        long refundAmount = defaultRefundPolicy.processRefundAmount(refundInfo);

        //then
        assertThat(refundAmount).isEqualTo(refundInfo.getFullRefundAmount());
    }

    @Test
    void 환불_프로세스_환불_예상금액이_0원_이하인_경우_예외발생() {
        //given
        RefundInfo refundInfo = TestFixtures.createRefundInfo(10000, 10, 1000);

        //when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> defaultRefundPolicy.processRefundAmount(refundInfo));
        assertThat(exception.getMessage()).isEqualTo("환불 예상 금액이 0원 이하입니다.");
    }

}

class GoldRefundPolicyTest {

    private RefundPolicy goldRefundPolicy;

    @BeforeEach
    public void setUp() {
        goldRefundPolicy = new GoldRefundPolicy();
    }

    @Test
    void 환불_가능_기간일_경우는_예외가_발생하지_않는다() {
        //when & then
        assertDoesNotThrow(() -> goldRefundPolicy.validateRefundable(0));
        assertDoesNotThrow(() -> goldRefundPolicy.validateRefundable(10));
        assertDoesNotThrow(() -> goldRefundPolicy.validateRefundable(20));
    }

    @Test
    void 환불_가능_기간_이후일_경우는_예외가_발생한다() {
        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> goldRefundPolicy.validateRefundable(21));

        //then
        assertEquals("20일 경과후에는 환불 요청이 불가능합니다.", exception.getMessage());
    }
}
```
