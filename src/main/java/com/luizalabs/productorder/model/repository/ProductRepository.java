package com.luizalabs.productorder.model.repository;

import com.luizalabs.productorder.model.entity.ProductEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<ProductEntity, String> {

    List<ProductEntity> findAllByOrderIdIn(List<String> idList);
}