package com.innowisekir.orderservice.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderResponse {

  private OrderDTO order;
  private UserDTO user;
}
