package com.mrlee.game_store.membership.policy;

public interface RefundPolicy {

    void validateRefundable(long usedDays);

    long calculateRefundAmount(RefundInfo refundInfo);

    default RefundType isFullRefundable(long usedDays) {
        if (usedDays < 3) {
            return RefundType.FULL;
        }
        return RefundType.PARTIAL;
    }

    default void validateRefundAmount(long refundAmount) {
        if (refundAmount <= 0) {
            throw new IllegalArgumentException("환불 예상 금액이 0원 이하입니다.");
        }
    }

    default long processRefundAmount(RefundInfo refundInfo) {
        validateRefundable(refundInfo.getUsedDays());
        long refundAmount = calculateRefundAmount(refundInfo);
        validateRefundAmount(refundAmount);
        return refundAmount;
    }
}
