package com.innowisekir.apigateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Supplier;
import javax.crypto.SecretKey;
import reactor.core.publisher.Mono;

public class JwtService {

  private final Supplier<SecretKey> secretKeySupplier;
  private final String issuer;
  private final long allowedSkewMills;

  public JwtService(SecProperties secProperties) {
    this.secretKeySupplier = () -> Keys.hmacShaKeyFor(secProperties.getSecret().getBytes(
        StandardCharsets.UTF_8));
    this.issuer = secProperties.getIssuer();
    this.allowedSkewMills = secProperties.getAllowedClockSkewInSeconds() * 1000L;
  }

  public Mono<Claims> parseAndValidate(String token) {
    return Mono.fromSupplier(() -> {
      try {
        return Jwts.parserBuilder()
            .setSigningKey(secretKeySupplier.get())
            .setAllowedClockSkewSeconds(allowedSkewMills / 1000)
            .build()
            .parseClaimsJws(token)
            .getBody();
      } catch (Exception e) {
        System.err.println("JWT parsing error: " + e.getMessage());
        throw e;
      }
    }).flatMap(claims -> {
      Date now = new Date();
      if (claims.getExpiration() != null && claims.getExpiration().before(now)) {
        return Mono.error(new IllegalStateException("Token expired"));
      }
      return Mono.just(claims);
    });
  }
}
