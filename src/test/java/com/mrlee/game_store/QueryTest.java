package com.mrlee.game_store;

import com.mrlee.game_store.dto.response.GameDetailsResponse;
import com.mrlee.game_store.repository.query.GameQueryRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@SpringBootTest
public class QueryTest {

    @Autowired
    private GameQueryRepository gameQueryRepository;

    @Test
    void gameDetailsTest() {
        Long gameId = 75L;
        GameDetailsResponse gameDetails = gameQueryRepository.getGameDetails(gameId);
        log.info("gameDetails = {}", gameDetails);
    }
}
