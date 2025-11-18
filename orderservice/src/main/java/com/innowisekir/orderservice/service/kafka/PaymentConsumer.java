package com.innowisekir.orderservice.service.kafka;

import com.innowisekir.orderservice.dto.event.CreatePaymentEvent;
import com.innowisekir.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer {

  private final OrderService orderService;

  @KafkaListener(topics = "create-payment", groupId = "order-service")
  public void createPayment(CreatePaymentEvent event) {
    log.info("Received CREATE_PAYMENT event: paymentId={}, orderId={}, status={}",
        event.getPaymentId(), event.getOrderId(), event.getStatus());

    try {
      if ("SUCCESS".equals(event.getStatus())) {
        orderService.updateOrderStatus(event.getOrderId(),"PAID");
        log.info("Order {} status updated to PAID", event.getOrderId());
      } else if ("FAILED".equals(event.getStatus())) {
        orderService.updateOrderStatus(event.getOrderId(),"PAYMENT_FAILED");
        log.info("Order {} status updated to PAYMENT_FAILED", event.getOrderId());
      }
    } catch (Exception ex) {
      log.error("Error processing CREATE_PAYMENT event for paymentId={}: {}",
          event.getPaymentId(), ex.getMessage(), ex);
    }
  }

}
