package com.mrlee.game_store.membership.dto.request;

import com.mrlee.game_store.membership.domain.Payment;
import lombok.Getter;

@Getter
public class RefundRequest {

    private Long paymentId;
    private String merchantId;
    private long refundAmount;
    private String reason;

    public RefundRequest(Payment payment, long refundAmount, String reason) {
        this.paymentId = payment.getId();
        this.merchantId = payment.getMerchantId();
        this.refundAmount = refundAmount;
        this.reason = reason;
    }
}
