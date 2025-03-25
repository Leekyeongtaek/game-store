package com.mrlee.game_store.membership.domain;

import com.mrlee.game_store.common.AuditingDateTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refund")
@Entity
public class Refund extends AuditingDateTime {

    public enum RefundStatus {COMPLETE, FAILED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refund_id")
    private Long id;
    @Column(name = "payment_id", nullable = false)
    private Long paymentId;
    @Column(name = "amount")
    private int amount;
    @Column(name = "reason", nullable = false)
    private String reason;
    @Column(name = "refund_date", nullable = false)
    private LocalDateTime refundDate;
    @Enumerated(EnumType.STRING)
    private RefundStatus status;
    @Column(name = "failure_reason")
    private String failureReason;

    public Refund(Long paymentId, int amount, String reason) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.reason = reason;
        this.refundDate = LocalDateTime.now();
        this.status = RefundStatus.COMPLETE;
    }
}
