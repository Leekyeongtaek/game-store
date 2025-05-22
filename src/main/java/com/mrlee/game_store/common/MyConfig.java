package com.mrlee.game_store.common;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.persistence.EntityManager;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MyConfig {

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }

    //기본 커넥션 관련 설정은 무제한
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory httpRequestFactory = new SimpleClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(3000); //(연결시) 서버와 연결(Connection) 시도 최대 대기 시간, 클라이언트가 서버에 연결을 맺을 때 적용됨.
        httpRequestFactory.setReadTimeout(5000); //(응답시) socketTimeout, 서버 응답 대기 시간, 대상 서버에서 응답을 설정한 시간만큼 전달 받지 못하는 경우 예외 발생.
        return new RestTemplate(httpRequestFactory);
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}
