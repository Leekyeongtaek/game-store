package com.mrlee.game_store.dto.response;

import com.mrlee.game_store.common.MyUtil;
import com.mrlee.game_store.domain.Game;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.NumberFormat;
import java.util.*;

@NoArgsConstructor
@Data
public class GamePromotionResponse {

    private Long id;
    private String name;
    private String price;
    private String discountPrice;
    private int discountRate;
    private String coverImage;
    private String type;
    private String gamePlatforms;

    public GamePromotionResponse(Game game) {
        this.id = game.getId();
        this.name = MyUtil.truncateGameTitle(game.getName());
        this.price = NumberFormat.getInstance(Locale.KOREA).format(game.getPrice());
        this.discountPrice = NumberFormat.getInstance(Locale.KOREA).format(game.getGameDiscount().getDiscountPrice());
        this.discountRate = game.getGameDiscount().getDiscountRate();
        this.coverImage = game.getCoverImage();
        this.type = "[" + game.getType().getKoreanName() + "]";
        this.gamePlatforms = game.getGamePlatforms().stream().map(e -> e.getPlatform().getName()).toList().toString();
    }
}
