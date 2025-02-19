package com.mrlee.game_store.dto.response;

import com.mrlee.game_store.domain.Platform;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlatformResponse {
    private Long id;
    private String name;

    public PlatformResponse(Platform platform) {
        this.id = platform.getId();
        this.name = platform.getName();
    }
}
