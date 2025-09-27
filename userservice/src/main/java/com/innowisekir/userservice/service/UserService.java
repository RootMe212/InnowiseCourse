package com.innowisekir.userservice.service;

import com.innowisekir.userservice.dto.UserDTO;
import com.innowisekir.userservice.entity.User;
import com.innowisekir.userservice.exception.InvalidEmailException;
import com.innowisekir.userservice.exception.UserNotFoundException;
import com.innowisekir.userservice.mapper.CardInfoListMapper;
import com.innowisekir.userservice.mapper.UserMapper;
import com.innowisekir.userservice.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public static final String NOT_FOUND = " not found";
  private final CardInfoListMapper cardInfoListMapper;

  @Autowired
  public UserService(UserRepository userRepository, UserMapper userMapper,
      CardInfoListMapper cardInfoListMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;

    this.cardInfoListMapper = cardInfoListMapper;
  }

  @CachePut(value = "USER_CACHE", key = "#result.id")
  public UserDTO createUser(UserDTO userDTO) {
    if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
      throw new InvalidEmailException("Email already exists");
    }
    User user = userMapper.toEntity(userDTO);
    User savedUser = userRepository.save(user);

    return returnUserDTO(savedUser);
  }

  @Cacheable(value = "USER_CACHE", key = "#id")
  public UserDTO getUserById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User which id " + id + NOT_FOUND));

    return returnUserDTO(user);
  }

  @Cacheable(value = "USER_CACHE", key = "'all:' + #ids.hashCode()")
  public List<UserDTO> getUsersByIds(List<Long> ids) {
    List<User> users = userRepository.findByIdIn(ids);
    return users.stream()
        .map(this::returnUserDTO)
        .toList();
  }

  @Cacheable(value = "USER_CACHE", key = "#email")
  public UserDTO getUserByEmail(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("User with email " + email + NOT_FOUND));

    return returnUserDTO(user);
  }


  @Transactional
  @CachePut(value = "USER_CACHE", key = "#result.id")
  public UserDTO updateUser(Long id, UserDTO userDTO) {
    userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User with id " + id + NOT_FOUND));

    Optional<User> existingUser = userRepository.findByEmail(userDTO.getEmail());
    if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
      throw new InvalidEmailException("Email already exists");
    }

    userRepository.updateUserById(
        id,
        userDTO.getName(),
        userDTO.getSurname(),
        userDTO.getEmail(),
        userDTO.getBirthDate()
    );

    User updatedUser = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User with id " + id + NOT_FOUND));

    return returnUserDTO(updatedUser);

  }

  @Transactional
  @CacheEvict(value = "USER_CACHE", key = "#id")
  public void deleteUser(Long id) {
    userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User with id " + id + NOT_FOUND));

    userRepository.deleteUserByIdNative(id);
  }

  private UserDTO returnUserDTO(User user) {
    UserDTO userDTO = userMapper.toDTO(user);
    if (userDTO.getCards() != null) {
      userDTO.setCards(cardInfoListMapper.toDTOList(user.getCards()));
    }
    return userDTO;
  }
}