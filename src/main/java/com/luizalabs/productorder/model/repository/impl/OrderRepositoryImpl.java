package com.luizalabs.productorder.model.repository.impl;

import com.luizalabs.productorder.model.entity.OrderEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl {

    private final MongoBatchUpsertRepository mongoBatchUpsertRepository;
    private final MongoTemplate mongoTemplate;

    /**
     * Save all orders with mongo batch
     *
     * @param orderEntityList Order entities to save
     */
    public void saveAll(List<OrderEntity> orderEntityList) {
        mongoBatchUpsertRepository.upsertMany(orderEntityList, OrderEntity.class);
    }

    /**
     * Find orders using mongo criteria filters
     *
     * @param orderId   filter by order id.
     * @param startDate filter by initial date range (YYYY-MM-DD).
     * @param endDate   filter by final date range (YYYY-MM-DD).
     * @param pageable  pageable data
     */
    public Page<OrderEntity> findOrders(String orderId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        var criteria = new Criteria();

        setIdCriteria(orderId, criteria);
        setPurchaseDateCriteria(startDate, endDate, criteria);

        var query = new Query(criteria).with(pageable);

        long count = mongoTemplate.count(query, OrderEntity.class);
        List<OrderEntity> orders = mongoTemplate.find(query, OrderEntity.class);

        return new PageImpl<>(orders, pageable, count);
    }

    private static void setIdCriteria(String orderId, Criteria criteria) {
        if (Objects.nonNull(orderId)) {
            criteria.and("id").is(orderId);
        }
    }

    private static void setPurchaseDateCriteria(LocalDate startDate, LocalDate endDate, Criteria criteria) {
        if (Objects.nonNull(startDate) || Objects.nonNull(endDate)) {
            var dateCriteria = new Criteria("purchaseDate");
            if (Objects.nonNull(startDate)) {
                dateCriteria.gte(startDate);
            }
            if (Objects.nonNull(endDate)) {
                dateCriteria.lte(endDate);
            }
            criteria.andOperator(dateCriteria);
        }
    }
}
