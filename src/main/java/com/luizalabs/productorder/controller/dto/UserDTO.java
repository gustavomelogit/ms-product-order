package com.luizalabs.productorder.controller.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents the details of a user")
public class UserDTO {

    @JsonProperty("user_id")
    @Schema(description = "Unique identifier for the user", example = "12345")
    private Long id;

    @Schema(description = "Name of the user", example = "John Doe")
    private String name;

    @Schema(description = "List of orders associated with the user")
    private List<OrderDTO> orders;

}