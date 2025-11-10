package com.innowisekir.apigateway.dto;

import lombok.Data;

@Data
public class AuthRegisterRequest {

  private String username;
  private String password;
  private Long userId;

  public AuthRegisterRequest(String username, String password, Long userId) {
    this.username = username;
    this.password = password;
    this.userId = userId;
  }
}