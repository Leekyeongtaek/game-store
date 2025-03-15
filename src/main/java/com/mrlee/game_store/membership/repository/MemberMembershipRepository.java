package com.mrlee.game_store.membership.repository;

import com.mrlee.game_store.membership.domain.MemberMembership;
import com.mrlee.game_store.membership.service.MembershipInitService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberMembershipRepository extends JpaRepository<MemberMembership, Long> {

    @Query(value = "SELECT mms FROM MemberMembership mms WHERE mms.memberId = :memberId AND mms.status = :status ORDER BY mms.id DESC")
    List<MemberMembership> findByMemberIdAndStatus(@Param("memberId") Long memberId, @Param("status") MemberMembership.MembershipStatus status);

}
