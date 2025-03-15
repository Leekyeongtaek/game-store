package com.mrlee.game_store.membership.iamport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IamPortResponse {
    private String code;
    private String message;

    public IamPortResponse(String code) {
        this.code = code;
    }
}
