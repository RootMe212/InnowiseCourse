package com.innowisekir.apigateway.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateUserRequest {

  private String name;
  private String surname;
  private LocalDate birthDate;
  private String email;

  public CreateUserRequest(String name, String surname, LocalDate birthDate, String email) {
    this.name = name;
    this.surname = surname;
    this.birthDate = birthDate;
    this.email = email;
  }
}