package com.innowisekir.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDTO {
  @NotBlank
  private String name;

  @NotBlank
  private String surname;

  @Past
  private LocalDate birthDate;

  @NotBlank
  @Email
  private String email;

}
