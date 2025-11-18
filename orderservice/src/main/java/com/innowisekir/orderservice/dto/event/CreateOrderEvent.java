package com.innowisekir.orderservice.dto.event;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderEvent {
  private Long orderId;
  private Long userId;
  private String status;
  private LocalDateTime creationDate;
  private List<OrderItemEvent> items;

}
