package com.mrlee.game_store.membership.iamport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CancelRequest {

    private String merchantId;
    private long amount;
}
