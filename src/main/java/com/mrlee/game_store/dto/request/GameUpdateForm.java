package com.mrlee.game_store.dto.request;

import com.mrlee.game_store.domain.Game;
import com.mrlee.game_store.domain.GameType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class GameUpdateForm {

    private Long id;
    @NotNull(message = "게임 그룹은 필수 선택 사항입니다.")
    private Long gameGroupId;
    @NotNull(message = "배급사는 필수 선택 사항입니다.")
    private Long publisherId;
    @NotBlank(message = "이름은 필수 입력 사항입니다.")
    private String name;
    private MultipartFile coverImage;
    private String coverImageUrl;
    @NotBlank(message = "게임 상세는 필수 입력 사항입니다.")
    private String description;
    @NotNull(message = "발매일은 필수로 입력 사항입니다.")
    private LocalDate releaseDate;
    @Min(value = 0, message = "가격은 0 원 이상만 입력 가능합니다.")
    @Max(value = 300000, message = "최대 가격은 300,000 이하만 입력 가능합니다.")
    private int price;
    @NotNull(message = "게임 타입은 필수 선택 사항입니다.")
    private GameType type;
    private List<Long> gameGenreIds = new ArrayList<>();
    private List<Long> gamePlatformIds = new ArrayList<>();
    private List<Long> gameScreenLanguageIds = new ArrayList<>();
    private List<Long> gameAudioLanguageIds = new ArrayList<>();
    private GameDiscountUpdateForm gameDiscount;
    private Boolean isDiscounted = false;

    public GameUpdateForm(Game game) {
        this.id = game.getId();
        this.gameGroupId = game.getGameGroupId();
        this.publisherId = game.getPublisherId();
        this.name = game.getName();
        this.coverImageUrl = game.getCoverImage();
        this.description = game.getDescription();
        this.releaseDate = game.getReleaseDate();
        this.price = game.getPrice();
        this.type = game.getType();
        this.gameGenreIds = game.getGenreIds();
        this.gamePlatformIds = game.getGamePlatformIds();
        this.gameScreenLanguageIds = game.getScreenLanguageIds();
        this.gameAudioLanguageIds = game.getAudioLanguageIds();
        if (game.hasDiscounted()) {
            this.gameDiscount = new GameDiscountUpdateForm(game.getGameDiscount());
            this.isDiscounted = true;
        }
    }
}
