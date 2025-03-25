package com.mrlee.game_store.membership.util;

import com.mrlee.game_store.common.HttpRetry;
import com.mrlee.game_store.membership.iamport.IamPortResponse;
import com.mrlee.game_store.membership.iamport.TokenResponse;
import com.mrlee.game_store.membership.iamport.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Component
public class RestTemplateUtil {

    private final RestTemplate restTemplate;
    private static int cnt = 0;

    @Value("${iamport.api-key}")
    private String restApiKey;

    @Value("${iamport.secret-key}")
    private String secretKey;

    @HttpRetry
    public void post(String url, Object request) {
        HttpEntity<Object> httpEntity = createHttpEntity(request);
        callIamPort(httpEntity);
    }

    private IamPortResponse callIamPort(HttpEntity<Object> httpEntity) {
        cnt++;
        if (cnt % 2 != 0) {
            throw new RuntimeException("아임포트 서버 오류 발생..");
        }
        return new IamPortResponse("200", "요청 성공");
    }

    private HttpEntity<Object> createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getIamPortToken().getAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    private Token getIamPortToken() {
        final String TOKEN_URL = "https://api.iamport.kr/users/getToken";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("imp_key", restApiKey);
        formData.add("imp_secret", secretKey);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

        ResponseEntity<TokenResponse> response = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, entity, TokenResponse.class);
        return response.getBody().getResponse();
    }
}
