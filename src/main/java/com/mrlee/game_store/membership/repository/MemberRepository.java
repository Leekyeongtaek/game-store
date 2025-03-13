package com.mrlee.game_store.membership.repository;

import com.mrlee.game_store.membership.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
