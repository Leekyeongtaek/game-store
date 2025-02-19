package com.mrlee.game_store.dto.request;

import com.mrlee.game_store.domain.GameType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class GameDiscountSearchCondition {

    private List<GameType> types = new ArrayList<>();
    private List<Long> genreIds = new ArrayList<>();
    private List<Long> platformIds = new ArrayList<>();
    private List<String> webBasePrices = new ArrayList<>();
}
