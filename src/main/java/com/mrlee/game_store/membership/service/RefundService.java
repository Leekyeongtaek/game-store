package com.mrlee.game_store.membership.service;

import com.mrlee.game_store.membership.domain.MemberSubscription;
import com.mrlee.game_store.membership.domain.Membership;
import com.mrlee.game_store.membership.domain.Membership.MembershipType;
import com.mrlee.game_store.membership.domain.Payment;
import com.mrlee.game_store.membership.domain.Refund;
import com.mrlee.game_store.membership.policy.RefundInfo;
import com.mrlee.game_store.membership.dto.request.RefundRequest;
import com.mrlee.game_store.membership.iamport.CancelRequest;
import com.mrlee.game_store.membership.policy.DefaultRefundPolicy;
import com.mrlee.game_store.membership.policy.GoldRefundPolicy;
import com.mrlee.game_store.membership.policy.RefundPolicy;
import com.mrlee.game_store.membership.repository.RefundRepository;
import com.mrlee.game_store.membership.util.RestTemplateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class RefundService {

    private final RestTemplateUtil restTemplateUtil;
    private final RefundRepository refundRepository;
    private final PaymentService paymentService;
    private static final Map<MembershipType, RefundPolicy> REFUND_POLICY_MAP = new HashMap<>();

    static {
        REFUND_POLICY_MAP.put(MembershipType.BRONZE, new DefaultRefundPolicy());
        REFUND_POLICY_MAP.put(MembershipType.SILVER, new DefaultRefundPolicy());
        REFUND_POLICY_MAP.put(MembershipType.GOLD, new GoldRefundPolicy());
    }

    public void processRefund(MemberSubscription memberSubscription, String reason) {
        Payment payment = paymentService.getPaymentById(memberSubscription.getPaymentId());
        long refundAmount = calculateRefundAmount(memberSubscription);
        requestRefund(new RefundRequest(payment, refundAmount, reason));
    }

    private long calculateRefundAmount(MemberSubscription memberSubscription) {
        Membership membership = memberSubscription.getMembership();
        RefundPolicy refundPolicy = getRefundPolicy(membership.getName());
        return refundPolicy.processRefundAmount(new RefundInfo(memberSubscription));
    }

    private void requestRefund(RefundRequest form) {
        restTemplateUtil.post("https://api.iamport.kr/payment/cancel/", new CancelRequest(form.getMerchantId(), form.getRefundAmount()));
        refundRepository.save(new Refund(form.getPaymentId(), (int) form.getRefundAmount(), form.getReason()));
    }

    private RefundPolicy getRefundPolicy(MembershipType membershipType) {
        return Optional.ofNullable(REFUND_POLICY_MAP.get(membershipType))
                .orElseThrow(() -> new IllegalArgumentException("환불 정책을 찾을 수 없습니다: " + membershipType));
    }
}
