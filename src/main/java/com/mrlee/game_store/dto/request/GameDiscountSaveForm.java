package com.mrlee.game_store.dto.request;

import com.mrlee.game_store.domain.GameDiscount;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class GameDiscountSaveForm {

    private Integer discountRate;
    private Integer discountPrice;
    private LocalDate startDate;
    private LocalDate endDate;

    public GameDiscount toEntity() {
        return GameDiscount.builder()
                .discountRate(discountRate)
                .discountPrice(discountPrice)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}
