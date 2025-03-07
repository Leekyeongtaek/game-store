package com.mrlee.game_store.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mrlee.game_store.common.AuditingDateTime;
import com.mrlee.game_store.common.ImageUploadUtil;
import com.mrlee.game_store.common.MyUtil;
import com.mrlee.game_store.dto.request.GameDiscountUpdateForm;
import com.mrlee.game_store.dto.request.GameUpdateForm;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//todo private List<GameGenre> gameGenres = new ArrayList<>(); NPE 방지하기 아주 좋은 코드!
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "game")
@Entity
public class Game extends AuditingDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long id;

    @Column(name = "game_group_id")
    private Long gameGroupId;

    @Column(name = "publisher_id")
    private Long publisherId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private GameType type;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private int price;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "cover_image")
    private String coverImage;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<GameGenre> gameGenres = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<GamePlatform> gamePlatforms = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<GameScreenLanguage> gameScreenLanguages = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<GameAudioLanguage> gameAudioLanguages = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "game_discount_id")
    private GameDiscount gameDiscount;

    @Builder
    public Game(Long gameGroupId, Long publisherId, GameType type, String name, int price, LocalDate releaseDate, String coverImage, String description) {
        this.gameGroupId = gameGroupId;
        this.publisherId = publisherId;
        this.type = type;
        this.name = name;
        this.price = price;
        this.releaseDate = releaseDate;
        this.coverImage = coverImage;
        this.description = description;
        this.gameGenres = new ArrayList<>();
        this.gamePlatforms = new ArrayList<>();
        this.gameScreenLanguages = new ArrayList<>();
        this.gameAudioLanguages = new ArrayList<>();
    }

    public void update(GameUpdateForm form) {
        this.gameGroupId = form.getGameGroupId();
        this.publisherId = form.getPublisherId();
        this.name = form.getName();
        this.price = form.getPrice();
        if (!form.getCoverImage().isEmpty() && form.getCoverImage() != null) {
            this.coverImage = MyUtil.uploadImageFile(form.getCoverImage());
        }
        this.type = form.getType();
        this.description = form.getDescription();
        this.releaseDate = form.getReleaseDate();
    }

    public void addGameDiscount(GameDiscount gameDiscount) {
        this.gameDiscount = gameDiscount;
    }

    public void removeGameDiscount() {
        this.gameDiscount = null;
    }

    public void addGameGenre(GameGenre gameGenre) {
        gameGenres.add(gameGenre);
        gameGenre.addGame(this);
    }

    public void removeGameGenre(GameGenre gameGenre) {
        gameGenres.remove(gameGenre);
        gameGenre.addGame(null);
    }

    public void addGamePlatform(GamePlatform gamePlatform) {
        gamePlatforms.add(gamePlatform);
        gamePlatform.addGame(this);
    }

    public void removeGamePlatform(GamePlatform gamePlatform) {
        gamePlatforms.remove(gamePlatform);
        gamePlatform.addGame(null);
    }

    public void addGameScreenLanguage(GameScreenLanguage gameScreenLanguage) {
        gameScreenLanguages.add(gameScreenLanguage);
        gameScreenLanguage.addGame(this);
    }

    public void removeGameScreenLanguage(GameScreenLanguage gameScreenLanguage) {
        gameScreenLanguages.remove(gameScreenLanguage);
        gameScreenLanguage.addGame(null);
    }

    public void addGameAudioLanguage(GameAudioLanguage gameAudioLanguage) {
        gameAudioLanguages.add(gameAudioLanguage);
        gameAudioLanguage.addGame(this);
    }

    public void removeGameAudioLanguage(GameAudioLanguage gameAudioLanguage) {
        gameAudioLanguages.remove(gameAudioLanguage);
        gameAudioLanguage.addGame(null);
    }

    public List<Long> getGenreIds() {
        return gameGenres
                .stream()
                .map(e -> e.getGenre().getId())
                .toList();
    }

    public List<Long> getGamePlatformIds() {
        return gamePlatforms
                .stream()
                .map(e -> e.getPlatform().getId())
                .toList();
    }

    public List<Long> getAudioLanguageIds() {
        return gameAudioLanguages.stream()
                .map(audioLanguage -> audioLanguage.getLanguage().getId())
                .toList();
    }

    public List<Long> getScreenLanguageIds() {
        return gameScreenLanguages.stream()
                .map(screenLanguage -> screenLanguage.getLanguage().getId())
                .toList();
    }

    public boolean hasDiscounted() {
        return gameDiscount != null;
    }

    public boolean hasGameGenres() {
        return !gameGenres.isEmpty();
    }

    public boolean hasGamePlatforms() {
        return !gamePlatforms.isEmpty();
    }

    public boolean hasGameScreenLanguages() {
        return !gameScreenLanguages.isEmpty();
    }

    public boolean hasGameAudioLanguages() {
        return !gameAudioLanguages.isEmpty();
    }

    public void applyGameDiscount(Boolean isDiscounted, GameDiscountUpdateForm form) {
        if (isDiscounted != null && isDiscounted) {
            if (hasDiscounted()) {
                gameDiscount.update(form);
            } else {
                addGameDiscount(form.toEntity());
            }
        } else {
            removeGameDiscount();
        }
    }
}