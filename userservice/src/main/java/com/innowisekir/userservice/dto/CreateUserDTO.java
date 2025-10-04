package com.innowisekir.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDTO {

  @NotBlank(message = "Name cannot be blank")
  private String name;

  @NotBlank(message = "Surname cannot be blank")
  private String surname;

  @NotNull(message = "Birth date cannot be null")
  @Past(message = "Birth date must be in the past")
  private LocalDate birthDate;

  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Invalid email format")
  private String email;
}
