package com.luizalabs.productorder.model.service;

import com.luizalabs.productorder.controller.dto.UserDTO;
import com.luizalabs.productorder.controller.mapper.ResponseMapper;
import com.luizalabs.productorder.model.dto.ProductOrderDTO;
import com.luizalabs.productorder.model.entity.OrderEntity;
import com.luizalabs.productorder.model.entity.ProductEntity;
import com.luizalabs.productorder.model.entity.UserEntity;
import com.luizalabs.productorder.model.mapper.ProductOrderMapper;
import com.luizalabs.productorder.model.utils.ProductOrderFileUtils;
import com.luizalabs.productorder.model.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductOrderService {

    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService;

    /**
     * Processes the import file and performs data normalization.
     *
     * @param file File with product orders data
     */
    public void importFile(MultipartFile file) throws IOException {
        log.info("Import File | Start importing file");

        var productOrderFileList = ProductOrderFileUtils.processFile(file);
        var productOrderList = ProductOrderMapper.INSTANCE.fileToDTO(productOrderFileList);

        userService.saveAll(productOrderList.stream().map(ProductOrderDTO::getUser).distinct().toList());
        orderService.saveAll(productOrderList.stream().map(ProductOrderDTO::getOrder).toList());
        productService.saveAll((productOrderList.stream().map(ProductOrderDTO::getProducts)
                .flatMap(List::stream).toList()));

        log.info("Import File | File import successfully");
    }

    /**
     * Find product orders with filters and pagination.
     *
     * @param orderId   filter by order id.
     * @param startDate filter by start date range (YYYY-MM-DD).
     * @param endDate   filter by final date range (YYYY-MM-DD).
     * @param page      page of pagination
     * @param size      size of pagination
     * @return product orders found
     */
    public Page<UserDTO> find(Long orderId,
                              LocalDate startDate, LocalDate endDate,
                              int page, int size) {
        log.info("Find Product Orders | Finding with parameters: orderId: <{}>, " +
                        "startDate: <{}>, endDate: <{}>, page: <{}>, size: <{}>",
                orderId, startDate, endDate, page, size);

        var ordersPage = orderService.findOrders(
                Utils.toString(orderId),
                startDate,
                endDate,
                PageRequest.of(page, size)
        );

        var userIdList = ordersPage.stream().map(OrderEntity::getUserId).toList();
        var userList = userService.findUsersByIdList(userIdList);

        var orderIdList = ordersPage.stream().map(OrderEntity::getId).toList();
        var productList = productService.findAllByOrderIds(orderIdList);

        var responseDTO = mapToResponseDTO(ordersPage.get().toList(), userList, productList);

        return new PageImpl<>(responseDTO, ordersPage.getPageable(), ordersPage.getTotalElements());
    }


    public List<UserDTO> mapToResponseDTO(List<OrderEntity> orderList, List<UserEntity> userList, List<ProductEntity> productList) {
        var userMap = userList.stream()
                .collect(Collectors.toMap(UserEntity::getId, user -> user));

        var orderIdToProductsMap = productList.stream()
                .collect(Collectors.groupingBy(
                        ProductEntity::getOrderId,
                        Collectors.mapping(ResponseMapper.INSTANCE::productEntityToDTO, Collectors.toList())
                ));

        return orderList.stream()
                .map(order -> {
                    var userDTO = ResponseMapper.INSTANCE.userEntityToDTO(userMap.get(order.getUserId()));
                    var orderDTO = ResponseMapper.INSTANCE.orderEntityToDTO(order);

                    var products = orderIdToProductsMap.get(order.getId());
                    if (Objects.nonNull(products)) {
                        orderDTO.setProducts(products);
                    }

                    if (Objects.isNull(userDTO.getOrders())) {
                        userDTO.setOrders(new ArrayList<>());
                    }
                    userDTO.getOrders().add(orderDTO);

                    return userDTO;
                })
                .collect(Collectors.toList());
    }

}