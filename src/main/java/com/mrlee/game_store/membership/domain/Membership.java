package com.mrlee.game_store.membership.domain;

import com.mrlee.game_store.common.AuditingDateTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "membership")
@Entity
public class Membership extends AuditingDateTime {

    public enum MembershipType {FREE, BRONZE, SILVER, GOLD}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "membership_id")
    private Long id;
    @Enumerated(EnumType.STRING)
    private MembershipType name;
    @Column(name = "price")
    private int price;
    @Column(name = "duration_days")
    private int durationDays;

    @Builder
    public Membership(MembershipType name, int price, int durationDays) {
        this.name = name;
        this.price = price;
        this.durationDays = durationDays;
    }

    public long calculateDailyUsageFee() {
        return price / durationDays;
    }

    //getPrice() -> "이 멤버십의 원래 가격은 얼마인가?"
    //getFullRefundAmount() -> “이 멤버십을 환불하면 얼마를 돌려줄 수 있는가?”
    //만약 “환불 정책”이 바뀌면?, “환불 수수료 10% 차감” 정책이 생긴다면? price * 0.9
    public long getFullRefundAmount() {
        return price;
    }
}
