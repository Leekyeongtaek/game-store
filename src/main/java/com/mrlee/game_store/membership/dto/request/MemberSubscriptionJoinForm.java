package com.mrlee.game_store.membership.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSubscriptionJoinForm {
    private Long memberId;
    private Long membershipId;
}
