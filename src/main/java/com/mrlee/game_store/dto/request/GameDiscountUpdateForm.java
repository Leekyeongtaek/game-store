package com.mrlee.game_store.dto.request;

import com.mrlee.game_store.domain.GameDiscount;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class GameDiscountUpdateForm {

    private Integer discountRate;
    private Integer discountPrice;
    private LocalDate startDate;
    private LocalDate endDate;

    public GameDiscountUpdateForm(GameDiscount gameDiscount) {
        this.discountRate = gameDiscount.getDiscountRate();
        this.discountPrice = gameDiscount.getDiscountPrice();
        this.startDate = gameDiscount.getStartDate();
        this.endDate = gameDiscount.getEndDate();
    }

    public GameDiscount toEntity() {
        return GameDiscount.builder()
                .discountRate(discountRate)
                .discountPrice(discountPrice)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}
