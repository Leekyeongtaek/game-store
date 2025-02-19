package com.mrlee.game_store.dto.response;

import com.mrlee.game_store.domain.Publisher;
import lombok.Data;

@Data
public class PublisherResponse {
    private Long id;
    private String name;

    public PublisherResponse(Publisher publisher) {
        this.id = publisher.getId();
        this.name = publisher.getName();
    }
}
