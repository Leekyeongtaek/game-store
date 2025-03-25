package com.mrlee.game_store.membership.domain;

import com.mrlee.game_store.common.AuditingDateTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_subscription")
@Entity
public class MemberSubscription extends AuditingDateTime {

    public enum MembershipStatus {ACTIVE, CANCELLED, EXPIRED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_subscription_id")
    private Long id;
    @Column(name = "member_id", nullable = false)
    private Long memberId;
    @Column(name = "payment_id")
    private Long paymentId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "membership_id")
    private Membership membership;
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    private MembershipStatus status;

    //기존 생성자가 @Builder 생성자를 재사용하도록 변경
    //테스트에서 startDate, endDate, status를 직접 설정 가능 (유연성 증가)
    public static MemberSubscription createMemberSubscription(Long memberId, Membership membership) {
        return new MemberSubscription(memberId, membership, null, null, null, null);
    }

    public static MemberSubscription createPaidMemberSubscription(Long memberId, Membership membership, Long paymentId) {
        return new MemberSubscription(memberId, membership, paymentId, null, null, null);
    }

    //빌더 패턴 적용시 기본 생성자는 유지하고, 빌더용 생성자는 명시적으로 만들어야 한다”
    //@Builder 롬복을 통해 생성시 접근 제어자를 'private'로 설정해도 'public'으로 생성된다
    @Builder
    private MemberSubscription(Long memberId, Membership membership, Long paymentId,
                               LocalDate startDate, LocalDate endDate, MembershipStatus status) {
        this.memberId = memberId;
        this.paymentId = paymentId;
        this.membership = membership;
        this.startDate = (startDate != null) ? startDate : LocalDate.now();
        this.endDate = (endDate != null) ? endDate : this.startDate.plusDays(membership.getDurationDays());
        this.status = (status != null) ? status : MembershipStatus.ACTIVE;
    }

    // 기존 cancel() 메서드의 개선점: 멤버 구독 상태 변경과 검증이라는 2가지 역할을 수행함.
//    public void cancel() {
//        if (status.equals(MembershipStatus.CANCELLED)) {
//            throw new IllegalArgumentException("무료 멤버십 구독은 취소 요청이 불가능합니다.");
//        }
//        this.status = MembershipStatus.CANCELLED;
//    }

    public void cancelSubscription() {
        if (!isCancel()) {
            throw new IllegalArgumentException("멤버십 취소 가능한 상태가 아닙니다.");
        }
        this.status = MembershipStatus.CANCELLED;
    }

    // 활성화중인 멤버십만 취소 상태로 변경이 가능
    private boolean isCancel() {
        return this.status == MembershipStatus.ACTIVE;
    }

    public long calculateUsedDays(LocalDate refundRequestDate) {
        return ChronoUnit.DAYS.between(startDate, refundRequestDate);
    }
}
