package com.mrlee.game_store.membership.controller;

import com.mrlee.game_store.membership.dto.request.MembershipJoinForm;
import com.mrlee.game_store.membership.service.MembershipService;
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
@RequestMapping("/membership")
@RestController
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping("/join")
    public ResponseEntity<Void> joinMembership(@RequestBody MembershipJoinForm form) {
        membershipService.joinMembership(form);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //환불 후 무료 멤버십 변경
    //payments/cancel
}
