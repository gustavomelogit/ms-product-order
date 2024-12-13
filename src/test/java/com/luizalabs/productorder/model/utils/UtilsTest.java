package com.luizalabs.productorder.model.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UtilsTest {

    @ParameterizedTest
    @CsvSource({
            "12345, '12345'",
            "0, '0'"
    })
    @DisplayName("Should convert Long to String when given Long value")
    void shouldConvertLongToStringWhenGivenLongValue(Long input, String expectedOutput) {
        var result = Utils.toString(input);

        assertEquals(expectedOutput, result);
    }

    @Test
    @DisplayName("Should return null String when given null")
    void shouldReturnNullStringWhenGivenNull() {
        var result = Utils.toString(null);

        assertNull(result);
    }

    @ParameterizedTest
    @CsvSource({
            "1001, 2002, '1001_2002'",
            "123, 456, '123_456'",
            "9999, 8888, '9999_8888'"
    })
    @DisplayName("Should generate ProductId when given ProductId and OrderId")
    void shouldGenerateProductIdWhenGivenProductIdAndOrderId(Long productId, Long orderId, String expectedProductId) {
        var result = Utils.generateProductId(productId, orderId);

        assertEquals(expectedProductId, result);
    }

    @ParameterizedTest
    @CsvSource({
            "'1001_2002', '1001'",
            "'123_456', '123'",
            "'9999_8888', '9999'"
    })
    @DisplayName("Should extract ProductId from combined Id")
    void shouldExtractProductIdFromCombinedId(String inputId, String expectedProductId) {
        var result = Utils.extractProductId(inputId);

        assertEquals(expectedProductId, result);
    }
}