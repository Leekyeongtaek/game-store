package com.mrlee.game_store.membership.policy;

import com.mrlee.game_store.membership.domain.MemberSubscription;
import com.mrlee.game_store.membership.domain.Membership;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class RefundInfo {

    private long fullRefundAmount;
    private long usedDays;
    private long dailyUsageFee;

    //getter가 단순한 값을 반환하는 경우는 getter 메서드 호출하고, 객체를 반환하는 경우 꺼내서 사용하면 가독성, 유지보수성 향상
    public RefundInfo(MemberSubscription memberSubscription) {
        Membership membership = memberSubscription.getMembership();
        this.usedDays = memberSubscription.calculateUsedDays(LocalDate.now());
        this.fullRefundAmount = membership.getFullRefundAmount();
        this.dailyUsageFee = membership.calculateDailyUsageFee();
    }

    @Builder
    public RefundInfo(long fullRefundAmount, long usedDays, long dailyUsageFee) {
        this.fullRefundAmount = fullRefundAmount;
        this.usedDays = usedDays;
        this.dailyUsageFee = dailyUsageFee;
    }
}

//    public long calculateRefundAmount(RefundType refundType) {
//        if (refundType == RefundType.FULL) {
//            return fullRefundAmount;
//        }
//        return Math.max(0, fullRefundAmount - (dailyUsageFee * usedDays));
//    }

//  isFullRefund 기본 변수 -> enum 변경
//    public long calculateRefundAmount(boolean isFullRefund) {
//        if (isFullRefund) {
//            return originPrice;
//        }
//        return Math.max(0, originPrice - (dailyUsageFee * usedDays));
//    }
