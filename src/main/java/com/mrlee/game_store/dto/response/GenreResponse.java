package com.mrlee.game_store.dto.response;

import com.mrlee.game_store.domain.Genre;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GenreResponse {
    private Long id;
    private String code;
    private String name;

    public GenreResponse(Genre genre) {
        this.id = genre.getId();
        this.code = genre.getCode();
        this.name = genre.getName();
    }
}
