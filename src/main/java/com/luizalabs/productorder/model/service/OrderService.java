package com.luizalabs.productorder.model.service;

import com.luizalabs.productorder.model.entity.OrderEntity;
import com.luizalabs.productorder.model.repository.impl.OrderRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepositoryImpl orderRepository;

    /**
     * Save all orders
     *
     * @param orderEntityList Order entities to save
     */
    public void saveAll(List<OrderEntity> orderEntityList) {
        log.info("Save Order | Saving orders...");
        orderRepository.saveAll(orderEntityList);
    }

    /**
     * Find orders
     *
     * @param orderId   filter by order id.
     * @param startDate filter by initial date range (YYYY-MM-DD).
     * @param endDate   filter by final date range (YYYY-MM-DD).
     * @param pageable  pageable data
     */
    public Page<OrderEntity> findOrders(String orderId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return orderRepository.findOrders(orderId, startDate, endDate, pageable);
    }

}