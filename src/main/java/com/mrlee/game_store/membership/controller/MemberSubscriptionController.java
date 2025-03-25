package com.mrlee.game_store.membership.controller;

import com.mrlee.game_store.membership.dto.request.MemberSubscriptionCancelForm;
import com.mrlee.game_store.membership.dto.request.MemberSubscriptionJoinForm;
import com.mrlee.game_store.membership.service.MemberSubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/member-subscription")
@RestController
public class MemberSubscriptionController {

    private final MemberSubscriptionService memberSubscriptionService;
    private final RestTemplate restTemplate;

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

    /**
     * RestTemplate 커넥션 타임아웃용 테스트 API
     *
     * @return
     */
    @GetMapping("/connection-test")
    public ResponseEntity<Void> testConnection() {
        try {
            restTemplate.getForObject("http://10.255.255.1", String.class);
        } catch (ResourceAccessException e) {
            log.info("ConnectTimeout:: {}", e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * HTTPBin HTTP 테스트용 API
     * @return
     */
    @GetMapping("/read-test")
    public ResponseEntity<Void> testReadTimeout() {
        try {
            restTemplate.getForObject("https://httpbin.org/delay/6", String.class);
        } catch (ResourceAccessException e) {
            log.info("ReadTimeout:: {}", e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
