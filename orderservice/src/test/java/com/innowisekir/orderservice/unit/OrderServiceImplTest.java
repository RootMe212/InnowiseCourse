package com.innowisekir.orderservice.unit;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innowisekir.orderservice.client.UserClient;
import com.innowisekir.orderservice.dto.create.CreateOrderDTO;
import com.innowisekir.orderservice.dto.create.CreateOrderItemDTO;
import com.innowisekir.orderservice.dto.response.OrderDTO;
import com.innowisekir.orderservice.dto.response.OrderResponse;
import com.innowisekir.orderservice.dto.response.UserDTO;
import com.innowisekir.orderservice.entity.Order;
import com.innowisekir.orderservice.mapper.OrderMapper;
import com.innowisekir.orderservice.repository.OrderRepository;
import com.innowisekir.orderservice.service.OrderServiceImpl;
import com.innowisekir.orderservice.service.kafka.OrderProducer;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Should create order successfully when valid data provided")
class OrderServiceImplTest {

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private OrderMapper orderMapper;

  @Mock
  private UserClient userClient;
  @Mock
  private OrderProducer orderProducer;

  @InjectMocks
  private OrderServiceImpl orderService;

  private CreateOrderDTO createOrderDTO;
  private Order order;
  private UserDTO userDTO;
  private OrderResponse orderResponse;
  private OrderDTO orderDTO;

  @BeforeEach
  @DisplayName("Setup test data")
  void setUp() {
    CreateOrderItemDTO item1 = new CreateOrderItemDTO();
    item1.setItemId(1L);
    item1.setQuantity(2);

    CreateOrderItemDTO item2 = new CreateOrderItemDTO();
    item2.setItemId(2L);
    item2.setQuantity(1);

    createOrderDTO = new CreateOrderDTO();
    createOrderDTO.setUserId(1L);
    createOrderDTO.setStatus("PENDING");
    createOrderDTO.setItems(Arrays.asList(item1, item2));

    order = new Order();
    order.setId(1L);
    order.setUserId(1L);
    order.setStatus("PENDING");
    order.setCreationDate(LocalDateTime.now());

    userDTO = new UserDTO();
    userDTO.setId(1L);
    userDTO.setEmail("user@example.com");
    userDTO.setName("Test User");

    orderDTO = new OrderDTO();
    orderDTO.setId(1L);
    orderDTO.setUserId(1L);
    orderDTO.setStatus("PENDING");
    orderDTO.setCreationDate(LocalDateTime.now());

    orderResponse = new OrderResponse();
    orderResponse.setOrder(orderDTO);
    orderResponse.setUser(userDTO);
  }

  @Test
  @DisplayName("Should create order successfully when valid data provided")
  void shouldCreateOrderSuccessfully() {
    when(orderMapper.toEntity(createOrderDTO)).thenReturn(order);
    when(orderRepository.save(any(Order.class))).thenReturn(order);
    when(userClient.getByEmail("user@example.com")).thenReturn(userDTO);
    when(orderMapper.toDTO(order)).thenReturn(orderResponse.getOrder());

    OrderResponse result = orderService.createOrder(createOrderDTO, "user@example.com");

    assertNotNull(result);
    assertEquals(orderResponse.getOrder().getId(), result.getOrder().getId());
    assertNotNull(userDTO, "userDTO should not be null");
    assertEquals(userDTO.getEmail(), result.getUser().getEmail());
    verify(orderRepository).save(any(Order.class));
    verify(userClient).getByEmail("user@example.com");
    verify(orderProducer).sendEvent(any());
  }

  @Test
  @DisplayName("Should get order by ID when order exists")
  void shouldGetOrderByIdWhenOrderExists() {
    Long orderId = 1L;
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(userClient.getByEmail("user@example.com")).thenReturn(userDTO);
    when(orderMapper.toDTO(order)).thenReturn(orderResponse.getOrder());

    OrderResponse result = orderService.getOrderById(orderId, "user@example.com");

    assertNotNull(result);
    assertEquals(orderId, result.getOrder().getId());
    verify(orderRepository).findById(orderId);
    verify(userClient).getByEmail("user@example.com");
  }

  @Test
  @DisplayName("Should throw exception when order not found by ID")
  void shouldThrowExceptionWhenOrderNotFoundById() {
    Long orderId = 999L;
    when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

    assertThrows(Exception.class, () ->
        orderService.getOrderById(orderId, "user@example.com"));
    verify(orderRepository).findById(orderId);
    verify(userClient, never()).getByEmail(anyString());
  }

  @Test
  @DisplayName("Should get orders by IDs when orders exist")
  void shouldGetOrdersByIdsWhenOrdersExist() {
    // Given
    List<Long> orderIds = Arrays.asList(1L, 2L);
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(orderRepository.findById(2L)).thenReturn(Optional.of(order));
    when(userClient.getByEmail("user@example.com")).thenReturn(userDTO);
    when(orderMapper.toDTO(order)).thenReturn(orderResponse.getOrder());

    List<OrderResponse> result = orderService.getOrdersByIds(orderIds, "user@example.com");

    assertNotNull(result);
    assertEquals(2, result.size());
    verify(orderRepository, times(2)).findById(anyLong());
    verify(userClient, times(2)).getByEmail("user@example.com");
  }

  @Test
  @DisplayName("Should update order status when valid data provided")
  void shouldUpdateOrderStatusWhenValidDataProvided() {
    Long orderId = 1L;
    CreateOrderDTO updateDTO = new CreateOrderDTO();
    updateDTO.setStatus("COMPLETED");

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(orderRepository.updateOrderById(anyString(), anyLong())).thenReturn(1);
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(userClient.getByEmail("user@example.com")).thenReturn(userDTO);
    when(orderMapper.toDTO(order)).thenReturn(orderResponse.getOrder());

    OrderResponse result = orderService.updateOrder(orderId, updateDTO, "user@example.com");

    assertNotNull(result);
    verify(orderRepository).updateOrderById("COMPLETED", orderId);
    verify(userClient).getByEmail("user@example.com");
  }

  @Test
  @DisplayName("Should delete order when order exists")
  void shouldDeleteOrderWhenOrderExists() {
    Long orderId = 1L;
    when(orderRepository.existsById(orderId)).thenReturn(true);
    when(orderRepository.deleteOrderById(orderId)).thenReturn(1);

    orderService.deleteOrder(orderId);

    verify(orderRepository).existsById(orderId);
    verify(orderRepository).deleteOrderById(orderId);
  }

  @Test
  @DisplayName("Should throw exception when deleting non-existent order")
  void shouldThrowExceptionWhenDeletingNonExistentOrder() {
    Long orderId = 999L;
    when(orderRepository.existsById(orderId)).thenReturn(false);

    assertThrows(Exception.class, () -> orderService.deleteOrder(orderId));
    verify(orderRepository).existsById(orderId);
    verify(orderRepository, never()).deleteOrderById(anyLong());
  }

}
