package com.luizalabs.productorder.model.service;

import com.luizalabs.productorder.model.entity.OrderEntity;
import com.luizalabs.productorder.model.repository.impl.OrderRepositoryImpl;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepositoryImpl orderRepository;

    private List<OrderEntity> orderEntityList;
    private Pageable pageable;

    private LocalDate startDate;
    private LocalDate endDate;
    private String orderId;

    @BeforeEach
    void setUp() {
        orderEntityList = Instancio.createList(OrderEntity.class);
        pageable = PageRequest.of(0, 10);
        startDate = LocalDate.of(2023, 12, 1);
        endDate = LocalDate.of(2023, 12, 31);
        orderId = "12345";
    }

    @Test
    @DisplayName("Should save all orders successfully")
    void shouldSaveAllOrdersSuccessfully() {
        orderService.saveAll(orderEntityList);

        verify(orderRepository, times(1)).saveAll(orderEntityList);
    }

    @Test
    @DisplayName("Should find orders")
    void shouldFindOrders() {
        var orderList = new PageImpl<>(orderEntityList, pageable, 1L);

        when(orderRepository.findOrders(any(), any(), any(), any())).thenReturn(orderList);

        var result = orderService.findOrders(orderId, startDate, endDate, pageable);

        assertNotNull(result);
        assertEquals(orderList.getTotalElements(), result.getTotalElements());
        assertEquals(orderList.getNumberOfElements(), result.getContent().size());
        verify(orderRepository, times(1)).findOrders(any(), any(), any(), any());
    }

}