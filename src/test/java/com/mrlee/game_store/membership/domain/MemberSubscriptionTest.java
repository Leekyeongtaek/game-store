package com.mrlee.game_store.membership.domain;

import com.mrlee.game_store.fixture.TestFixtures;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class MemberSubscriptionTest {

    //Should-When 패턴
    //회원_구독_기간은_구독_시작일로부터_요청일_차이를_통해_구할_수_있다.
    @Test
    void 구독_사용_일수를_계산할_수_있다() {
        //given
        Membership bronzeMembership = TestFixtures.getBronzeMembership();
        LocalDate startDate = LocalDate.of(2025, 3, 10);
        LocalDate requestDate = LocalDate.of(2025, 3, 20);

        MemberSubscription memberSubscription = TestFixtures.createSubscription(
                1L, bronzeMembership, startDate);

        //when
        long usedDays = memberSubscription.calculateUsedDays(requestDate);

        //then
        assertThat(usedDays).isEqualTo(10);
    }

}