package com.innowisekir.paymentservice.dto.event;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEvent {
  private Long itemId;
  private Integer quantity;
  private BigDecimal price;

}
