package com.mrlee.game_store.membership.controller;

import com.mrlee.game_store.membership.dto.request.MemberSubscriptionCancelForm;
import com.mrlee.game_store.membership.dto.request.MemberSubscriptionJoinForm;
import com.mrlee.game_store.membership.service.MemberSubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/member-subscription")
@RestController
public class MemberSubscriptionController {

    private final MemberSubscriptionService memberSubscriptionService;

    @PostMapping("/join")
    public ResponseEntity<Void> joinMemberSubscription(@RequestBody MemberSubscriptionJoinForm form) {
        memberSubscriptionService.joinMemberSubscription(form);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelMemberSubscription(@RequestBody MemberSubscriptionCancelForm form) {
        memberSubscriptionService.cancelMemberSubscription(form);
        return ResponseEntity.ok("멤버십 구독 취소 요청이 완료되었습니다.");
    }
}
