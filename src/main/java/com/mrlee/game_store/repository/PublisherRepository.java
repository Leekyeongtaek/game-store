package com.mrlee.game_store.repository;

import com.mrlee.game_store.domain.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
}
