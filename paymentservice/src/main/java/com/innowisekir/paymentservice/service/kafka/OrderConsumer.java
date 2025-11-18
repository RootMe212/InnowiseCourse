package com.innowisekir.paymentservice.service.kafka;

import com.innowisekir.paymentservice.dto.event.CreateOrderEvent;
import com.innowisekir.paymentservice.service.PaymentService;
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
  public void createOrder(CreateOrderEvent event) {
    log.info("Received CREATE_ORDER event: orderId={}, userId={}, status={}, itemsCount={}",
        event.getOrderId(), event.getUserId(), event.getStatus(), event.getItems()
            != null ? event.getItems().size() : 0);

  }
}
