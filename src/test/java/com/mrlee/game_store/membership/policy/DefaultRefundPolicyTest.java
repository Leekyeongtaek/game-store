package com.mrlee.game_store.membership.policy;

import com.mrlee.game_store.fixture.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DefaultRefundPolicyTest {

    private RefundPolicy defaultRefundPolicy;

    @BeforeEach
    public void setUp() {
        defaultRefundPolicy = new DefaultRefundPolicy();
    }

    @Test
    void 환불_가능_기간일_경우는_예외가_발생하지_않는다() {
        //when & then
        assertDoesNotThrow(() -> defaultRefundPolicy.validateRefundable(0));
        assertDoesNotThrow(() -> defaultRefundPolicy.validateRefundable(10));
        assertDoesNotThrow(() -> defaultRefundPolicy.validateRefundable(15));
    }

    @Test
    void 환불_가능_기간_이후일_경우는_예외가_발생한다() {
        //when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> defaultRefundPolicy.validateRefundable(16));

        //then
        assertEquals("15일 경과후에는 환불 요청이 불가능합니다.", exception.getMessage());
    }

    @Test
    void 전액_환불_가능_기간인_경우_전액_환불_해준다() {
        //given
        RefundInfo refundInfo = TestFixtures.createRefundInfo(20000, 2, 666);

        //when
        long refundAmount = defaultRefundPolicy.calculateRefundAmount(refundInfo);
        long fullRefundAmount = refundInfo.getFullRefundAmount();

        //then
        assertThat(refundAmount).isEqualTo(fullRefundAmount);
    }

    @Test
    void 전액_환불_기간이_경과하면_부분_환불_해준다() {
        //given
        RefundInfo refundInfo = TestFixtures.createRefundInfo(20000, 10, 666);

        //when
        long refundAmount = defaultRefundPolicy.calculateRefundAmount(refundInfo);
        long expectedRefundAmount = refundInfo.getFullRefundAmount() - (refundInfo.getDailyUsageFee() * refundInfo.getUsedDays());

        //then
        assertThat(refundAmount).isEqualTo(expectedRefundAmount);
    }

    @Test
    void 환불_프로세스_정상_케이스() {
        //given
        RefundInfo refundInfo = TestFixtures.createRefundInfo(20000, 2, 666);

        //when
        long refundAmount = defaultRefundPolicy.processRefundAmount(refundInfo);

        //then
        assertThat(refundAmount).isEqualTo(refundInfo.getFullRefundAmount());
    }

    @Test
    void 환불_프로세스_환불_예상금액이_0원_이하인_경우_예외발생() {
        //given
        RefundInfo refundInfo = TestFixtures.createRefundInfo(10000, 10, 1000);

        //when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> defaultRefundPolicy.processRefundAmount(refundInfo));
        assertThat(exception.getMessage()).isEqualTo("환불 예상 금액이 0원 이하입니다.");
    }

}