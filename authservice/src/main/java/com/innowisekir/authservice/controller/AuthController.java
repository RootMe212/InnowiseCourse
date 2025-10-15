package com.innowisekir.authservice.controller;

import com.innowisekir.authservice.dto.LoginRequest;
import com.innowisekir.authservice.dto.RefreshTokenRequest;
import com.innowisekir.authservice.dto.RegisterRequest;
import com.innowisekir.authservice.dto.TokenResponse;
import com.innowisekir.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
    TokenResponse response = authService.login(loginRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/register")
  public ResponseEntity<TokenResponse> register(
      @Valid @RequestBody RegisterRequest registerRequest) {
    TokenResponse response = authService.register(registerRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/refresh")
  public ResponseEntity<TokenResponse> refreshToken(
      @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
    TokenResponse response = authService.refreshToken(refreshTokenRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/validate")
  public ResponseEntity<Void> validateToken(@RequestHeader("Authorization") String authHeader) {
    String token = authHeader.substring(7); // Remove "Bearer " prefix
    authService.validateToken(token);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
    authService.logout(refreshTokenRequest.getRefreshToken());
    return ResponseEntity.ok().build();
  }
}