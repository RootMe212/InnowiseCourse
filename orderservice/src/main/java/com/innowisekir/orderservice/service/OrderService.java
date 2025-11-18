package com.innowisekir.orderservice.service;

import com.innowisekir.orderservice.dto.create.CreateOrderDTO;
import com.innowisekir.orderservice.dto.response.OrderResponse;
import java.util.List;

public interface OrderService {

  OrderResponse createOrder(CreateOrderDTO orderDTO, String userEmail);

  OrderResponse getOrderById(Long id, String userEmail);

  List<OrderResponse> getOrdersByIds(List<Long> ids, String userEmail);

  List<OrderResponse> getOrdersByStatuses(List<String> statuses, String userEmail);

  OrderResponse updateOrder(Long id, CreateOrderDTO orderDTO, String userEmail);

  void deleteOrder(Long id);
  void updateOrderStatus(Long orderId, String status);

}
