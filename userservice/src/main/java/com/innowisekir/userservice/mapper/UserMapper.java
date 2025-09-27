package com.innowisekir.userservice.mapper;

import com.innowisekir.userservice.dto.UserDTO;
import com.innowisekir.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CardInfoMapper.class})
public interface UserMapper {

  UserDTO toDTO(User user);

  @Mapping(target = "cards", ignore = true)
  User toEntity(UserDTO userDTO);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "cards", ignore = true)
  User toEntityForUpdate(UserDTO userDTO);

}
