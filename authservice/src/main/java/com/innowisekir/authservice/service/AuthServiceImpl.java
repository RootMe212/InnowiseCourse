package com.innowisekir.authservice.service;

import com.innowisekir.authservice.dto.LoginRequest;
import com.innowisekir.authservice.dto.RefreshTokenRequest;
import com.innowisekir.authservice.dto.RegisterRequest;
import com.innowisekir.authservice.dto.TokenResponse;
import com.innowisekir.authservice.entity.RefreshToken;
import com.innowisekir.authservice.entity.UserCredentials;
import com.innowisekir.authservice.exception.InvalidCredentialsException;
import com.innowisekir.authservice.exception.InvalidTokenException;
import com.innowisekir.authservice.exception.TokenExpiredException;
import com.innowisekir.authservice.exception.UserAlreadyExistsException;
import com.innowisekir.authservice.repository.RefreshTokenRepository;
import com.innowisekir.authservice.repository.UserCredentialsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

  private final UserCredentialsRepository userCredentialsRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordService passwordService;
  private final JwtService jwtService;
  private final UserServiceClient userServiceClient;

  @Override
  @Transactional
  public TokenResponse login(LoginRequest loginRequest) {

    UserCredentials userCredentials = userCredentialsRepository.findByUsername(
            loginRequest.getUsername())
        .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

    if (!passwordService.matches(loginRequest.getPassword(), userCredentials.getPassword())) {
      throw new InvalidCredentialsException("Invalid username or password");
    }

    if (!userCredentials.getIsActive()) {
      throw new InvalidCredentialsException("Account is deactivated");
    }

    refreshTokenRepository.revokeAllUserTokens(userCredentials.getUserId());

    String accessToken = jwtService.generateAccessToken(userCredentials.getUsername(),
        userCredentials.getUserId());
    String refreshToken = jwtService.generateRefreshToken(userCredentials.getUsername(),
        userCredentials.getUserId());

    saveRefreshToken(refreshToken, userCredentials.getUserId());

    return TokenResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .expiresIn(900L) // 15 minutes
        .build();
  }

  @Override
  @Transactional
  public TokenResponse register(RegisterRequest registerRequest) {
    userServiceClient.validateUserForRegistration(registerRequest.getUserId());
    if (userCredentialsRepository.existsByUsername(registerRequest.getUsername())) {
      throw new UserAlreadyExistsException("Username already exists");
    }

    if (userCredentialsRepository.findByUserId(registerRequest.getUserId()).isPresent()) {
      throw new UserAlreadyExistsException("User credentials already exist for this user ID");
    }

    UserCredentials userCredentials = UserCredentials.builder()
        .username(registerRequest.getUsername())
        .password(passwordService.encodePassword(registerRequest.getPassword()))
        .userId(registerRequest.getUserId())
        .isActive(true)
        .build();

    userCredentialsRepository.save(userCredentials);

    String accessToken = jwtService.generateAccessToken(userCredentials.getUsername(),
        userCredentials.getUserId());
    String refreshToken = jwtService.generateRefreshToken(userCredentials.getUsername(),
        userCredentials.getUserId());

    saveRefreshToken(refreshToken, userCredentials.getUserId());

    return TokenResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .expiresIn(900L)
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

    try {
      String username = jwtService.extractUsername(token);
      if (!jwtService.validateToken(token, username)) {
        throw new InvalidTokenException("Invalid token");
      }

      UserCredentials userCredentials = userCredentialsRepository.findByUsername(username)
          .orElseThrow(() -> new InvalidTokenException("User not found"));

      if (!userCredentials.getIsActive()) {
        throw new InvalidTokenException("User account is deactivated");
      }

    } catch (Exception e) {
      throw new InvalidTokenException("Token validation failed");
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
