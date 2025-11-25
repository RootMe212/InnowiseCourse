package com.innowisekir.apigateway.client;

import com.innowisekir.apigateway.dto.CreateUserRequest;
import com.innowisekir.apigateway.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceClient {
  private final WebClient userServiceWebClient;

  public Mono<Long> createUser(CreateUserRequest createUserRequest) {
    return userServiceWebClient
        .post()
        .uri("/api/v1/users")
        .bodyValue(createUserRequest)
        .retrieve().bodyToMono(UserResponse.class)
        .map(UserResponse::getId);
  }

  public Mono<Void> rollbackUser(Long userId) {
    log.info("Rolling back user creation for ID: {}", userId);

    return userServiceWebClient
        .delete()
        .uri("/api/v1/users/{id}", userId)
        .retrieve()
        .bodyToMono(Void.class)
        .doOnSuccess(v -> log.info("User {} rolled back", userId))
        .doOnError(err -> log.error("User {} rollBack failed : {}", userId, err.getMessage()));
  }
}
