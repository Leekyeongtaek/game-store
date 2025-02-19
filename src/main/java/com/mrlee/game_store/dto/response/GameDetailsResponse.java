package com.mrlee.game_store.dto.response;

import com.mrlee.game_store.common.MyUtil;
import com.mrlee.game_store.domain.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Data
@NoArgsConstructor
public class GameDetailsResponse {

    private Long id;
    private String name;
    private String price;
    private String releaseDate;
    private String coverImage;
    private String description;
    private GameType type;
    private GameDiscountResponse gameDiscount;
    private PublisherResponse publisher;
    private List<PlatformResponse> platforms;
    private List<GenreResponse> genres;
    private List<LanguageResponse> screenLanguages;
    private List<LanguageResponse> audioLanguages;
    private List<GameEditionResponse> editions;
    private List<GameAdditionalContentResponse> additionalContents;
    private String platformsStr;
    private String genresStr;
    private String audiosLanguagesStr;
    private String screenLanguagesStr;

    public GameDetailsResponse(Game game, Publisher publisher, List<Game> games) {
        this.id = game.getId();
        this.name = game.getName();
        this.price = NumberFormat.getInstance(Locale.KOREA).format(game.getPrice());
        this.releaseDate = MyUtil.toFormattedDate(game.getReleaseDate());
        this.coverImage = game.getCoverImage();
        this.description = game.getDescription().replace("\n", "<br>");
        this.type = game.getType();
        this.publisher = new PublisherResponse(publisher);
        this.platforms = convertPlatforms(game);
        this.genres = convertGenres(game);
        this.screenLanguages = convertScreenLanguages(game);
        this.audioLanguages = convertAudioLanguages(game);
        this.editions = convertGameEditionResponses(games);
        this.additionalContents = convertGameAdditionalContentResponses(games);
        this.genresStr = toGenresString();
        this.platformsStr = toPlatformsString();
        this.screenLanguagesStr = toScreenLanguagesString();
        this.audiosLanguagesStr = toAudioLanguagesString();
        if (game.hasDiscounted()) {
            this.gameDiscount = new GameDiscountResponse(game.getGameDiscount());
        }
    }

    //todo noneMatch는 스트림 전체에 대해 특정 조건을 만족하지 않는지 확인할 때 사용하는 메소드
    /*
    * List<String> usernames = List.of("alice", "bob", "charlie");
        String newUsername = "david";

        // 새로운 사용자 이름이 이미 존재하는지 검사
        boolean isUnique = usernames.stream()
            .noneMatch(username -> username.equals(newUsername));

        if (isUnique) {
            System.out.println("Username is unique!");
        } else {
            System.out.println("Username already exists!");
        }
    * */
    //todo QueryFactory에서 데이터 목록 조회시 Null 객체가 아닌 빈 리스트를 반환해줌
    private List<GameEditionResponse> convertGameEditionResponses(List<Game> games) {
        return games.stream().filter(e -> e.getType().equals(GameType.PRODUCT))
                .map(GameEditionResponse::new)
                .toList();
    }

    private List<GameAdditionalContentResponse> convertGameAdditionalContentResponses(List<Game> games) {
        return games.stream()
                .filter(e -> !e.getType().equals(GameType.PRODUCT))
                .map(GameAdditionalContentResponse::new)
                .toList();
    }

    private List<PlatformResponse> convertPlatforms(Game game) {
        return game.getGamePlatforms().stream()
                .map(GamePlatform::getPlatform)
                .map(PlatformResponse::new)
                .toList();
    }

    private List<GenreResponse> convertGenres(Game game) {
        return game.getGameGenres().stream()
                .map(GameGenre::getGenre)
                .map(GenreResponse::new)
                .toList();
    }

    private List<LanguageResponse> convertScreenLanguages(Game game) {
        return game.getGameScreenLanguages().stream()
                .map(GameScreenLanguage::getLanguage)
                .map(LanguageResponse::new)
                .toList();
    }

    private List<LanguageResponse> convertAudioLanguages(Game game) {
        return game.getGameAudioLanguages().stream()
                .map(GameAudioLanguage::getLanguage)
                .map(LanguageResponse::new)
                .toList();
    }

    private String toPlatformsString() {
        if (platforms != null && !platforms.isEmpty()) {
            return String.join(", ", platforms.stream().map(PlatformResponse::getName).toList());
        } else {
            return null;
        }
    }

    private String toGenresString() {
        if (genres != null && !genres.isEmpty()) {
            return String.join(", ", genres.stream().map(GenreResponse::getName).toList());
        } else {
            return null;
        }
    }

    private String toScreenLanguagesString() {
        if (screenLanguages != null && !screenLanguages.isEmpty()) {
            return String.join(", ", screenLanguages.stream().map(LanguageResponse::getName).toList());
        } else {
            return null;
        }
    }

    private String toAudioLanguagesString() {
        if (audioLanguages != null && !audioLanguages.isEmpty()) {
            return String.join(", ", audioLanguages.stream().map(LanguageResponse::getName).toList());
        } else {
            return null;
        }
    }
}
