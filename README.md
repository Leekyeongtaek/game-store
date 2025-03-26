# Game-Store 프로젝트
- 플레이스테이션 스토어를 참고해서 만든 게임 관리 사이트입니다.

## AWS EC2 운영 서버 주소
- http://15.165.177.116:8080/

## 목차
- [프로젝트 기술 상세](#프로젝트-기술-상세)
- [테이블 ERD](#테이블-ERD)
- [프로젝트 개선 사항 기록](#프로젝트-개선-사항-기록)

### 프로젝트 기술 상세
- IDE : IntelliJ
- Java : 17
- SpringBoot : 3.4.1
- MySql : 8.0.32
- ORM 기술 : JPA, SpringDataJPA, QueryDSL
- 뷰 템플릿 : 타임리프

### 테이블 ERD

#### 게임 스토어
<img width="800" alt="game_stroe_erd" src="https://github.com/user-attachments/assets/4608589b-2476-43ea-b1bb-1b13f171c846">

|테이블|인덱스명|컬럼|용도|
|------|---|---|---|
|game|idx_game_discount_id|game_discount_id|할인 게임 목록 조회시 game 테이블과 game_discount 테이블 조인시 사용|
|game|idx_type|type|필터에서 게임 유형 선택|
|game_discount|idx_discount_price|discount_price|필터에서 금액 범위 선택|
|game_genre|idx_game_id_genre_id|game_id, genre_id|필터에서 장르 선택|

- 할인 게임 목록 조회 쿼리에서 사용되는 인덱스

#### 멤버십
<img width="800" alt="game_stroe_erd" src="https://github.com/user-attachments/assets/b7e6c63a-8127-42e7-b277-0d6282c9f687">

### 프로젝트 개선 사항 기록

#### 1. 레디스 캐시 적용
```java
// 레디스 적용 전 코드
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

// 레디스 적용 후 코드
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
__개선 사항__
- 레디스 캐시 적용 전에는 게임 등록 관련 화면 진입시마다 매번 5번의 쿼리가 발생.<br>
- 캐시 적용 후에는 해당 데이터가 레디스 캐시에 키로 존재하지 않으면 최초 한번 조회 후 캐시 데이터를 반환하는 방식으로 변경해서 성능 향상.

#### 2. HTTP 통신 설정
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

#### 3. 쿼리 실행 계획, 인덱스 설정
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

##### 실행 계획 타입
|이름|용도|
|-----|-----|
|All|테이블 전체 스캔|
|Const|단일 테이블 조회, 1건 매칭, primary, unique key|
|Eq-ref|조인 조회, 1건 매칭, primary, unique key|
|Ref|일반 인덱스(중복 허용), game 테이블의 type 컬럼 인덱스처럼 중복 존재(PRODUCT, DLC, ITEM, COSTUME, CHARATOR)|
|Range|인덱스에 범위 검색(ex:Between)|
|Index|인덱스를 전체 스캔|

##### 외래키 관련 학습 내용
- 외래키 설정시 참조 무결성(외래키 값은 반드시 해당 테이블에 존재해야함) 제약 조건 때문에 데이터 삽입/수정 실패하는 상황이 발생한다
- 레코드 삭제/수정시 순서에 제약이 생긴다
- 급하게 수정사항 발생시 참조 대상이 없으면 데이터 삽입 실패 발생할 수 있다

#### 스프링 AOP 적용
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
