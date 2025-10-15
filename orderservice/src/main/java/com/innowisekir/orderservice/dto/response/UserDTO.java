package com.innowisekir.orderservice.dto.response;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

  private Long id;
  private String name;
  private String surname;
  private String email;
  private LocalDate birthDate;

}
