package com.mrlee.game_store.membership.iamport;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {
    private String code;
    private String message;
    private Token response;
}
