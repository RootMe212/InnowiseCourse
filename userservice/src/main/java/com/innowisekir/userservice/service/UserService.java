package com.innowisekir.userservice.service;

import com.innowisekir.userservice.dto.UserDTO;
import com.innowisekir.userservice.entity.User;
import com.innowisekir.userservice.exception.UserNotFoundException;
import com.innowisekir.userservice.mapper.UserMapper;
import com.innowisekir.userservice.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public static final String NOT_FOUND = " not found";
  @Autowired
  public UserService(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }


  public UserDTO createUser(UserDTO userDTO) {
    User user = userMapper.toEntity(userDTO);
    User savedUser = userRepository.save(user);
    return userMapper.toDTO(savedUser);
  }

  public UserDTO getUserById(Long id) {
    return userRepository.findById(id)
        .map(userMapper::toDTO)
        .orElseThrow(() -> new UserNotFoundException("User which id " + id + NOT_FOUND ));
  }

  public List<UserDTO> getUsersByIds(List<Long> ids) {
    List<User> users = userRepository.findByIdIn(ids);
    return users.stream()
        .map(userMapper::toDTO)
        .toList();
  }

  public UserDTO findUserByEmail(String email) {
    return userRepository.findByEmail(email)
        .map(userMapper::toDTO)
        .orElseThrow(() -> new UserNotFoundException("User with email " + email + NOT_FOUND ));
  }

  @Transactional
  public UserDTO updateUser(Long id, UserDTO userDTO) {
    userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User with id " + id + NOT_FOUND ));

      User updateuser = userMapper.toEntityForUpdate(userDTO);
      updateuser.setId(id);
      int updatedRows = userRepository.updateUserById(
          id,
          updateuser.getName(),
          updateuser.getSurname(),
          updateuser.getEmail(),
          updateuser.getBirthDate());
    if (updatedRows == 0) {
      throw new UserNotFoundException("Failed to update user with id " + id);
    }

    return getUserById(id);

  }

  @Transactional
  public void deleteUser(Long id) {
    userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User with id " + id + NOT_FOUND ));

    int deletedRows = userRepository.deleteUserByIdNative(id);
    if (deletedRows == 0) {
      throw new UserNotFoundException("Failed to delete user with id " + id);
    }
  }
}
