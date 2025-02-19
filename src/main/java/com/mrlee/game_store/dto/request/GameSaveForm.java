package com.mrlee.game_store.dto.request;

import com.mrlee.game_store.common.MyUtil;
import com.mrlee.game_store.domain.Game;
import com.mrlee.game_store.domain.GameType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class GameSaveForm {

    @NotNull(message = "게임 그룹은 필수 선택 사항입니다.")
    private Long gameGroupId;
    @NotNull(message = "배급사는 필수 선택 사항입니다.")
    private Long publisherId;
    @NotBlank(message = "이름은 필수 입력 사항입니다.")
    private String name;
    @NotBlank(message = "게임 상세는 필수 입력 사항입니다.")
    private String description;
    private MultipartFile coverImage;
    @NotNull(message = "발매일은 필수로 입력 사항입니다.")
    private LocalDate releaseDate;
    @Min(value = 0, message = "가격은 0 원 이상만 입력 가능합니다.")
    @Max(value = 300000, message = "최대 가격은 300,000 이하만 입력 가능합니다.")
    private int price;
    @NotNull(message = "게임 타입은 필수 선택 사항입니다.")
    private GameType type;
    private Boolean isDiscounted = false;
    private GameDiscountSaveForm gameDiscount;
    private List<Long> gameGenreIds = new ArrayList<>();
    private List<Long> gamePlatformIds = new ArrayList<>();
    private List<Long> gameScreenLanguageIds = new ArrayList<>();
    private List<Long> gameAudioLanguageIds = new ArrayList<>();

    public Game toEntity() {
        Game game = Game.builder()
                .gameGroupId(gameGroupId)
                .publisherId(publisherId)
                .name(name)
                .price(price)
                .coverImage(MyUtil.uploadImageFile(coverImage))
                .description(description)
                .type(type)
                .releaseDate(releaseDate)
                .build();

        if (isDiscounted) {
            game.addGameDiscount(gameDiscount.toEntity());
        }

        return game;
    }
}
