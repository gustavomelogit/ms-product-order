package com.luizalabs.productorder.controller.mapper;

import com.luizalabs.productorder.controller.dto.OrderDTO;
import com.luizalabs.productorder.controller.dto.ProductDTO;
import com.luizalabs.productorder.controller.dto.UserDTO;
import com.luizalabs.productorder.model.entity.OrderEntity;
import com.luizalabs.productorder.model.entity.ProductEntity;
import com.luizalabs.productorder.model.entity.UserEntity;
import com.luizalabs.productorder.model.utils.Utils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Objects;

@Mapper
public interface ResponseMapper {

    ResponseMapper INSTANCE = Mappers.getMapper(ResponseMapper.class);

    @Mapping(target = "id", source = "id", qualifiedByName = "parseLong")
    OrderDTO orderEntityToDTO(OrderEntity entity);

    @Mapping(target = "id", source = "id", qualifiedByName = "parseLong")
    UserDTO userEntityToDTO(UserEntity entity);

    @Mapping(target = "id", source = "id", qualifiedByName = "extractProductId")
    ProductDTO productEntityToDTO(ProductEntity entity);

    @Named("parseLong")
    static Long parseLong(String id) {
        return Objects.isNull(id) ? null : Long.parseLong(id);
    }

    @Named("extractProductId")
    static String extractProductId(String productId) {
        return Utils.extractProductId(productId);
    }

}
