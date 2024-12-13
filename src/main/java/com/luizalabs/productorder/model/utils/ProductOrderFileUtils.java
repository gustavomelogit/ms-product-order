package com.luizalabs.productorder.model.utils;

import com.luizalabs.productorder.model.dto.file.OrderFileDTO;
import com.luizalabs.productorder.model.dto.file.ProductFileDTO;
import com.luizalabs.productorder.model.dto.file.ProductOrderFileDTO;
import com.luizalabs.productorder.model.dto.file.UserFileDTO;
import com.luizalabs.productorder.model.exceptions.FileException;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.luizalabs.productorder.model.utils.Utils.generateProductId;

@UtilityClass
public class ProductOrderFileUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String SUPPORTED_FILE_TYPE = "text/plain";

    public List<ProductOrderFileDTO> processFile(MultipartFile file) throws IOException {
        validateTxtFile(file);
        var productOrderFileAggregated = new HashMap<String, ProductOrderFileDTO>();

        try (var lines = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)).lines()) {

            lines.forEach(line -> {
                try {
                    var userIdStr = line.substring(0, 10).trim();
                    var userName = line.substring(10, 55).trim();
                    var orderIdStr = line.substring(55, 65).trim();
                    var productIdStr = line.substring(65, 75).trim();
                    var productValueStr = line.substring(75, 87).trim();
                    var purchaseDateStr = line.substring(87, 95).trim();

                    var key = String.join("-", userIdStr, orderIdStr);

                    var user = buildUser(userIdStr, userName);
                    var order = buildOrder(orderIdStr, purchaseDateStr, user.getId());
                    var product = buildProduct(productIdStr, productValueStr, order.getId());

                    productOrderFileAggregated.merge(key,
                            createNewAggregation(user, order, product),
                            (existingProductOrder, newEntry) ->
                                    mergeAggregations(existingProductOrder, product));
                } catch (Exception e) {
                    throw new FileException("Invalid data format: " + e.getMessage(), e);
                }
            });
        }
        return productOrderFileAggregated.values().stream().toList();
    }

    static void validateTxtFile(MultipartFile file) {
        if (Objects.isNull(file) || file.isEmpty()) {
            throw new MultipartException("File is mandatory");
        }

        var contentType = file.getContentType();
        if (Objects.isNull(contentType) || ObjectUtils.notEqual(SUPPORTED_FILE_TYPE, contentType)) {
            throw new MultipartException("Unsupported file type");
        }
    }

    static UserFileDTO buildUser(String userIdStr, String userName) {
        return UserFileDTO.builder()
                .id(Long.parseLong(userIdStr))
                .name(userName)
                .build();
    }

    static OrderFileDTO buildOrder(String orderIdStr, String purchaseDateStr, Long userId) {
        return OrderFileDTO.builder()
                .id(Long.parseLong(orderIdStr))
                .purchaseDate(LocalDate.parse(purchaseDateStr, DATE_FORMATTER))
                .userId(userId)
                .build();
    }

    static ProductFileDTO buildProduct(String productIdStr, String productValueStr, Long orderId) {
        return ProductFileDTO.builder()
                .id(generateProductId(Long.parseLong(productIdStr), orderId))
                .productValue(new BigDecimal(productValueStr))
                .orderId(orderId.toString())
                .build();
    }

    static ProductOrderFileDTO createNewAggregation(UserFileDTO user,
                                                    OrderFileDTO order,
                                                    ProductFileDTO product) {
        var products = new ArrayList<ProductFileDTO>();
        order.setTotalAmount(product.getProductValue());
        products.add(product);
        return ProductOrderFileDTO.builder()
                .user(user)
                .order(order)
                .products(products)
                .build();
    }


    static ProductOrderFileDTO mergeAggregations(ProductOrderFileDTO existingProductOrder,
                                                 ProductFileDTO product) {
        var products = existingProductOrder.getProducts();
        var existingProduct = products.stream()
                .filter(currentProduct -> Objects.equals(currentProduct.getId(), product.getId()))
                .findFirst();

        if (existingProduct.isPresent()) {
            existingProduct.get().setProductValue(
                    existingProduct.get().getProductValue().add(product.getProductValue()));
        } else {
            products.add(product);
        }

        var order = existingProductOrder.getOrder();
        order.setTotalAmount(order.getTotalAmount().add(product.getProductValue()));

        return existingProductOrder;
    }


}
