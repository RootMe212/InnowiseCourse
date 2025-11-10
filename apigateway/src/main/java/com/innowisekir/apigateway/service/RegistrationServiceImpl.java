package com.innowisekir.apigateway.service;

import com.innowisekir.apigateway.dto.AuthRegisterRequest;
import com.innowisekir.apigateway.dto.CreateUserRequest;
import com.innowisekir.apigateway.dto.RegisterRequest;
import com.innowisekir.apigateway.dto.TokenResponse;
import com.innowisekir.apigateway.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

  private final WebClient userServiceClient;
  private final WebClient authServiceClient;

  @Override
  public Mono<TokenResponse> register(RegisterRequest registerRequest) {
    return createUser(registerRequest)
        .flatMap(userId -> {
          log.info("User registered successfully");

          return createCredentials(registerRequest,userId)
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

    return userServiceClient
        .post()
        .uri("/api/v1/users")
        .bodyValue(createUserRequest)
        .retrieve().bodyToMono(UserResponse.class)
        .map(UserResponse::getId);
  }

  private Mono<TokenResponse> createCredentials(RegisterRequest registerRequest,Long userId) {
    var authRegisterRequest = new AuthRegisterRequest(
        registerRequest.getUsername(),
        registerRequest.getPassword(),
        userId
    );

    return authServiceClient
        .post()
        .uri("/api/v1/auth/register")
        .bodyValue(authRegisterRequest)
        .retrieve().bodyToMono(TokenResponse.class);
  }

  private Mono<Void> rollbackUser(Long userId) {
    log.info("Rolling back user creation for ID: {}", userId);

    return userServiceClient
        .delete()
        .uri("/api/v1/users/{id}", userId)
        .retrieve()
        .bodyToMono(Void.class)
        .doOnSuccess(v -> log.info("User {} rolled back", userId))
        .doOnError(err -> log.error("User {} rollBack failed : {}", userId,err.getMessage()));
  }

}
