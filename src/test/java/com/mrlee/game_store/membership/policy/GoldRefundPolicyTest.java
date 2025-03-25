package com.mrlee.game_store.membership.policy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GoldRefundPolicyTest {

    private RefundPolicy goldRefundPolicy;

    @BeforeEach
    public void setUp() {
        goldRefundPolicy = new GoldRefundPolicy();
    }

    @Test
    void 환불_가능_기간일_경우는_예외가_발생하지_않는다() {
        //when & then
        assertDoesNotThrow(() -> goldRefundPolicy.validateRefundable(0));
        assertDoesNotThrow(() -> goldRefundPolicy.validateRefundable(10));
        assertDoesNotThrow(() -> goldRefundPolicy.validateRefundable(20));
    }

    @Test
    void 환불_가능_기간_이후일_경우는_예외가_발생한다() {
        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> goldRefundPolicy.validateRefundable(21));

        //then
        assertEquals("20일 경과후에는 환불 요청이 불가능합니다.", exception.getMessage());
    }
}