package com.mrlee.game_store.fixture;

import com.mrlee.game_store.membership.domain.MemberSubscription;
import com.mrlee.game_store.membership.domain.Membership;
import com.mrlee.game_store.membership.policy.RefundInfo;

import java.time.LocalDate;

public class TestFixtures {

    public static Membership getFreeMembership() {
        return Membership.builder()
                .name(Membership.MembershipType.FREE)
                .price(0)
                .durationDays(-1) // -1이 "무제한"을 의미하는 이유는, 많은 시스템에서 -1을 “제한 없음”이나 “끝이 없음”을 나타내는 값으로 사용하기 때문
                .build();
    }

    public static Membership getBronzeMembership() {
        return Membership.builder()
                .name(Membership.MembershipType.BRONZE)
                .price(10000)
                .durationDays(30)
                .build();
    }

    public static Membership getSilverMembership() {
        return Membership.builder()
                .name(Membership.MembershipType.SILVER)
                .price(20000)
                .durationDays(30)
                .build();
    }

    public static Membership getGoldMembership() {
        return Membership.builder()
                .name(Membership.MembershipType.GOLD)
                .price(30000)
                .durationDays(30)
                .build();
    }

    public static MemberSubscription createSubscription(Long memberId, Membership membership, LocalDate startDate) {
        return MemberSubscription.builder()
                .memberId(memberId)
                .membership(membership)
                .startDate(startDate)
                .build();
    }

    /**
     * RefundInfo 객체를 생성한다
     *
     * @param fullRefundAmount 전체 환불 금액을 의미합니다.
     *                         예) 전액 환불이 가능한 경우의 환불 예상 금액.
     * @param usedDays         사용일수를 의미합니다.
     *                         예) 멤버십을 사용한 총 일수.
     * @param dailyUsageFee    일일 사용 요금을 의미합니다.
     *                         예) 하루 사용 시 차감되는 금액.
     * @return 생성된 RefundInfo 객체.
     */
    public static RefundInfo createRefundInfo(long fullRefundAmount, long usedDays, long dailyUsageFee) {
        return RefundInfo.builder()
                .fullRefundAmount(fullRefundAmount)
                .usedDays(usedDays)
                .dailyUsageFee(dailyUsageFee)
                .build();
    }
}
