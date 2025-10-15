package com.innowisekir.authservice.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class UserDTO {
  private Long id;
  private String name;
  private String surname;
  private LocalDate birthDate;
  private String email;
}
