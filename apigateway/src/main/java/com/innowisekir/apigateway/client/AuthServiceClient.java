package com.innowisekir.apigateway.client;

import com.innowisekir.apigateway.dto.AuthRegisterRequest;
import com.innowisekir.apigateway.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthServiceClient {
  private final WebClient authServiceWebClient;

  public Mono<TokenResponse> createCredentials(AuthRegisterRequest authRegisterRequest) {
    return authServiceWebClient
        .post()
        .uri("/api/v1/auth/register")
        .bodyValue(authRegisterRequest)
        .retrieve().bodyToMono(TokenResponse.class);
  }

}
