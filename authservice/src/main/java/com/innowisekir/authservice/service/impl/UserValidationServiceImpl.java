package com.innowisekir.authservice.service.impl;

import com.innowisekir.authservice.exception.UserAlreadyExistsException;
import com.innowisekir.authservice.exception.UserNotFoundException;
import com.innowisekir.authservice.repository.UserCredentialsRepository;
import com.innowisekir.authservice.service.UserServiceClient;
import com.innowisekir.authservice.service.serv.UserValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidationServiceImpl implements UserValidationService {

  private final UserServiceClient userServiceClient;
  private final UserCredentialsRepository userCredentialsRepository;

  @Override
  public void ensureUserExistsInUserService(Long userId) {
    if (!userServiceClient.userExists(userId)) {
      throw new UserNotFoundException("User with id " + userId + " does not exist");
    }
  }

  @Override
  public void ensureUsernameAvailable(String username) {
    if (userCredentialsRepository.existsByUsername(username)) {
      throw new UserAlreadyExistsException("Username already exists");
    }
  }

  @Override
  public void ensureNoCredentialsForUserId(Long userId) {
    if (userCredentialsRepository.findByUserId(userId).isPresent()) {
      throw new UserAlreadyExistsException("User credentials already exists for user id " + userId);
    }
  }
}
