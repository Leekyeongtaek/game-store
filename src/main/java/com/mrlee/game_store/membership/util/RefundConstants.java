package com.mrlee.game_store.membership.util;

// @Component: 이 클래스는 데이터베이스나 외부 API에 의존하지 않으므로, static 유틸리티 클래스로 변경하는 것이 적절
// 내부 상태가 없는 유틸성 클래스(변경 가능한 멤버 변수가 존재하지 않음)
public class RefundConstants {

    //static: 메모리 할당 방식에 차이가 있다.
    //클래스가 로딩될 때 한 번만 메모리에 할당(메서드 영역)
    //public static final int FULL_REFUND_PERIOD = 7; // 7일 이내인 경우 100% 환불
    //public static final int NON_REFUNDABLE_AFTER = 15; // 15일 경과시 환불 불가
}
