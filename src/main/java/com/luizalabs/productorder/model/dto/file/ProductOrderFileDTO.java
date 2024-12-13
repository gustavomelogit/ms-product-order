package com.luizalabs.productorder.model.dto.file;

import lombok.*;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProductOrderFileDTO {

    private UserFileDTO user;
    private OrderFileDTO order;
    private List<ProductFileDTO> products;

    public List<ProductFileDTO> getProducts() {
        return CollectionUtils.isEmpty(products) ? new ArrayList<>() : products;
    }

}
