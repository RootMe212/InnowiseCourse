package com.innowisekir.orderservice.dto.response;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ItemDTO {

  private Long id;
  private String name;
  private BigDecimal price;
}
