package com.innowisekir.orderservice.service;

import com.innowisekir.orderservice.client.UserClient;
import com.innowisekir.orderservice.dto.create.CreateOrderDTO;
import com.innowisekir.orderservice.dto.response.OrderResponse;
import com.innowisekir.orderservice.dto.response.UserDTO;
import com.innowisekir.orderservice.entity.Order;
import com.innowisekir.orderservice.exception.OrderNotFoundException;
import com.innowisekir.orderservice.exception.UserServiceException;
import com.innowisekir.orderservice.mapper.OrderMapper;
import com.innowisekir.orderservice.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final OrderMapper orderMapper;
  private final UserClient userClient;

  public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper,
      UserClient userClient) {
    this.orderRepository = orderRepository;
    this.orderMapper = orderMapper;
    this.userClient = userClient;
  }

  @Override
  @Transactional
  public OrderResponse createOrder(CreateOrderDTO createOrderDTO, String userEmail) {
    Order order = orderMapper.toEntity(createOrderDTO);
    order.setCreationDate(LocalDateTime.now());
    Order savedOrder = orderRepository.save(order);
    return buildOrderResponse(savedOrder,userEmail);
  }

  private OrderResponse buildOrderResponse(Order savedOrder, String userEmail) {
    OrderResponse orderResponse = new OrderResponse();
    orderResponse.setOrder(orderMapper.toDTO(savedOrder));

    if (userEmail != null && !userEmail.isBlank()) {
      try {
        UserDTO userDTO = userClient.getByEmail(userEmail);
        orderResponse.setUser(userDTO);
      } catch (Exception e) {
        throw new UserServiceException("Failed to get user by email: " + userEmail, e);
      }
    }
    return orderResponse;
  }

  @Override
  public OrderResponse getOrderById(Long id, String userEmail) {
    Order order = orderRepository.findById(id)
        .orElseThrow(() -> new OrderNotFoundException("Order with id " + id + " not found"));
    return buildOrderResponse(order,userEmail);
  }

  @Override
  public List<OrderResponse> getOrdersByIds(List<Long> ids, String userEmail) {
    return ids.stream()
        .map(id -> getOrderById(id, userEmail))
        .toList();
  }

  @Override
  public List<OrderResponse> getOrdersByStatuses(List<String> statuses, String userEmail) {
    return orderRepository.findOrdersByStatuses(statuses).stream()
        .map(order -> getOrderById(order.getId(), userEmail))
        .toList();
  }

  @Override
  @Transactional
  public OrderResponse updateOrder(Long id, CreateOrderDTO orderDTO, String userEmail) {
    Order existing = orderRepository.findById(id)
        .orElseThrow(() -> new OrderNotFoundException("Order with id " + id + " not found"));

    orderRepository.updateOrderById(orderDTO.getStatus(), id);

    if (orderDTO.getItems() != null && !orderDTO.getItems().isEmpty()) {
      existing.getOrderItems().clear();

      Order updatedFromDto = orderMapper.toEntity(orderDTO);
      existing.getOrderItems().addAll(updatedFromDto.getOrderItems());

      orderRepository.save(existing);
    }

    Order updated = orderRepository.findById(id).orElseThrow();
    return buildOrderResponse(updated, userEmail);
  }

  @Override
  @Transactional
  public void deleteOrder(Long id) {
    if (!orderRepository.existsById(id)) {
      throw new OrderNotFoundException("Order with id " + id + " not found");
    }
    orderRepository.deleteOrderById(id);
  }
}
