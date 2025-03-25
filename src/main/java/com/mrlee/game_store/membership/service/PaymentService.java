package com.mrlee.game_store.membership.service;

import com.mrlee.game_store.membership.domain.Membership;
import com.mrlee.game_store.membership.domain.Payment;
import com.mrlee.game_store.membership.iamport.PaymentRequest;
import com.mrlee.game_store.membership.repository.PaymentRepository;
import com.mrlee.game_store.membership.util.RestTemplateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RestTemplateUtil restTemplateUtil;

    public Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 결제 정보를 찾을 수 없습니다."));
    }

    public Long processPayment(Long memberId, Membership membership) {
        String merchantId = createMerchantId();
        restTemplateUtil.post("https://api.iamport.kr/payment/", new PaymentRequest(merchantId, membership.getPrice()));
        return paymentRepository.save(new Payment(merchantId, memberId, membership.getId(), membership.getPrice())).getId();
    }

    private String createMerchantId() {
        return UUID.randomUUID().toString().substring(0, 10);
    }
}
