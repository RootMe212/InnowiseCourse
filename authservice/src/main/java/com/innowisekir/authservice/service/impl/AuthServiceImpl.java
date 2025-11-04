package com.innowisekir.authservice.service.impl;

import com.innowisekir.authservice.dto.LoginRequest;
import com.innowisekir.authservice.dto.RegisterRequest;
import com.innowisekir.authservice.dto.TokenResponse;
import com.innowisekir.authservice.entity.UserCredentials;
import com.innowisekir.authservice.exception.InvalidCredentialsException;
import com.innowisekir.authservice.repository.UserCredentialsRepository;
import com.innowisekir.authservice.service.serv.AuthService;
import com.innowisekir.authservice.service.PasswordService;
import com.innowisekir.authservice.service.serv.TokenManagementService;
import com.innowisekir.authservice.service.UserServiceClient;
import com.innowisekir.authservice.service.serv.UserValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

  private final UserCredentialsRepository userCredentialsRepository;
  private final PasswordService passwordService;
  private final UserServiceClient userServiceClient;
  private final UserValidationService userValidationService;
  private final TokenManagementService tokenManagementService;

  @Override
  @Transactional
  public TokenResponse login(LoginRequest loginRequest) {

    UserCredentials userCredentials = userCredentialsRepository.findByUsername(
            loginRequest.getUsername())
        .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

    if (!passwordService.matches(loginRequest.getPassword(), userCredentials.getPassword())) {
      throw new InvalidCredentialsException("Invalid username or password");
    }

    if (!userCredentials.getIsActive()) {
      throw new InvalidCredentialsException("Account is deactivated");
    }

    return tokenManagementService.issueTokens(userCredentials.getUsername(),
        userCredentials.getUserId());
  }

  @Override
  @Transactional
  public TokenResponse register(RegisterRequest registerRequest) {

    Long userId = registerRequest.getUserId();

    userValidationService.ensureUserExistsInUserService(userId);

    userValidationService.ensureUsernameAvailable(registerRequest.getUsername());
    userValidationService.ensureNoCredentialsForUserId(userId);

    UserCredentials userCredentials = UserCredentials.builder()
        .username(registerRequest.getUsername())
        .password(passwordService.encodePassword(registerRequest.getPassword()))
        .userId(userId)
        .isActive(true)
        .build();

    userCredentialsRepository.save(userCredentials);

    return tokenManagementService.issueTokens(userCredentials.getUsername(), userId);
  }
}
