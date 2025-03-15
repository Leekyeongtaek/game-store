package com.mrlee.game_store.membership.service;

import com.mrlee.game_store.membership.domain.MemberMembership;
import com.mrlee.game_store.membership.domain.Membership;
import com.mrlee.game_store.membership.domain.Payment;
import com.mrlee.game_store.membership.dto.request.MembershipJoinForm;
import com.mrlee.game_store.membership.iamport.PaymentRequest;
import com.mrlee.game_store.membership.repository.MemberMembershipRepository;
import com.mrlee.game_store.membership.repository.MembershipRepository;
import com.mrlee.game_store.membership.repository.PaymentRepository;
import com.mrlee.game_store.membership.util.RestTemplateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final MemberMembershipRepository memberMembershipRepository;
    private final PaymentRepository paymentRepository;
    private final RestTemplateUtil restTemplateUtil;

    // 기존 joinMembership 메서드 개선: 하나의 메서드는 하나의 기능만을 가진다. SRP
    public void joinMembership(MembershipJoinForm form) {
        Membership membership = getMembershipById(form.getMembershipId());
        MemberMembership existMembership = getActiveMembership(form.getMemberId());
        existMembership.cancel(); // 기존 멤버십 상태를 '취소'로 업데이트

        // 신규 가입 멤버십 저장
        MemberMembership newMembership = new MemberMembership(form.getMemberId(), membership.getId(), membership.getDurationDays());
        memberMembershipRepository.save(newMembership);

        //결제 요청 및 결제 정보 저장
        processPayment(form.getMemberId(), membership);
    }

    private Membership getMembershipById(Long membershipId) {
        return membershipRepository.findById(membershipId)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버십을 찾을 수 없습니다: " + membershipId));
    }

    private MemberMembership getActiveMembership(Long memberId) {
        List<MemberMembership> memberMemberships = memberMembershipRepository.findByMemberIdAndStatus(memberId, MemberMembership.MembershipStatus.ACTIVE);
        if (memberMemberships.size() != 1) {
            log.info("회원 번호, 멤버십 개수 = {}, {}", memberId, memberMemberships.size());
            throw new IllegalArgumentException("멤버십 무결성 오류가 발생했습니다.");
        }
        return memberMemberships.get(0);
    }

    private void processPayment(Long memberId, Membership membership) {
        String merchantId = UUID.randomUUID().toString().substring(0, 10);
        restTemplateUtil.post("https://api.iamport.kr/payment/", new PaymentRequest(merchantId, membership.getPrice()));
        paymentRepository.save(new Payment(merchantId, memberId, membership.getId(), membership.getPrice()));
    }
}
