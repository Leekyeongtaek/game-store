package com.mrlee.game_store.membership.service;

import com.mrlee.game_store.membership.domain.MemberSubscription;
import com.mrlee.game_store.membership.domain.Membership;
import com.mrlee.game_store.membership.dto.request.MemberSubscriptionCancelForm;
import com.mrlee.game_store.membership.dto.request.MemberSubscriptionJoinForm;
import com.mrlee.game_store.membership.repository.MemberSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.mrlee.game_store.membership.domain.MemberSubscription.MembershipStatus;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MemberSubscriptionService {

    private final MemberSubscriptionRepository memberSubscriptionRepository;
    private final MembershipService membershipService;
    private final PaymentService paymentService;
    private final RefundService refundService;

    /**
     * 멤버십 구독 가입 (기존 멤버십 취소 -> 신규 멤버십 결제 -> 신규 구독 정보 저장)
     * 구독 업그레이드에 대한 부분은 제외함
     */
    public void joinMemberSubscription(MemberSubscriptionJoinForm form) { // “Form” 접미사는 보통 "사용자 입력 폼" (ex: HTML Form)을 의미
        MemberSubscription currentSubscription = getActiveMemberSubscription(form.getMemberId());
        currentSubscription.cancelSubscription();

        Membership membership = membershipService.getMembershipById(form.getMembershipId());
        Long paymentId = paymentService.processPayment(form.getMemberId(), membership);

        createNewSubscription(form.getMemberId(), membership, paymentId);
    }

    /**
     * 멤버십 구독 해지 (기존 멤버십 취소 -> 구독 환불 요청 -> 무료 멤버십 전환)
     */
    public void cancelMemberSubscription(MemberSubscriptionCancelForm form) {
        MemberSubscription currentSubscription = getActiveMemberSubscription(form.getMemberId());
        currentSubscription.cancelSubscription();

        refundService.processRefund(currentSubscription, form.getReason());

        createNewFreeSubscription(form.getMemberId());
    }

    private void createNewSubscription(Long memberId, Membership membership, Long paymentId) {
        memberSubscriptionRepository.save(MemberSubscription.createPaidMemberSubscription(memberId, membership, paymentId));
        log.info("신규 멤버십 저장 완료: memberId={}, membershipId={}", memberId, membership.getId());
    }

    private void createNewFreeSubscription(Long memberId) {
        Membership membership = membershipService.getFreeMembership();
        memberSubscriptionRepository.save(MemberSubscription.createMemberSubscription(memberId, membership));
        log.info("무료 멤버십 저장 완료: memberId={}", memberId);
    }

    private MemberSubscription getActiveMemberSubscription(Long memberId) {
        List<MemberSubscription> memberSubscriptions = memberSubscriptionRepository.findByMemberIdAndStatus(memberId, MembershipStatus.ACTIVE);
        if (memberSubscriptions.size() != 1) {
            log.info("회원 번호, 멤버십 개수 = {}, {}", memberId, memberSubscriptions.size());
            throw new IllegalArgumentException("멤버십 무결성 오류가 발생했습니다.");
        }
        return memberSubscriptions.get(0);
    }

}
