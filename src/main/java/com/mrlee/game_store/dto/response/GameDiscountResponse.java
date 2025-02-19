package com.mrlee.game_store.dto.response;

import com.mrlee.game_store.domain.GameDiscount;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

@NoArgsConstructor
@Data
public class GameDiscountResponse {

    private Long id;
    private int discountRate;
    private String discountPrice;
    private LocalDate startDate;
    private LocalDate endDate;

    public GameDiscountResponse(GameDiscount gameDiscount) {
        this.id = gameDiscount.getId();
        this.discountRate = gameDiscount.getDiscountRate();
        this.discountPrice = NumberFormat.getInstance(Locale.KOREA).format(gameDiscount.getDiscountPrice());
        this.startDate = gameDiscount.getStartDate();
        this.endDate = gameDiscount.getEndDate();
    }
}
