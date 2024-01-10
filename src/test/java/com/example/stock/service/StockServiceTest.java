package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.aspectj.lang.annotation.After;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.swing.table.TableCellEditor;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockServiceTest {

    @Autowired
    private PessimisticLockStockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    void init() {
        Stock createStock = Stock.builder()
                .productId(1L)
                .quantity(100L)
                .build();
        stockRepository.save(createStock);
    }

    @AfterEach
    void tearDown() {
        stockRepository.deleteAll();
    }

    @DisplayName("재고가 제대로 감소하는지 테스트 한다.")
    @Test
    void decrease() {

        // given
        Long id = 1L;
        Long quantity = 1L;

        // when
        stockService.decrease(id, quantity);
        Stock findStock = stockRepository.findById(id).orElseThrow();
        // then
        assertThat(findStock.getQuantity()).isEqualTo(99L);

    }

    @DisplayName("동시에 100개의 재고 감소 요청, 감소 1개씩 100번 이다.")
    @Test
    void multiThreadStock() throws InterruptedException {

        // given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for ( int i=0; i<threadCount; i++ ) {
            executorService.submit(() -> {
                try {
                    stockService.decrease(1L, 1L);

                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        // then
        Stock findStock = stockRepository.findById(1L).orElseThrow();
        assertThat(findStock.getQuantity()).isEqualTo(0L);

    }



}