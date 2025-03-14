package com.mrlee.game_store.membership;

import com.mrlee.game_store.membership.service.IamPortToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Component
public class RestTemplateUtil {

    private final RestTemplate restTemplate;

    @Value("${iamport.api-key}")
    private String restApiKey;

    @Value("${iamport.secret-key}")
    private String secretKey;

    //강제 예외 발생 시키기
    //todo 소켓, 커넥션 타임아웃, 재요청

    private HttpHeaders createHttpHeaderWithToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + getIamPortToken().getAccessToken());
        return headers;
    }

    private IamPortToken getIamPortToken() {
        final String TOKEN_URL = "https://api.iamport.kr/users/getToken";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("imp_key", restApiKey);
        formData.add("imp_secret", secretKey);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

        ResponseEntity<IamPortTokenResponse> response = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, entity, IamPortTokenResponse.class);
        return response.getBody().getResponse();
    }
}
