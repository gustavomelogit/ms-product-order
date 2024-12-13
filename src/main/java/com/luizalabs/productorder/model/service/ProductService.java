package com.luizalabs.productorder.model.service;

import com.luizalabs.productorder.model.entity.ProductEntity;
import com.luizalabs.productorder.model.repository.ProductRepository;
import com.luizalabs.productorder.model.repository.impl.MongoBatchUpsertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final MongoBatchUpsertRepository mongoBatchUpsertRepository;

    /**
     * Save all products
     *
     * @param productEntityList Product entities to save
     */
    public void saveAll(List<ProductEntity> productEntityList) {
        log.info("Save Product | Saving products...");
        mongoBatchUpsertRepository.upsertMany(productEntityList, ProductEntity.class);
    }

    /**
     * Find products by order ids
     *
     * @param idList order id list to find
     * @return products found
     */
    public List<ProductEntity> findAllByOrderIds(List<String> idList) {
        return productRepository.findAllByOrderIdIn(idList);
    }
}