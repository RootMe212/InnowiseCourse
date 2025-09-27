package com.innowisekir.userservice.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardInfoDTO {

  private Long id;

  private Long userId;

  @NotBlank(message = "Card number cannot be blank")
  @Pattern(regexp = "\\d{5}$")
  private String number;

  private String holder;

  @NotNull(message = "Expiration date cannot be null")
  private LocalDate expirationDate;
}
