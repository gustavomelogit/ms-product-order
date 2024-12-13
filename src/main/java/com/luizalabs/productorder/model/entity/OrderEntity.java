package com.luizalabs.productorder.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tb_order")
public class OrderEntity implements EntityId<String> {

    @Id
    private String id;

    private BigDecimal totalAmount;

    private LocalDate purchaseDate;

    private String userId;

}