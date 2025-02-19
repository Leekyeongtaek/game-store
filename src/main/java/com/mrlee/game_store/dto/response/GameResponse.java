package com.mrlee.game_store.dto.response;

import com.mrlee.game_store.domain.Game;
import com.mrlee.game_store.domain.GameDiscount;
import com.mrlee.game_store.domain.GameType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@NoArgsConstructor
@Data
public class GameResponse {

    private Long id;
    private Long gameGroupId;
    private Long publisherId;
    private String name;
    private String coverImage;
    private String description;
    private LocalDate releaseDate;
    private String price;
    private GameType type;
    private GameDiscountResponse gameDiscount;
    private List<Long> gameGenreIds;
    private List<Long> gamePlatformIds;
    private List<Long> gameAudioLanguageIds;
    private List<Long> gameScreenLanguageIds;

    public GameResponse(Game game) {
        this.id = game.getId();
        this.gameGroupId = game.getGameGroupId();
        this.publisherId = game.getPublisherId();
        this.name = game.getName();
        this.price = NumberFormat.getInstance(Locale.KOREA).format(game.getPrice());
        this.releaseDate = game.getReleaseDate();
        this.coverImage = game.getCoverImage();
        this.description = game.getDescription();
        this.type = game.getType();
        if (game.hasDiscounted()) {
            this.gameDiscount = new GameDiscountResponse(game.getGameDiscount());
        }
        this.gameGenreIds = game.getGenreIds();
        this.gamePlatformIds = game.getGamePlatformIds();
        this.gameAudioLanguageIds = game.getAudioLanguageIds();
        this.gameScreenLanguageIds = game.getScreenLanguageIds();
    }
}
