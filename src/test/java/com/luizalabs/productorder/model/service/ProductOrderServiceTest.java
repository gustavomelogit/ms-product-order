package com.luizalabs.productorder.model.service;

import com.luizalabs.productorder.model.dto.ProductOrderDTO;
import com.luizalabs.productorder.model.entity.OrderEntity;
import com.luizalabs.productorder.model.entity.ProductEntity;
import com.luizalabs.productorder.model.entity.UserEntity;
import com.luizalabs.productorder.model.mapper.ProductOrderMapper;
import com.luizalabs.productorder.model.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static com.luizalabs.productorder.common.CommonTestHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductOrderServiceTest {

    @InjectMocks
    private ProductOrderService productOrderService;

    @Mock
    private UserService userService;

    @Mock
    private OrderService orderService;

    @Mock
    private ProductService productService;

    @Mock
    private MultipartFile multipartFile;

    @Captor
    private ArgumentCaptor<List<UserEntity>> userCaptor;

    @Captor
    private ArgumentCaptor<List<OrderEntity>> orderCaptor;

    @Captor
    private ArgumentCaptor<List<ProductEntity>> productCaptor;

    private Page<OrderEntity> ordersPage;
    private List<UserEntity> userList;
    private List<ProductEntity> productList;
    private Pageable pageable;

    @BeforeEach
    public void setUp() {
        pageable = PageRequest.of(0, 10);
        var productOrderDTO = getFileDTO();

        ordersPage = new PageImpl<>(List.of(productOrderDTO.getOrder()), pageable, 1);
        userList = List.of(productOrderDTO.getUser());
        productList = productOrderDTO.getProducts();
    }


    @Test
    void shouldImportFileSuccessfully() throws IOException {
        var productOrderFile = getFileLineDTO();
        productOrderFile.getOrder().setTotalAmount(productOrderFile.getProducts().getFirst().getProductValue());
        var productOrderFileList = List.of(productOrderFile);
        var productOrderList = ProductOrderMapper.INSTANCE.fileToDTO(productOrderFileList);
        var fileContent = getFileLine();

        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent.getBytes()));
        when(multipartFile.getContentType()).thenReturn("text/plain");

        productOrderService.importFile(multipartFile);

        verify(userService).saveAll(userCaptor.capture());
        verify(orderService).saveAll(orderCaptor.capture());
        verify(productService).saveAll(productCaptor.capture());

        assertEquals(productOrderList.stream().map(ProductOrderDTO::getUser).toList(), userCaptor.getValue());
        assertEquals(productOrderList.stream().map(ProductOrderDTO::getOrder).toList(), orderCaptor.getValue());
        assertEquals(productOrderList.stream().map(ProductOrderDTO::getProducts).flatMap(List::stream).toList(),
                productCaptor.getValue());
    }

    @Test
    void shouldFindProductOrdersWithFilters() {
        var orderId = 12345L;
        var startDate = LocalDate.of(2023, 12, 1);
        var endDate = LocalDate.of(2023, 12, 31);

        when(orderService.findOrders(any(), any(), any(), any())).thenReturn(ordersPage);
        when(userService.findUsersByIdList(anyList())).thenReturn(userList);
        when(productService.findAllByOrderIds(anyList())).thenReturn(productList);

        var result = productOrderService.find(
                orderId, startDate, endDate, pageable.getPageNumber(), pageable.getPageSize());

        verify(orderService).findOrders(Utils.toString(orderId), startDate, endDate, pageable);
        verify(userService).findUsersByIdList(anyList());
        verify(productService).findAllByOrderIds(anyList());

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldMapToResponseDTOSuccessfully() {
        var orderList = ordersPage.stream().toList();
        var result = productOrderService.mapToResponseDTO(orderList, userList, productList);

        assertEquals(orderList.size(), result.size());
    }
}