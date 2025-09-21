package com.innowisekir.userservice.mapper;

import com.innowisekir.userservice.dto.UserDTO;
import com.innowisekir.userservice.entity.User;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface UserListMapper {


  List<UserDTO> toDtoList(List<User> userList);
}
