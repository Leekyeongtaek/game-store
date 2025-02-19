package com.mrlee.game_store.dto.response;

import com.mrlee.game_store.domain.Language;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LanguageResponse {
    private Long id;
    private String name;

    public LanguageResponse(Language language) {
        this.id = language.getId();
        this.name = language.getName();
    }
}
