package com.mrlee.game_store.membership.policy;

public class GoldRefundPolicy implements RefundPolicy {

    private static final int NON_REFUNDABLE_AFTER = 20;

    @Override
    public void validateRefundable(long usedDays) {
        if (usedDays > NON_REFUNDABLE_AFTER) {
            throw new IllegalArgumentException(NON_REFUNDABLE_AFTER + "일 경과후에는 환불 요청이 불가능합니다.");
        }
    }

    @Override
    public long calculateRefundAmount(RefundInfo refundInfo) {
        if (isFullRefundable(refundInfo.getUsedDays()) == RefundType.FULL) { //enum은 싱글턴이라 == 비교가 더 안전하고 성능도 좋다, 안전하다는 의미는 NPE가 발생하지 않음
            return refundInfo.getFullRefundAmount();
        }
        return Math.max(0, refundInfo.getFullRefundAmount() - (refundInfo.getDailyUsageFee() * refundInfo.getUsedDays()));
    }
}
