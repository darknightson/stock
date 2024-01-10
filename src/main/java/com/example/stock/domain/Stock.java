package com.example.stock.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private Long quantity;

    @Version
    private Long version;

    @Builder
    private Stock(Long productId, Long quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public static Stock createStock(Long productId, Long quantity) {
        return Stock.builder()
                .productId(productId)
                .quantity(quantity)
                .build();
    }

    public void decrease(Long quantity) {
        if ( this.quantity - quantity < 0 ) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.quantity -= quantity;
    }
}
