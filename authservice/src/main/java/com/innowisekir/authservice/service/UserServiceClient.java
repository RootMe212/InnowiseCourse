package com.innowisekir.authservice.service;

import com.innowisekir.authservice.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

  private final RestTemplate restTemplate;

  @Value("${user.service.url:http://localhost:8080}")
  private String userServiceUrl;

  public boolean userExists(Long userId) {
    try {
      String url = userServiceUrl + "/api/v1/users/" + userId + "/exists";
      ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
      return response.getBody() != null && response.getBody();
    } catch (Exception e) {
      return false;
    }
  }

  public UserInfo getUserInfo(Long userId) {
    try {
      String url = userServiceUrl + "/api/v1/users/" + userId;
      ResponseEntity<UserInfo> response = restTemplate.getForEntity(url, UserInfo.class);
      return response.getBody();
    } catch (Exception e) {
      throw new RuntimeException("User not found");
    }
  }

  public void validateUserForRegistration(Long userId) {
    if (!userExists(userId)) {
      throw new RuntimeException("User with ID " + userId + " does not exist in User Service");
    }
  }
}