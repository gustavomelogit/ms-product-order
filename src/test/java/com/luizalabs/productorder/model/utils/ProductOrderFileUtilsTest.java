package com.luizalabs.productorder.model.utils;

import com.luizalabs.productorder.model.dto.file.OrderFileDTO;
import com.luizalabs.productorder.model.dto.file.ProductFileDTO;
import com.luizalabs.productorder.model.dto.file.ProductOrderFileDTO;
import com.luizalabs.productorder.model.dto.file.UserFileDTO;
import com.luizalabs.productorder.model.exceptions.FileException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.luizalabs.productorder.common.CommonTestHelper.getFileLine;
import static com.luizalabs.productorder.common.CommonTestHelper.getFileLineDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductOrderFileUtilsTest {

    @Mock
    private MultipartFile mockFile;

    @Test
    @DisplayName("Should process file correctly when file format is valid")
    void shouldProcessFileCorrectly() throws IOException {
        var fileContent = getFileLine();
        var mockFile = mock(MultipartFile.class);

        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent.getBytes()));
        when(mockFile.getContentType()).thenReturn("text/plain");

        var result = ProductOrderFileUtils.processFile(mockFile);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        var productOrderFileDTO = result.getFirst();
        assertEquals(productOrderFileDTO, getFileLineDTO());
        assertEquals("Palmer Prosacco", productOrderFileDTO.getUser().getName());
        assertEquals(70, productOrderFileDTO.getUser().getId());
        assertEquals(753L, productOrderFileDTO.getOrder().getId());
        assertEquals(LocalDate.parse("2021-03-08"), productOrderFileDTO.getOrder().getPurchaseDate());
        assertEquals("3_753", productOrderFileDTO.getProducts().getFirst().getId());
        assertEquals(new BigDecimal("1836.74"), productOrderFileDTO.getProducts().getFirst().getProductValue());
    }


    @Test
    @DisplayName("Should throws exception when not process file correctly")
    void shouldThrowsExceptionWhenNotProcessFileCorrectly() throws IOException {
        var fileContent = "ERROR";
        var mockFile = mock(MultipartFile.class);

        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent.getBytes()));
        when(mockFile.getContentType()).thenReturn("text/plain");

        assertThrows(FileException.class, () ->
                ProductOrderFileUtils.processFile(mockFile));
    }


    @Test
    @DisplayName("Should throw exception when file type is unsupported")
    void shouldThrowExceptionWhenFileTypeIsUnsupported() {
        var mockFile = mock(MultipartFile.class);

        when(mockFile.getContentType()).thenReturn("application/pdf");

        assertThrows(MultipartException.class, () -> ProductOrderFileUtils.processFile(mockFile));
    }

    @Test
    @DisplayName("Should throw exception when file is empty")
    void shouldThrowExceptionWhenFileIsEmpty() {
        var mockFile = mock(MultipartFile.class);

        when(mockFile.isEmpty()).thenReturn(true);

        assertThrows(MultipartException.class, () -> ProductOrderFileUtils.processFile(mockFile));
    }

    @Test
    @DisplayName("Should correctly validate the file type")
    void shouldValidateFileType() {
        var validFile = mock(MultipartFile.class);
        var invalidFile = mock(MultipartFile.class);

        when(validFile.getContentType()).thenReturn("text/plain");
        when(invalidFile.getContentType()).thenReturn("application/json");

        assertDoesNotThrow(() -> ProductOrderFileUtils.validateTxtFile(validFile));
        assertThrows(MultipartException.class, () -> ProductOrderFileUtils.validateTxtFile(invalidFile));
    }

    @Test
    @DisplayName("Should correctly build User entity from string values")
    void shouldBuildUserFromStringValues() {
        var userIdStr = "12345";
        var userName = "John Doe";

        var user = ProductOrderFileUtils.buildUser(userIdStr, userName);

        assertNotNull(user);
        assertEquals(12345L, user.getId());
        assertEquals("John Doe", user.getName());
    }

    @Test
    @DisplayName("Should correctly build Order entity from string values")
    void shouldBuildOrderFromStringValues() {
        var orderIdStr = "10001";
        var purchaseDateStr = "20231201";

        var userId = 12345L;

        var order = ProductOrderFileUtils.buildOrder(orderIdStr, purchaseDateStr, userId);

        assertNotNull(order);
        assertEquals(10001L, order.getId());
        assertEquals(LocalDate.parse("2023-12-01"), order.getPurchaseDate());
        assertEquals(12345L, order.getUserId());
    }

    @Test
    @DisplayName("Should correctly build Product entity from string values")
    void shouldBuildProductFromStringValues() {
        var productIdStr = "56789";
        var productValueStr = "123.45";
        Long orderId = 10001L;

        var product = ProductOrderFileUtils.buildProduct(productIdStr, productValueStr, orderId);

        assertNotNull(product);
        assertEquals("56789_10001", product.getId());
        assertEquals(new BigDecimal("123.45"), product.getProductValue());
        assertEquals("10001", product.getOrderId());
    }

    @Test
    @DisplayName("Should correctly aggregate product, order, and user data")
    void shouldAggregateProductOrderData() {
        var user = UserFileDTO.builder().id(12345L).name("John Doe").build();
        var order = OrderFileDTO.builder().id(10001L).purchaseDate(LocalDate.now()).userId(12345L).build();
        var product = ProductFileDTO.builder().id("56789_10001").productValue(new BigDecimal("123.45")).orderId("10001").build();

        var aggregation = ProductOrderFileUtils.createNewAggregation(user, order, product);

        assertNotNull(aggregation);
        assertEquals(user, aggregation.getUser());
        assertEquals(order, aggregation.getOrder());
        assertEquals(1, aggregation.getProducts().size());
        assertEquals(product, aggregation.getProducts().getFirst());
    }


    @Test
    void shouldMergeAggregations() {
        var order = OrderFileDTO.builder().totalAmount(BigDecimal.valueOf(100.00)).build();
        var product = ProductFileDTO.builder().id("0_123").productValue(BigDecimal.valueOf(100.00)).build();

        var newProduct = ProductFileDTO.builder().id("0_123").productValue(BigDecimal.valueOf(25.00)).build();
        var existingOrder = ProductOrderFileDTO.builder()
                .order(order)
                .products(List.of(product))
                .build();


        var result = ProductOrderFileUtils.mergeAggregations(existingOrder, newProduct);

        assertThat(result.getProducts()).hasSize(1);
        assertThat(result.getProducts().getFirst().getProductValue()).isEqualByComparingTo("125.00");
        assertThat(result.getOrder().getTotalAmount()).isEqualByComparingTo("125.00");
    }


}