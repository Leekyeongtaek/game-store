package com.mrlee.game_store.repository;

import com.mrlee.game_store.domain.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformRepository extends JpaRepository<Platform, Long> {
}
