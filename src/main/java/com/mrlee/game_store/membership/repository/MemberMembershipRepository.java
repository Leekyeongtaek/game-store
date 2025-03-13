package com.mrlee.game_store.membership.repository;

import com.mrlee.game_store.membership.domain.MemberMembership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberMembershipRepository extends JpaRepository<MemberMembership, Long> {
}
