package com.luizalabs.productorder.model.repository.impl;

import com.luizalabs.productorder.model.entity.OrderEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryImplTest {

    @InjectMocks
    private OrderRepositoryImpl orderRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private MongoBatchUpsertRepository mongoBatchUpsertRepository;

    private OrderEntity orderEntity;
    private Pageable pageable;

    private LocalDate startDate;
    private LocalDate endDate;
    private String orderId;

    @BeforeEach
    void setUp() {
        orderEntity = Instancio.create(OrderEntity.class);
        pageable = PageRequest.of(0, 10);
        startDate = LocalDate.of(2023, 12, 1);
        endDate = LocalDate.of(2023, 12, 31);
        orderId = "12345";
    }

    @Test
    @DisplayName("Should save all orders successfully")
    void shouldSaveAllOrdersSuccessfully() {
        var orderEntityList = List.of(orderEntity);

        orderRepository.saveAll(orderEntityList);

        verify(mongoBatchUpsertRepository, times(1)).upsertMany(orderEntityList, OrderEntity.class);
    }

    @Test
    @DisplayName("Should find orders with valid criteria")
    void shouldFindOrdersWithValidCriteria() {
        var totalCount = 1L;
        var orderList = List.of(orderEntity);

        when(mongoTemplate.count(any(Query.class), eq(OrderEntity.class))).thenReturn(totalCount);
        when(mongoTemplate.find(any(Query.class), eq(OrderEntity.class))).thenReturn(orderList);

        var result = orderRepository.findOrders(orderId, startDate, endDate, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        verify(mongoTemplate, times(1)).count(any(Query.class), eq(OrderEntity.class));
        verify(mongoTemplate, times(1)).find(any(Query.class), eq(OrderEntity.class));
    }

    @Test
    @DisplayName("Should create criteria with order id filter")
    void shouldCreateCriteriaWithOrderIdFilter() {
        orderRepository.findOrders(orderId, null, null, pageable);

        var captor = ArgumentCaptor.forClass(Query.class);
        verify(mongoTemplate, times(1)).find(captor.capture(), any());
        var queryStr = captor.getValue().toString();
        assertTrue(queryStr.contains("\"id\" : \"12345\""));
    }

    @Test
    @DisplayName("Should create criteria with purchase date range filter")
    void shouldCreateCriteriaWithPurchaseDateRangeFilter() {
        orderRepository.findOrders(null, startDate, endDate, pageable);

        var captor = ArgumentCaptor.forClass(Query.class);
        verify(mongoTemplate, times(1)).find(captor.capture(), any());
        var queryStr = captor.getValue().toString();
        assertTrue(queryStr.contains("purchaseDate"));
        assertTrue(queryStr.contains("gte"));
        assertTrue(queryStr.contains("lte"));
    }

    @Test
    @DisplayName("Should not create purchase date filter if start date and end date are null")
    void shouldNotCreatePurchaseDateFilterIfNull() {
        orderRepository.findOrders(null, null, null, pageable);

        var captor = ArgumentCaptor.forClass(Query.class);
        verify(mongoTemplate, times(1)).find(captor.capture(), any());
        var queryStr = captor.getValue().toString();
        assertFalse(queryStr.contains("purchaseDate"));
    }

    @Test
    @DisplayName("Should create purchase date filter with only start date")
    void shouldCreatePurchaseDateFilterWithOnlyStartDate() {
        orderRepository.findOrders(null, startDate, null, pageable);

        var captor = ArgumentCaptor.forClass(Query.class);
        verify(mongoTemplate, times(1)).find(captor.capture(), any());
        var queryStr = captor.getValue().toString();
        assertTrue(queryStr.contains("gte"));
    }

    @Test
    @DisplayName("Should create purchase date filter with only end date")
    void shouldCreatePurchaseDateFilterWithOnlyEndDate() {
        orderRepository.findOrders(null, null, endDate, pageable);

        var captor = ArgumentCaptor.forClass(Query.class);
        verify(mongoTemplate, times(1)).find(captor.capture(), any());
        var queryStr = captor.getValue().toString();
        assertTrue(queryStr.contains("$lte"));
    }
}