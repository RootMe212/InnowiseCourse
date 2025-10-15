package com.innowisekir.authservice.service;

import com.innowisekir.authservice.dto.LoginRequest;
import com.innowisekir.authservice.dto.RefreshTokenRequest;
import com.innowisekir.authservice.dto.RegisterRequest;
import com.innowisekir.authservice.dto.TokenResponse;

public interface AuthService {

  TokenResponse login(LoginRequest loginRequest);

  TokenResponse register(RegisterRequest registerRequest);

  TokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

  void validateToken(String token);

  void logout(String refreshToken);
}
