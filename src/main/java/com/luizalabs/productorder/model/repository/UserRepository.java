package com.luizalabs.productorder.model.repository;


import com.luizalabs.productorder.model.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserEntity, String> {
}