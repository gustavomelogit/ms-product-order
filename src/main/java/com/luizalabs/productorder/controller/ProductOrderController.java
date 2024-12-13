package com.luizalabs.productorder.controller;

import com.luizalabs.productorder.controller.annotation.DefaultApiDocumentation;
import com.luizalabs.productorder.controller.dto.UserDTO;
import com.luizalabs.productorder.model.service.ProductOrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/product-order/v1")
@RequiredArgsConstructor
@Tag(name = "Product Order", description = "Endpoints for managing product orders and imports")
public class ProductOrderController {

    private final ProductOrderService productOrderService;

    @PostMapping("/import")
    @DefaultApiDocumentation(
            summary = "Import product orders file",
            description = "Uploads a file containing product order data to be processed."
    )
    public ResponseEntity<String> importProductOrders(
            @RequestParam(value = "file") MultipartFile file) throws IOException {
        productOrderService.importFile(file);

        return ResponseEntity.ok("Import processed successfully");
    }

    @GetMapping("/order")
    @DefaultApiDocumentation(
            summary = "Query product orders",
            description = "Retrieves product orders based on optional filters such as order ID and date range, with pagination."
    )
    public ResponseEntity<Page<UserDTO>> getProductOrders(
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        var userDTOs = productOrderService.find(orderId, startDate, endDate, page, size);
        return ResponseEntity.ok(userDTOs);
    }
}