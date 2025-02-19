package com.mrlee.game_store.dto.response;

import com.mrlee.game_store.domain.GameGroup;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GameGroupResponse {

    private Long id;
    private String name;

    public GameGroupResponse(GameGroup gameGroup) {
        this.id = gameGroup.getId();
        this.name = gameGroup.getName();
    }
}
