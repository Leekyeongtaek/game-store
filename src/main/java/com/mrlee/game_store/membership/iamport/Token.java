package com.mrlee.game_store.membership.iamport;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    @JsonProperty(value = "access_token")
    private String accessToken;
    private long now;
    @JsonProperty(value = "expired_at")
    private long expiredAt;

    @Override
    public String toString() {
        return "IamPortToken{" +
                "accessToken='" + accessToken + '\'' +
                ", now=" + now +
                ", expiredAt=" + expiredAt +
                '}';
    }
}
