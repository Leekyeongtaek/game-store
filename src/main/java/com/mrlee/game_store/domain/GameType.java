package com.mrlee.game_store.domain;

import lombok.Getter;

@Getter
public enum GameType {
    PRODUCT("제품판"), DLC("추가 콘텐츠"), ITEM("아이템"),
    COSTUME("의상"), CHARACTER("캐릭터");

    private final String koreanName;

    GameType(String koreanName) {
        this.koreanName = koreanName;
    }
}
