package com.example.stock.facade;

import com.example.stock.service.OptimisticLockStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OptimisticLockStockFacade {

    private final OptimisticLockStockService optimisticLockStockService;

    public void decrease(Long id, Long quantity) throws InterruptedException {
        while ( true ) {
            try {
                optimisticLockStockService.decrease(id, quantity);
                break;
            } catch ( RuntimeException e ) {
                // 재시도
                Thread.sleep(50);
            }
        }
    }
}
