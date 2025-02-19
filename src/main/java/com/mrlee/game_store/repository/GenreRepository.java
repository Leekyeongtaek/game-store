package com.mrlee.game_store.repository;

import com.mrlee.game_store.domain.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
