package com.mrlee.game_store.membership.repository;

import com.mrlee.game_store.membership.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
