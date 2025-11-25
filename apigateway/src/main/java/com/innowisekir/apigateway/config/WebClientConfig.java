package com.innowisekir.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

  @Value("${services.user-service.url:http://localhost:8082}")
  private String userServiceUrl;

  @Value("${services.auth-service.url:http://localhost:8081}")
  private String authServiceUrl;

  @Bean
  public WebClient userServiceWebClient() {
    return WebClient.builder()
        .baseUrl(userServiceUrl)
        .build();
  }

  @Bean
  public WebClient authServiceWebClient() {
    return WebClient.builder()
        .baseUrl(authServiceUrl)
        .build();
  }
}