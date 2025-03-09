package com.mrlee.game_store.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrlee.game_store.domain.GameGroup;
import com.mrlee.game_store.domain.GameType;
import com.mrlee.game_store.dto.response.*;
import com.mrlee.game_store.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

//데이터를 변경하는 작업(INSERT, UPDATE, DELETE)이 없음.
//JPA가 변경 감지를 비활성화하고 성능 최적화 효과가 있음.
//결국 트랜잭션 애노테이션은 필요하지 않다.
//@Transactional(readOnly = true)
@RequiredArgsConstructor
//특정 계층에 속하지 않는(C,S,R) 경우는 컴포넌트 사용.
@Component
public class RedisCacheManager {

    private final StringRedisTemplate stringRedisTemplate; //레디스 String 자료형 템플릿
    private final PublisherRepository publisherRepository;
    private final LanguageRepository languageRepository;
    private final PlatformRepository platformRepository;
    private final GenreRepository genreRepository;
    private final GameGroupRepository gameGroupRepository;
    private final ObjectMapper objectMapper;

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

    public List<GenreResponse> getCacheGenres() {
        final String KEY = "list:genres";
        String jsonData = stringRedisTemplate.opsForValue().get(KEY);

        if (jsonData != null) {
            return deserializeJson(jsonData, new TypeReference<ArrayList<GenreResponse>>() {
            });
        }

        List<GenreResponse> genreResponses = genreRepository.findAll().stream()
                .map(GenreResponse::new)
                .sorted(Comparator.comparing(GenreResponse::getName))
                .toList();

        stringRedisTemplate.opsForValue().set(KEY, serializeJson(genreResponses), 24, TimeUnit.HOURS);

        return genreResponses;
    }

    public List<PlatformResponse> getCachePlatforms() {
        final String KEY = "list:platforms";
        String jsonData = stringRedisTemplate.opsForValue().get(KEY);

        if (jsonData != null) {
            deserializeJson(jsonData, new TypeReference<ArrayList<PlatformResponse>>() {
            });
        }

        List<PlatformResponse> platformResponses = platformRepository.findAll()
                .stream()
                .map(PlatformResponse::new)
                .toList();

        stringRedisTemplate.opsForValue().set(KEY, serializeJson(platformResponses), 24, TimeUnit.HOURS);

        return platformResponses;
    }

    public List<LanguageResponse> getCacheLanguages() {
        final String KEY = "list:languages";
        String jsonData = stringRedisTemplate.opsForValue().get(KEY);

        if (jsonData != null) {
            return deserializeJson(jsonData, new TypeReference<ArrayList<LanguageResponse>>() {
            });
        }
        List<LanguageResponse> languageResponses = languageRepository.findAll()
                .stream()
                .map(LanguageResponse::new)
                .sorted(Comparator.comparing(LanguageResponse::getName))
                .toList();

        stringRedisTemplate.opsForValue().set(KEY, serializeJson(languageResponses), 24, TimeUnit.HOURS);

        return languageResponses;
    }

    public List<PublisherResponse> getCachePublishers() {
        final String KEY = "list:publishers";
        String jsonData = stringRedisTemplate.opsForValue().get(KEY);

        if (jsonData != null) {
            return deserializeJson(jsonData, new TypeReference<ArrayList<PublisherResponse>>() {
            });
        }

        List<PublisherResponse> publisherList = publisherRepository.findAll().stream()
                .map(PublisherResponse::new)
                .sorted(Comparator.comparing(PublisherResponse::getName))
                .toList();

        stringRedisTemplate.opsForValue().set(KEY, serializeJson(publisherList), 24, TimeUnit.HOURS);

        return publisherList;
    }

    public Map<String, String> getCacheGameTypes() {
        final String KEY = "map:gameTypes";
        String jsonData = stringRedisTemplate.opsForValue().get(KEY);

        if (jsonData != null) {
            return deserializeJson(jsonData, new TypeReference<LinkedHashMap<String, String>>() {
            });
        }

        Map<String, String> gameTypeMap = new LinkedHashMap<>();
        for (GameType gameType : GameType.values()) {
            gameTypeMap.put(gameType.name(), gameType.getKoreanName());
        }

        stringRedisTemplate.opsForValue().set(KEY, serializeJson(gameTypeMap), 24, TimeUnit.HOURS);

        return gameTypeMap;
    }

    public Map<String, String> getCacheWebBasePrices() {
        final String KEY = "map:webBasePrices";
        String jsonData = stringRedisTemplate.opsForValue().get(KEY);

        if (jsonData != null) {
            return deserializeJson(jsonData, new TypeReference<LinkedHashMap<String, String>>() {
            });
        }

        Map<String, String> webBasePrices = new LinkedHashMap<>();
        webBasePrices.put("0-4999", "4,999원 이하");
        webBasePrices.put("5000-9999", "5,000원 - 9,999원");
        webBasePrices.put("10000-19999", "10,000원 - 19,999원");
        webBasePrices.put("20000-49999", "20,000원 - 49,999원");
        webBasePrices.put("50000-999999", "50,000원 이상");

        stringRedisTemplate.opsForValue().set(KEY, serializeJson(webBasePrices), 24, TimeUnit.HOURS);

        return webBasePrices;
    }

// 제네릭 메서드와 와일드 카드는 용도가 다르다!
/*    private String toA(List<?> aa) {
        return "";
    }*/

    //직렬화(Serialization): 객체 → 바이트 스트림(파일, 네트워크 전송용 데이터)으로 변환하는 과정.
    //역직렬화(Deserialization): 바이트 스트림 → 객체로 변환하는 과정.
    private <T> String serializeJson(T data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    //<T>는 이 메소드에서 T라는 제네릭 메서드를 사용한다고 선언하는 부분
    //T는 이 메소드가 반환할 데이터 타입이고, 호출될 때 타입이 결정됨.
    private <T> T deserializeJson(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
