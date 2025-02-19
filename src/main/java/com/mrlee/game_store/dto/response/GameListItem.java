package com.mrlee.game_store.dto.response;

import com.mrlee.game_store.domain.Game;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.NumberFormat;
import java.util.Locale;

@Data
@NoArgsConstructor
public class GameListItem {
    private Long id;
    private String name;
    private String price;
    private String typeName;

    @QueryProjection
    public GameListItem(Game game) {
        this.id = game.getId();
        this.name = game.getName();
        this.price = NumberFormat.getInstance(Locale.KOREA).format(game.getPrice());
        this.typeName = game.getType().getKoreanName();
    }
}
