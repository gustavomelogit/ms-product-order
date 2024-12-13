package com.luizalabs.productorder.model.utils;

import lombok.experimental.UtilityClass;

import java.util.Objects;

@UtilityClass
public class Utils {

    public static String toString(Long value) {
        return Objects.isNull(value) ? null : value.toString();
    }

    public static String generateProductId(Long productId, Long orderId) {
        return productId + "_" + orderId;
    }

    public static String extractProductId(String id) {
        return id.split("_")[0];
    }


}

