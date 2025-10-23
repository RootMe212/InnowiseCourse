package com.innowisekir.authservice.service;

import com.innowisekir.authservice.dto.CreateUserDTO;
import com.innowisekir.authservice.dto.UserDTO;
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
      String url = userServiceUrl + "/api/v1/users/" + userId;
      ResponseEntity<UserDTO> response = restTemplate.getForEntity(url, UserDTO.class);
      return response.getStatusCode().is2xxSuccessful() && response.getBody() != null;
    } catch (Exception e) {
      return false;
    }
  }

  public UserDTO createUser(CreateUserDTO createUserDTO) {
    String url = userServiceUrl + "/api/v1/users";
    ResponseEntity<UserDTO> response = restTemplate.postForEntity(url, createUserDTO,
        UserDTO.class);
    if (response.getBody() == null || response.getBody().getId() == null) {
      throw new RuntimeException("User creation failed");
    }
    return response.getBody();
  }
}