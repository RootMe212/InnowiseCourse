package com.innowisekir.userservice.service;

import com.innowisekir.userservice.dto.UserDTO;
import com.innowisekir.userservice.entity.User;
import com.innowisekir.userservice.mapper.UserMapper;
import com.innowisekir.userservice.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

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

  public Optional<UserDTO> getUserById(Long id) {
    return userRepository.findById(id)
        .map(userMapper::toDTO);
  }

  public List<UserDTO> getUsersByIds(List<Long> ids) {
    List<User> users = userRepository.findByIdIn(ids);
    return users.stream()
        .map(userMapper::toDTO)
        .toList();
  }

  public Optional<UserDTO> findUserByEmail(String email) {
    return userRepository.findByEmail(email)
        .map(userMapper::toDTO) ;
  }

  @Transactional
  public boolean updateUser(Long id, UserDTO userDTO) {
    Optional<User> user = userRepository.findById(id);

    if(user.isPresent()){
      User updateuser = userMapper.toEntityForUpdate(userDTO);
      updateuser.setId(id);
      int updatedRows = userRepository.updateUserById(
          id,
          updateuser.getName(),
          updateuser.getSurname(),
          updateuser.getEmail(),
          updateuser.getBirthDate());
      return updatedRows > 0;
    }

    return false;
  }

  @Transactional
  public boolean deleteUser(Long id) {
    int deletedRows = userRepository.deleteUserByIdNative(id);
    return deletedRows > 0;
  }
}
