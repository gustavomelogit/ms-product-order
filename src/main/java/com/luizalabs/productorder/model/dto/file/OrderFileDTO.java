package com.luizalabs.productorder.model.dto.file;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderFileDTO {

    private Long id;
    private LocalDate purchaseDate;

    @Setter
    private BigDecimal totalAmount;

    private Long userId;

}
