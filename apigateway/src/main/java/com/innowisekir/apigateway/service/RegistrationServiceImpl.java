package com.innowisekir.apigateway.service;

import com.innowisekir.apigateway.client.AuthServiceClient;
import com.innowisekir.apigateway.client.UserServiceClient;
import com.innowisekir.apigateway.dto.AuthRegisterRequest;
import com.innowisekir.apigateway.dto.CreateUserRequest;
import com.innowisekir.apigateway.dto.RegisterRequest;
import com.innowisekir.apigateway.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

  private final UserServiceClient userServiceClient;
  private final AuthServiceClient authServiceClient;

  @Override
  public Mono<TokenResponse> register(RegisterRequest registerRequest) {
    return createUser(registerRequest)
        .flatMap(userId -> {
          log.info("User registered successfully");

          return createCredentials(registerRequest, userId)
              .onErrorResume(error -> {
                log.error("Failed to create credentials, rolling back user: {}", userId);
                return rollbackUser(userId).then(Mono.error(error));
              });
        })
        .doOnSuccess(response -> log.info("Registration completed successfully"))
        .doOnError(error -> log.error("Registration failed: {}", error.getMessage()));
  }

  private Mono<Long> createUser(RegisterRequest registerRequest) {
    var createUserRequest = new CreateUserRequest(
        registerRequest.getName(),
        registerRequest.getSurname(),
        registerRequest.getBirthDate(),
        registerRequest.getEmail()
    );

    return userServiceClient.createUser(createUserRequest);
  }

  private Mono<TokenResponse> createCredentials(RegisterRequest registerRequest, Long userId) {
    var authRegisterRequest = new AuthRegisterRequest(
        registerRequest.getUsername(),
        registerRequest.getPassword(),
        userId
    );

    return authServiceClient.createCredentials(authRegisterRequest);
  }

  private Mono<Void> rollbackUser(Long userId) {
    return userServiceClient.rollbackUser(userId);
  }
}
