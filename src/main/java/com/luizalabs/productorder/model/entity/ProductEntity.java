package com.luizalabs.productorder.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tb_product")
@CompoundIndex(def = "{'productId' : 1, 'orderId' : 1}", name = "product_order_idx")
public class ProductEntity implements EntityId<String> {

    @Id
    private String id;
    private BigDecimal productValue;
    private String orderId;
}