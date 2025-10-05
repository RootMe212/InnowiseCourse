package com.innowisekir.authservice.dto;

import lombok.Data;

@Data
public class UserInfo {
  private String email;
  private String name;
  private String surname;
  private Boolean isActive;
}
