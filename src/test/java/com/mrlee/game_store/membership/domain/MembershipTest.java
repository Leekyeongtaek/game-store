package com.mrlee.game_store.membership.domain;

import com.mrlee.game_store.fixture.TestFixtures;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class MembershipTest {

    //멤버십_하루_사용료는_멤버십_가격_나누기_멤버십_기간을_통해_구할_수_있다
    @Test
    void 멤버십_하루_사용료를_계산할_수_있다() {
        //given
        Membership bronzeMembership = TestFixtures.getBronzeMembership();

        //when
        long dailyUsageFee = bronzeMembership.calculateDailyUsageFee();

        //then
        //int expectedFee = 10000 / 30; // 나눗셈 연산 후 소수점을 버리는(내림 처리)
        int expectedFee = bronzeMembership.getPrice() / bronzeMembership.getDurationDays();
        Assertions.assertThat(dailyUsageFee).isEqualTo(expectedFee);
    }
}