package com.mrlee.game_store.membership.domain;

import com.mrlee.game_store.common.AuditingDateTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment")
@Entity
public class Payment extends AuditingDateTime {

    public enum PaymentMethod {CARD}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;
    @Column(name = "merchant_id", nullable = false)
    private String merchantId;
    @Column(name = "member_id", nullable = false)
    private Long memberId;
    @Column(name = "membership_id", nullable = false)
    private Long membershipId;
    @Column(name = "amount", nullable = false)
    private int amount;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    public Payment(String merchantId, Long memberId, Long membershipId, int amount) {
        this.merchantId = merchantId;
        this.memberId = memberId;
        this.membershipId = membershipId;
        this.amount = amount;
        this.paymentMethod = PaymentMethod.CARD;
        this.paymentDate = LocalDateTime.now();
    }
}
