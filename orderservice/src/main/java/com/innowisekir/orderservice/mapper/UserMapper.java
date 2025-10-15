package com.innowisekir.orderservice.mapper;

import com.innowisekir.orderservice.dto.response.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

  UserDTO toDTO(com.innowisekir.orderservice.dto.response.UserDTO userDTO);
}
