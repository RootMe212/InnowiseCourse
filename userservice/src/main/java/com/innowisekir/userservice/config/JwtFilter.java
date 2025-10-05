package com.innowisekir.userservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtFilter extends OncePerRequestFilter {

  @Value("${spring.security.jwt.secret:mySecretKey123456789012345678901234567890}")
  private String secret;

  private SecretKey key() {
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws  IOException {
    String header = request.getHeader("Authorization");
    if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
      unauthorized(response, "Missing or invalid Authorization header");
      return;
    }
    String token = header.substring(7);
    try {
      Jwts.parser().setSigningKey(key()).build().parseClaimsJws(token).getBody();
      chain.doFilter(request, response);
    } catch (Exception ex) {
      unauthorized(response, "Invalid token");
    }
  }

  private void unauthorized(HttpServletResponse response, String message) throws IOException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType("application/json");
    new ObjectMapper().writeValue(response.getWriter(), java.util.Map.of("error", "UNAUTHORIZED", "message", message));
  }
}