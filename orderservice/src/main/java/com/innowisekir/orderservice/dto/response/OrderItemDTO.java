package com.innowisekir.orderservice.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {

  private Long id;
  private Long itemId;
  private Integer quantity;
  private String itemName;
  private BigDecimal itemPrice;

}
