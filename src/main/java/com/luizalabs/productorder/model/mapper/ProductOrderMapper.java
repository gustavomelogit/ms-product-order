package com.luizalabs.productorder.model.mapper;

import com.luizalabs.productorder.model.dto.ProductOrderDTO;
import com.luizalabs.productorder.model.dto.file.ProductOrderFileDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ProductOrderMapper {

    ProductOrderMapper INSTANCE = Mappers.getMapper(ProductOrderMapper.class);

    List<ProductOrderDTO> fileToDTO(List<ProductOrderFileDTO> fileList);

}
