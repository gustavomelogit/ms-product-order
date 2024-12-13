package com.luizalabs.productorder.model.service;

import com.luizalabs.productorder.model.entity.ProductEntity;
import com.luizalabs.productorder.model.repository.ProductRepository;
import com.luizalabs.productorder.model.repository.impl.MongoBatchUpsertRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MongoBatchUpsertRepository mongoBatchUpsertRepository;


    private List<ProductEntity> productEntityList;

    @BeforeEach
    void setUp() {
        productEntityList = Instancio.createList(ProductEntity.class);
    }

    @Test
    @DisplayName("Should save all products when saveAll is called")
    void shouldSaveAllProducts() {
        productService.saveAll(productEntityList);

        verify(mongoBatchUpsertRepository, times(1)).upsertMany(productEntityList, ProductEntity.class);
    }

    @Test
    @DisplayName("Should find all products by order IDs")
    void shouldFindAllByOrderIds() {
        var idList = productEntityList.stream().map(ProductEntity::getId).toList();

        when(productRepository.findAllByOrderIdIn(idList)).thenReturn(productEntityList);

        var result = productService.findAllByOrderIds(idList);

        verify(productRepository, times(1)).findAllByOrderIdIn(idList);
        assertEquals(productEntityList.size(), result.size());
        assertEquals(result, productEntityList);
    }
}