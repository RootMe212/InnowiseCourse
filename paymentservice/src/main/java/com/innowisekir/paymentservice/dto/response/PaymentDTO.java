package com.innowisekir.paymentservice.dto.response;


import com.innowisekir.paymentservice.entity.status.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentDTO {

  private String id;
  private Long orderId;
  private Long userId;
  private PaymentStatus status;
  private LocalDateTime timestamp;
  private BigDecimal paymentAmount;
}
