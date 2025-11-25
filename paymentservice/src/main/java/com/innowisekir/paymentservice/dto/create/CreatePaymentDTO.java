package com.innowisekir.paymentservice.dto.create;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreatePaymentDTO {

  @NotNull(message = "Order ID cannot be null")
  private Long orderId;

  @NotNull(message = "User ID cannot be null")
  private Long userId;

  @NotNull(message = "Payment amount cannot be null")
  @DecimalMin(value = "0.01", message = "Payment amount must be greater than 0")
  private BigDecimal paymentAmount;

}
