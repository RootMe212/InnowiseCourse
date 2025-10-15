package com.innowisekir.authservice.controller;

import com.innowisekir.authservice.dto.LoginRequest;
import com.innowisekir.authservice.dto.RefreshTokenRequest;
import com.innowisekir.authservice.dto.RegisterRequest;
import com.innowisekir.authservice.dto.TokenResponse;
import com.innowisekir.authservice.exception.InvalidTokenException;
import com.innowisekir.authservice.service.serv.AuthService;
import com.innowisekir.authservice.service.serv.TokenManagementService;
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
  private final TokenManagementService tokenManagementService;

  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
    TokenResponse response = authService.login(loginRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/register")
  public ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
    TokenResponse response = authService.register(registerRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/refresh")
  public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
    TokenResponse response = tokenManagementService.refreshToken(refreshTokenRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/validate")
  public ResponseEntity<Void> validateToken(@RequestHeader("Authorization") String authHeader) {

    if (authHeader == null || authHeader.startsWith("Bearer ")) {
      throw new InvalidTokenException("Invalid authorization header");
    }
    String token = authHeader.substring(7); // Remove "Bearer " prefix
    tokenManagementService.validateToken(token);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
    tokenManagementService.logout(refreshTokenRequest.getRefreshToken());
    return ResponseEntity.ok().build();
  }
}