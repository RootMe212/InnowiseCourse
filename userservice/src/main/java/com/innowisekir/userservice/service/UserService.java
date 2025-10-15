package com.innowisekir.userservice.service;

import com.innowisekir.userservice.dto.UserDTO;
import java.util.List;

public interface UserService {

  UserDTO createUser(UserDTO userDTO);

  UserDTO getUserById(Long id);

  List<UserDTO> getUsersByIds(List<Long> ids);

  UserDTO getUserByEmail(String email);

  UserDTO updateUser(Long id, UserDTO userDTO);

  void deleteUser(Long id);

  Boolean existsById(Long id);
}
