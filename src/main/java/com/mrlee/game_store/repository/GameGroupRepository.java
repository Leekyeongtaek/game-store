package com.mrlee.game_store.repository;

import com.mrlee.game_store.domain.GameGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameGroupRepository extends JpaRepository<GameGroup, Long> {
}
