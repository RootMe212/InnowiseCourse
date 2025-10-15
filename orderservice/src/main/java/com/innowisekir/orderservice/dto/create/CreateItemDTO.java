package com.innowisekir.orderservice.dto.create;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateItemDTO {

  @NotBlank(message = "Name cannot be blank")
  @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
  private String name;

  @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
  private BigDecimal price;
}
