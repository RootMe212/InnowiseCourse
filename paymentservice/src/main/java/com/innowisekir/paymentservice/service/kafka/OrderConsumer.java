package com.innowisekir.paymentservice.service.kafka;

import com.innowisekir.paymentservice.dto.create.CreatePaymentDTO;
import com.innowisekir.paymentservice.dto.event.OrderCreateEvent;
import com.innowisekir.paymentservice.service.PaymentService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderConsumer {
  private final PaymentService paymentService;

  @KafkaListener(topics = "create-order", groupId = "payment-service")
  public void createOrder(OrderCreateEvent event) {
    log.info("Received CREATE_ORDER event: orderId={}, userId={}, status={}, itemsCount={}",
        event.getOrderId(), event.getUserId(), event.getStatus(), event.getItems()
            != null ? event.getItems().size() : 0);
    try {
      if (event.getItems() == null || event.getItems().isEmpty()) {
        log.warn("Order {} has no items, skipping payment creation", event.getOrderId());
        return;
      }

      BigDecimal totalAmount = event.getItems().stream()
          .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
          .reduce(BigDecimal.ZERO, BigDecimal::add);

      CreatePaymentDTO paymentDTO = new CreatePaymentDTO();
      paymentDTO.setOrderId(event.getOrderId());
      paymentDTO.setUserId(event.getUserId());
      paymentDTO.setPaymentAmount(totalAmount);

      paymentService.createPayment(paymentDTO);
      log.info("Payment created for orderId={}, amount={}", event.getOrderId(), totalAmount);
    } catch (Exception ex) {
      log.error("Error processing CREATE_ORDER event for orderId={}: {}",
          event.getOrderId(), ex.getMessage(), ex);
    }
  }
}
