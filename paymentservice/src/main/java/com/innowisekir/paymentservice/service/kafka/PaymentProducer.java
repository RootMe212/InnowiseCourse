package com.innowisekir.paymentservice.service.kafka;

import com.innowisekir.paymentservice.dto.event.CreatePaymentEvent;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProducer {
  private final KafkaTemplate<String, CreatePaymentEvent> kafkaTemplate;
  private static final String CREATE_PAYMENT_TOPIC = "create-payment";

  public CompletableFuture<SendResult<String, CreatePaymentEvent>> sendEvent(CreatePaymentEvent event) {
    CompletableFuture<SendResult<String, CreatePaymentEvent>> future =
        kafkaTemplate.send(CREATE_PAYMENT_TOPIC, event.getPaymentId(), event);

    return future.whenComplete((result, ex) -> {
      if (ex == null) {
        log.info("Sent CREATE_PAYMENT event for paymentId={} to topic={}",
            event.getPaymentId(), CREATE_PAYMENT_TOPIC);
      } else {
        log.error("Unable to send CREATE_PAYMENT event for paymentId={} due to: {}",
            event.getPaymentId(), ex.getMessage(), ex);
      }
    });
  }
}
