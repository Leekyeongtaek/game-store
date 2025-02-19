package com.mrlee.game_store.repository;

import com.mrlee.game_store.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {

    @Query("SELECT g FROM Game g LEFT JOIN FETCH g.gameDiscount WHERE g.id = :gameId")
    Optional<Game> findByIdJoinFetchGameDiscount(@Param("gameId") Long gameId);
}
