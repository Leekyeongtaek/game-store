# Game-Store 프로젝트
- 플레이스테이션 스토어를 참고해서 만든 게임 관리 사이트입니다.

## AWS EC2 운영 서버 주소
- http://15.165.177.116:8080/

## 목차
- [프로젝트 기술 상세](#프로젝트-기술-상세)
- [테이블 ERD](#테이블-ERD)

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

#### 레디스 캐시 적용
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
}
```
레디스 캐시 적용 전에는 게임 등록 관련 화면 진입시마다 매번 5번의 쿼리가 발생.<br>
캐시 적용 후에는 해당 데이터가 레디스 캐시에 키로 존재하지 않으면 최초 한번 조회 후 캐시 데이터를 반환하는 방식으로 변경해서 성능 향상.
