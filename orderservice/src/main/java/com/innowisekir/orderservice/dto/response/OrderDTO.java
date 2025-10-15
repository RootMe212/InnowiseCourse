package com.innowisekir.orderservice.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderDTO {

  private Long id;
  private Long userId;
  private String status;
  private LocalDateTime creationDate;
  private List<OrderItemDTO> items;

}
