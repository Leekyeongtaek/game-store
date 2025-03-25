package com.mrlee.game_store.membership.policy;

public class DefaultRefundPolicy implements RefundPolicy {

    private static final int NON_REFUNDABLE_AFTER = 15;

    @Override
    public void validateRefundable(long usedDays) {
        if (usedDays > NON_REFUNDABLE_AFTER) {
            throw new IllegalArgumentException(NON_REFUNDABLE_AFTER + "일 경과후에는 환불 요청이 불가능합니다.");
        }
    }

    @Override
    public long calculateRefundAmount(RefundInfo refundInfo) {
        if (isFullRefundable(refundInfo.getUsedDays()) == RefundType.FULL) {
            return refundInfo.getFullRefundAmount();
        }
        return Math.max(0, refundInfo.getFullRefundAmount() - (refundInfo.getDailyUsageFee() * refundInfo.getUsedDays()));
    }
}
