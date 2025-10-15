package com.innowisekir.orderservice.controller;

import com.innowisekir.orderservice.dto.create.CreateOrderDTO;
import com.innowisekir.orderservice.dto.response.OrderResponse;
import com.innowisekir.orderservice.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @PostMapping
  public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderDTO orderDTO,
      @RequestParam("userEmail") String userEmail) {

    OrderResponse orderResponse = orderService.createOrder(orderDTO, userEmail);
    return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id,
      @RequestParam("userEmail") String userEmail) {
    OrderResponse orderResponse = orderService.getOrderById(id, userEmail);
    return ResponseEntity.ok(orderResponse);
  }

  @GetMapping("/ids")
  public ResponseEntity<List<OrderResponse>> getOrdersByIds(@RequestParam("ids") List<Long> ids
      , @RequestParam("userEmail") String userEmail) {
    List<OrderResponse> orderResponses = orderService.getOrdersByIds(ids, userEmail);
    return ResponseEntity.ok(orderResponses);
  }

  @GetMapping("/statuses")
  public ResponseEntity<List<OrderResponse>> getOrdersByStatuses(
      @RequestParam("statuses") List<String> statuses,
      @RequestParam("userEmail") String userEmail) {
    List<OrderResponse> responses = orderService.getOrdersByStatuses(statuses, userEmail);
    return ResponseEntity.ok(responses);
  }

  @PutMapping("/{id}")
  public ResponseEntity<OrderResponse> updateOrder(
      @PathVariable Long id,
      @Valid @RequestBody CreateOrderDTO orderDTO,
      @RequestParam("userEmail") String userEmail) {
    OrderResponse response = orderService.updateOrder(id, orderDTO, userEmail);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
    orderService.deleteOrder(id);
    return ResponseEntity.noContent().build();
  }

}
