package com.mrlee.game_store.membership;

import com.mrlee.game_store.membership.service.IamPortToken;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IamPortTokenResponse {
    private String code;
    private String message;
    private IamPortToken response;
}
