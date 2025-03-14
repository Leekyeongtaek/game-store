package com.mrlee.game_store.membership.repository;

import com.mrlee.game_store.membership.domain.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRepository extends JpaRepository<Refund, Long> {
}
