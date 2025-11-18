package com.innowisekir.paymentservice.dto.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentEvent {
  private String paymentId;
  private Long orderId;
  private Long userId;
  private String status;
  private BigDecimal paymentAmount;
  private LocalDateTime timestamp;

}