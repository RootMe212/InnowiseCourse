package com.innowisekir.orderservice.service.kafka;

import com.innowisekir.orderservice.dto.event.OrderCreateEvent;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {
  private final KafkaTemplate<String, OrderCreateEvent> kafkaTemplate;

  @Value("${kafka.topics.create-order}")
  private String createOrderTopic;

  public CompletableFuture<SendResult<String,OrderCreateEvent>> sendEvent(OrderCreateEvent event) {
    CompletableFuture<SendResult<String,OrderCreateEvent>> future =
        kafkaTemplate.send(createOrderTopic, event.getOrderId().toString(), event);

    return future.whenComplete((result, error) -> {
      if (error == null) {
        log.info("Sent CREATE_ORDER event for orderId={} to topic={}",
            event.getOrderId(), createOrderTopic);
      } else {
        log.error("Unable to send CREATE_ORDER event for orderId={} due to: {}",
            event.getOrderId(), error.getMessage(), error);
      }
    });
  }

}
