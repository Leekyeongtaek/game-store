package com.mrlee.game_store.membership.domain;

import com.mrlee.game_store.common.AuditingDateTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_membership")
@Entity
public class MemberMembership extends AuditingDateTime {

    public enum MembershipStatus {ACTIVE, CANCELLED, EXPIRED}

    @Id
    private Long id;
    @Column(name = "member_id", nullable = false)
    private Long memberId;
    @Column(name = "membership_id", nullable = false)
    private Long membershipId;
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    @Enumerated(EnumType.STRING)
    private MembershipStatus status;
}
