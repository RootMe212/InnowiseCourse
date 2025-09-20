package com.innowisekir.userservice.service;

import com.innowisekir.userservice.entities.User;
import com.innowisekir.userservice.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }


  public User createUser(User user) {
    return userRepository.save(user);
  }

  public Optional<User> getUserById(Long id) {
    return userRepository.findById(id);
  }

  public List<User> getUsersByIds(List<Long> ids) {
    return userRepository.findByIdIn(ids);
  }

  public Optional<User> findUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public boolean updateUser(Long id, User user) {
    int updatedRows = userRepository.updateUserById(
        id,
        user.getName(),
        user.getSurname(),
        user.getEmail(),
        user.getBirthDate());
    return updatedRows > 0;
  }

  public boolean deleteUser(Long id) {
    int deletedRows = userRepository.deleteUserByIdNative(id);
    return deletedRows > 0;
  }
}
