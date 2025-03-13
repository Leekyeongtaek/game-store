package com.mrlee.game_store.membership.domain;

import com.mrlee.game_store.common.AuditingDateTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
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
}
