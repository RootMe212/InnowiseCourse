package com.innowisekir.authservice.service.impl;

import com.innowisekir.authservice.dto.RefreshTokenRequest;
import com.innowisekir.authservice.dto.TokenResponse;
import com.innowisekir.authservice.entity.RefreshToken;
import com.innowisekir.authservice.exception.InvalidTokenException;
import com.innowisekir.authservice.exception.TokenExpiredException;
import com.innowisekir.authservice.repository.RefreshTokenRepository;
import com.innowisekir.authservice.service.JwtService;
import com.innowisekir.authservice.service.serv.TokenManagementService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenManagementServiceImpl implements TokenManagementService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtService jwtService;

  public TokenResponse issueTokens(String username, Long userId) {
    refreshTokenRepository.revokeAllUserTokens(userId);

    String accessToken = jwtService.generateAccessToken(username, userId);
    String refreshToken = jwtService.generateRefreshToken(username, userId);

    saveRefreshToken(refreshToken, userId);

    return TokenResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .expiresIn(900L) // 15 minutes
        .build();
  }


  @Override
  @Transactional
  public TokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {

    RefreshToken refreshToken = refreshTokenRepository.findByToken(
            refreshTokenRequest.getRefreshToken())
        .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

    if (refreshToken.getIsRevoked()) {
      throw new InvalidTokenException("Refresh token has been revoked");
    }

    if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new TokenExpiredException("Refresh token has expired");
    }

    String username = jwtService.extractUsername(refreshTokenRequest.getRefreshToken());
    if (!jwtService.validateToken(refreshTokenRequest.getRefreshToken(), username) ||
        !jwtService.isRefreshToken(refreshTokenRequest.getRefreshToken())) {
      throw new InvalidTokenException("Invalid refresh token");
    }

    refreshToken.setIsRevoked(true);
    refreshTokenRepository.save(refreshToken);

    String newAccessToken = jwtService.generateAccessToken(username, refreshToken.getUserId());
    String newRefreshToken = jwtService.generateRefreshToken(username, refreshToken.getUserId());

    saveRefreshToken(newRefreshToken, refreshToken.getUserId());

    return TokenResponse.builder()
        .accessToken(newAccessToken)
        .refreshToken(newRefreshToken)
        .expiresIn(900L)
        .build();
  }

  @Override
  public void validateToken(String token) {
    String username = jwtService.extractUsername(token);
    if (!jwtService.validateToken(token, username)) {
      throw new InvalidTokenException("Invalid token");
    }
  }

  @Override
  @Transactional
  public void logout(String refreshToken) {

    RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
        .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

    token.setIsRevoked(true);
    refreshTokenRepository.save(token);
  }

  private void saveRefreshToken(String token, Long userId) {
    RefreshToken refreshToken = RefreshToken.builder()
        .token(token)
        .userId(userId)
        .expiresAt(LocalDateTime.now().plusDays(7))
        .isRevoked(false)
        .build();

    refreshTokenRepository.save(refreshToken);
  }
}
