package com.mrlee.game_store.membership.repository;

import com.mrlee.game_store.membership.domain.Membership;
import com.mrlee.game_store.membership.domain.Membership.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    Optional<Membership> findByName(MembershipType name);
}
