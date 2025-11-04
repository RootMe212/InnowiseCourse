package com.innowisekir.apigateway.security;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "security.jwt")
@Data
public class SecProperties {

  private String secret;
  private String issuer;
  private Long allowedClockSkewInSeconds = 60L;
  private List<String> allowlist = List.of(
      "/api/v1/auth/login",
      "/api/v1/auth/register"
  );


}
