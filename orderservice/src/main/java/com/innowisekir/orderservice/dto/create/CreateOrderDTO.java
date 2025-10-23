package com.innowisekir.orderservice.dto.create;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateOrderDTO {

  @NotNull(message = "User ID cannot be null")
  private Long userId;

  @NotBlank(message = "Status cannot be blank")
  private String status;

  @Valid
  private List<CreateOrderItemDTO> items;

}
