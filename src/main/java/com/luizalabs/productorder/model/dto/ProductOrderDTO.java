package com.luizalabs.productorder.model.dto;

import com.luizalabs.productorder.model.entity.OrderEntity;
import com.luizalabs.productorder.model.entity.ProductEntity;
import com.luizalabs.productorder.model.entity.UserEntity;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductOrderDTO {

    private UserEntity user;
    private OrderEntity order;
    private List<ProductEntity> products;

}
