package com.innowisekir.apigateway.controller;


import com.innowisekir.apigateway.dto.RegisterRequest;
import com.innowisekir.apigateway.dto.TokenResponse;
import com.innowisekir.apigateway.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class RegistrationController {

  private final RegistrationService registrationService;

  @PostMapping("/register")
  public Mono<ResponseEntity<TokenResponse>> register(
      @Valid @RequestBody RegisterRequest registerRequest) {
    return registrationService.register(registerRequest)
        .map(ResponseEntity::ok)
        .onErrorReturn(ResponseEntity.badRequest().build());
  }

}
