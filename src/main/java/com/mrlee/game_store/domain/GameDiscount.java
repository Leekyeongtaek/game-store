package com.mrlee.game_store.domain;

import com.mrlee.game_store.common.AuditingDateTime;
import com.mrlee.game_store.dto.request.GameDiscountUpdateForm;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "game_discount")
@Entity
public class GameDiscount extends AuditingDateTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_discount_id")
    private Long id;
    private int discountRate;
    private int discountPrice;
    private LocalDate startDate;
    private LocalDate endDate;

    @Builder
    public GameDiscount(int discountRate, int discountPrice, LocalDate startDate, LocalDate endDate) {
        this.discountRate = discountRate;
        this.discountPrice = discountPrice;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void update(GameDiscountUpdateForm form) {
        this.discountRate = form.getDiscountRate();
        this.discountPrice = form.getDiscountPrice();
        this.startDate = form.getStartDate();
        this.endDate = form.getEndDate();
    }
}
