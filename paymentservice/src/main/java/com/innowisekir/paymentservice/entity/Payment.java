package com.innowisekir.paymentservice.entity;


import com.innowisekir.paymentservice.entity.status.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class Payment {

  @Id
  private String id;

  private Long orderId;

  private Long userId;

  private PaymentStatus status;

  private LocalDateTime timestamp;

  private BigDecimal paymentAmount;


}
