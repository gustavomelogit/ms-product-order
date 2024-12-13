package com.luizalabs.productorder.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents the details of an order")
public class OrderDTO {

    @JsonProperty("order_id")
    @Schema(description = "Unique identifier for the order", example = "101")
    private Long id;

    @JsonProperty("total")
    @Schema(description = "Total amount for the order", example = "150.50")
    private BigDecimal totalAmount;

    @JsonProperty("date")
    @Schema(description = "Purchase date of the order", example = "2023-12-01")
    private LocalDate purchaseDate;

    @Schema(description = "List of products included in the order")
    private List<ProductDTO> products;

}