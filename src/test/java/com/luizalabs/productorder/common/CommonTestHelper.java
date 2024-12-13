package com.luizalabs.productorder.common;

import com.luizalabs.productorder.model.dto.ProductOrderDTO;
import com.luizalabs.productorder.model.dto.file.OrderFileDTO;
import com.luizalabs.productorder.model.dto.file.ProductFileDTO;
import com.luizalabs.productorder.model.dto.file.ProductOrderFileDTO;
import com.luizalabs.productorder.model.dto.file.UserFileDTO;
import com.luizalabs.productorder.model.entity.OrderEntity;
import com.luizalabs.productorder.model.entity.ProductEntity;
import com.luizalabs.productorder.model.entity.UserEntity;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@UtilityClass
public class CommonTestHelper {

    public static String getFileLine() {
        return "0000000070                              Palmer Prosacco00000007530000000003     1836.7420210308";
    }

    public static ProductOrderFileDTO getFileLineDTO() {
        return ProductOrderFileDTO.builder()
                .user(UserFileDTO.builder().name("Palmer Prosacco").id(70L).build())
                .order(OrderFileDTO.builder().id(753L).purchaseDate(LocalDate.parse("2021-03-08")).userId(70L).build())
                .products(List.of(ProductFileDTO.builder().productValue(new BigDecimal("1836.74")).orderId("753").id("3_753").build()))
                .build();

    }

    public static ProductOrderDTO getFileDTO() {
        return ProductOrderDTO.builder()
                .order(OrderEntity.builder().id("753").purchaseDate(LocalDate.parse("2021-03-08")).userId("70").build())
                .user(UserEntity.builder().name("Palmer Prosacco").id("70").build())
                .products(List.of(ProductEntity.builder().productValue(new BigDecimal("1836.74")).orderId("753").id("3_753").build()))
                .build();

    }
}
