package com.mrlee.game_store.dto.response;

import com.mrlee.game_store.domain.Game;
import com.mrlee.game_store.domain.GamePlatform;
import com.mrlee.game_store.domain.GameType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Data
@NoArgsConstructor
public class GameEditionResponse {
    private Long id;
    private String name;
    private String coverImage;
    private String price;
    private GameDiscountResponse gameDiscount;
    private String typeName;
    private List<PlatformResponse> platforms;

    public GameEditionResponse(Game game) {
        this.id = game.getId();
        this.name = game.getName();
        this.coverImage = game.getCoverImage();
        this.price = NumberFormat.getInstance(Locale.KOREA).format(game.getPrice());
        if (game.hasDiscounted()) {
            this.gameDiscount = new GameDiscountResponse(game.getGameDiscount());
        }
        this.typeName = game.getType().getKoreanName();
        this.platforms = game.getGamePlatforms().stream()
                .map(GamePlatform::getPlatform)
                .map(PlatformResponse::new).toList();
    }
}
