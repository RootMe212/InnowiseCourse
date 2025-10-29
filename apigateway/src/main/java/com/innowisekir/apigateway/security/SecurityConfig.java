package com.innowisekir.apigateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

  @Bean
  public JwtService jwtService(SecProperties secProperties) {
    return new JwtService(secProperties);
  }

  @Bean
  public JwtAuthFilter jwtAuthFilter(SecProperties secProperties, JwtService jwtService) {
    return new JwtAuthFilter(secProperties, jwtService);
  }

}
