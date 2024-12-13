package com.luizalabs.productorder.model.dto.file;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductFileDTO {

    private String id;

    @Setter
    private BigDecimal productValue;

    private String orderId;

}
