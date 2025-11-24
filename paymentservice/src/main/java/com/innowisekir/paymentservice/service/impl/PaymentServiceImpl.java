package com.innowisekir.paymentservice.service.impl;

import com.innowisekir.paymentservice.dto.create.CreatePaymentDTO;
import com.innowisekir.paymentservice.dto.event.PaymentCreateEvent;
import com.innowisekir.paymentservice.dto.response.PaymentDTO;
import com.innowisekir.paymentservice.entity.Payment;
import com.innowisekir.paymentservice.entity.status.PaymentStatus;
import com.innowisekir.paymentservice.exception.PaymentNotFoundException;
import com.innowisekir.paymentservice.mapper.PaymentMapper;
import com.innowisekir.paymentservice.repository.PaymentRepository;
import com.innowisekir.paymentservice.service.PaymentService;
import com.innowisekir.paymentservice.service.RandomNumberService;
import com.innowisekir.paymentservice.service.kafka.PaymentProducer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

  private final PaymentRepository paymentRepository;
  private final PaymentMapper paymentMapper;
  private final RandomNumberService randomNumberService;
  private final PaymentProducer paymentProducer;

  public PaymentServiceImpl(PaymentRepository paymentRepository, PaymentMapper paymentMapper,
      RandomNumberService randomNumberService, PaymentProducer paymentProducer) {
    this.paymentRepository = paymentRepository;
    this.paymentMapper = paymentMapper;
    this.randomNumberService = randomNumberService;
    this.paymentProducer = paymentProducer;
  }

  @Override
  public PaymentDTO createPayment(CreatePaymentDTO paymentDTO) {
    Payment payment = paymentMapper.toEntity(paymentDTO);
    payment.setTimestamp(LocalDateTime.now());

    PaymentStatus paymentStatus = determinePaymentStatus();
    payment.setStatus(paymentStatus);

    Payment savedPayment = paymentRepository.save(payment);

    PaymentCreateEvent event = createEvent(savedPayment);
    paymentProducer.sendEvent(event);

    return paymentMapper.toDTO(savedPayment);
  }

  private PaymentCreateEvent createEvent(Payment payment) {
    PaymentCreateEvent event = new PaymentCreateEvent();
    event.setPaymentId(payment.getId());
    event.setOrderId(payment.getOrderId());
    event.setUserId(payment.getUserId());
    event.setStatus(payment.getStatus().name());
    event.setPaymentAmount(payment.getPaymentAmount());
    event.setTimestamp(payment.getTimestamp());
    return event;
  }

  private PaymentStatus determinePaymentStatus() {
    int randomNumber = randomNumberService.getRandomNumber();

    if (randomNumber % 2 == 0) {
      return PaymentStatus.SUCCESS;
    } else {
      return PaymentStatus.FAILED;
    }
  }

  @Override
  public PaymentDTO getPaymentById(String id) {
    Payment payment = paymentRepository.findById(id)
        .orElseThrow(() -> new PaymentNotFoundException("Payment " + id + " not found"));
    return paymentMapper.toDTO(payment);
  }

  @Override
  public List<PaymentDTO> getPaymentsByOrderId(Long orderId) {
    return paymentRepository.findByOrderId(orderId).stream()
        .map(paymentMapper::toDTO)
        .toList();
  }

  @Override
  public List<PaymentDTO> getPaymentsByUserId(Long userId) {
    return paymentRepository.findByUserId(userId).stream()
        .map(paymentMapper::toDTO)
        .toList();
  }

  @Override
  public List<PaymentDTO> getPaymentsByStatuses(List<PaymentStatus> statuses) {
    return paymentRepository.findByStatusIn(statuses).stream()
        .map(paymentMapper::toDTO)
        .toList();
  }

  @Override
  public BigDecimal getTotalSumByDatePeriod(LocalDateTime from, LocalDateTime to) {
    return paymentRepository.findByTimestampBetween(from,to).stream()
        .map(Payment::getPaymentAmount)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
