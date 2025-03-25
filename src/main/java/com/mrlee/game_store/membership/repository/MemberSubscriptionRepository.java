package com.mrlee.game_store.membership.repository;

import com.mrlee.game_store.membership.domain.MemberSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberSubscriptionRepository extends JpaRepository<MemberSubscription, Long> {

    //JPQL로 작성시 엔티티명 변경 또는 필드명이 변경되는 경우 런타임 예외가 발생할 가능성이 높다
    @Query(value = "SELECT ms FROM MemberSubscription ms WHERE ms.memberId = :memberId AND ms.status = :status ORDER BY ms.id DESC")
    List<MemberSubscription> findByMemberIdAndStatus(@Param("memberId") Long memberId, @Param("status") MemberSubscription.MembershipStatus status);

}
