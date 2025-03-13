package com.mrlee.game_store.membership.repository;

import com.mrlee.game_store.membership.domain.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
}
