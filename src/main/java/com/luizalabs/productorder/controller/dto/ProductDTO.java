package com.luizalabs.productorder.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents the details of a product")
public class ProductDTO {

    @JsonProperty("product_id")
    @Schema(description = "Unique identifier for the product", example = "202")
    private Long id;

    @JsonProperty("value")
    @Schema(description = "Value of the product", example = "29.99")
    private BigDecimal productValue;
}