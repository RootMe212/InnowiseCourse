package com.innowisekir.paymentservice.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
  private int status;
  private String error;
  private String message;
  private LocalDateTime timestamp;
}
