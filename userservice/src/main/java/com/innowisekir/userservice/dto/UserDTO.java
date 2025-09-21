package com.innowisekir.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserDTO {
  private Long id;

  @NotBlank
  private String name;

  @NotBlank
  private String surname;

  @NotNull
  @Past
  private LocalDate birthDate;

  @NotBlank
  @Email
  private String email;

  private List<CardInfoDTO> cards;
}
