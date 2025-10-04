package com.innowisekir.userservice.service;

import com.innowisekir.userservice.dto.UserDTO;
import com.innowisekir.userservice.entity.User;
import com.innowisekir.userservice.exception.EntityAlreadyDeletedException;
import com.innowisekir.userservice.exception.InvalidEmailException;
import com.innowisekir.userservice.exception.UserNotFoundException;
import com.innowisekir.userservice.mapper.CardInfoMapper;
import com.innowisekir.userservice.mapper.UserMapper;
import com.innowisekir.userservice.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public static final String NOT_FOUND = " not found";
  private final CardInfoMapper cardInfoMapper;

  public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,
      CardInfoMapper cardInfoMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;

    this.cardInfoMapper = cardInfoMapper;
  }

  @Override
  @CachePut(value = "USER_CACHE", key = "#result.id", condition = "#result != null and #result.id != null")
  public UserDTO createUser(UserDTO userDTO) {
    if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
      throw new InvalidEmailException("Email already exists");
    }
    User user = userMapper.toEntity(userDTO);
    User savedUser = userRepository.save(user);

    return returnUserDTO(savedUser);
  }

  @Override
  @Cacheable(value = "USER_CACHE", key = "#id", condition = "#id != null")
  public UserDTO getUserById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User which id " + id + NOT_FOUND));

    return returnUserDTO(user);
  }

  @Override
  @Cacheable(value = "USER_CACHE", key = "'all:' + #ids.hashCode()", condition = "#ids != null")
  public List<UserDTO> getUsersByIds(List<Long> ids) {
    List<User> users = userRepository.findByIdIn(ids);
    return users.stream()
        .map(this::returnUserDTO)
        .toList();
  }

  @Override
  @Cacheable(value = "USER_CACHE", key = "#email", condition = "#email != null")
  public UserDTO getUserByEmail(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("User with email " + email + NOT_FOUND));

    return returnUserDTO(user);
  }

  @Override
  @Transactional
  @CachePut(value = "USER_CACHE", key = "#result.id", condition = "#result != null and #result.id != null")
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

  @Override
  @Transactional
  @CacheEvict(value = "USER_CACHE", key = "#id", condition = "#id != null")
  public void deleteUser(Long id) {
    if (!userRepository.existsById(id)) {
      throw new EntityAlreadyDeletedException("User with id " + id + " has already been deleted or does not exist");
    }

    userRepository.deleteUserByIdNative(id);
  }

  private UserDTO returnUserDTO(User user) {
    UserDTO userDTO = userMapper.toDTO(user);
    if (userDTO.getCards() != null) {
      userDTO.setCards(cardInfoMapper.toDTOList(user.getCards()));
    }
    return userDTO;
  }
}