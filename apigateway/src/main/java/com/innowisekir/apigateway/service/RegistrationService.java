package com.innowisekir.apigateway.service;

import com.innowisekir.apigateway.dto.RegisterRequest;
import com.innowisekir.apigateway.dto.TokenResponse;
import reactor.core.publisher.Mono;

public interface RegistrationService {
  Mono<TokenResponse> register(RegisterRequest registerRequest);

}
