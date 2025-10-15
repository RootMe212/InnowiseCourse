package com.innowisekir.orderservice.dto.create;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderItemDTO {

  @NotNull(message = "Item ID cannot be null")
  private Long itemId;

  @Min(value = 1, message = "Quantity must be at least 1")
  private Integer quantity;
}
