package com.innowisekir.authservice.service.serv;

import com.innowisekir.authservice.dto.RefreshTokenRequest;
import com.innowisekir.authservice.dto.TokenResponse;

public interface TokenManagementService {

  TokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

  TokenResponse issueTokens(String username, Long userId);

  void validateToken(String token);

  void logout(String refreshToken);
}
