package com.innowisekir.orderservice.service.kafka;

import com.innowisekir.orderservice.dto.event.CreateOrderEvent;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {
  private final KafkaTemplate<String, CreateOrderEvent> kafkaTemplate;
  private static final String CREATE_ORDER_TOPIC = "create-order";

  public CompletableFuture<SendResult<String,CreateOrderEvent>> sendEvent(CreateOrderEvent event) {
    CompletableFuture<SendResult<String,CreateOrderEvent>> future =
        kafkaTemplate.send(CREATE_ORDER_TOPIC, event.getOrderId().toString(), event);

    return future.whenComplete((result, error) -> {
      if (error == null) {
        log.info("Sent CREATE_ORDER event for orderId={} to topic={}",
            event.getOrderId(), CREATE_ORDER_TOPIC);
      } else {
        log.error("Unable to send CREATE_ORDER event for orderId={} due to: {}",
            event.getOrderId(), error.getMessage(), error);
      }
    });
  }

}
