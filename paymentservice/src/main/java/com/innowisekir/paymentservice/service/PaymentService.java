package com.innowisekir.paymentservice.service;

import com.innowisekir.paymentservice.dto.create.CreatePaymentDTO;
import com.innowisekir.paymentservice.dto.response.PaymentDTO;
import com.innowisekir.paymentservice.entity.status.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {

  PaymentDTO createPayment(CreatePaymentDTO paymentDTO);

  PaymentDTO getPaymentById(String id);

  List<PaymentDTO> getPaymentsByOrderId(Long orderId);

  List<PaymentDTO> getPaymentsByUserId(Long userId);

  List<PaymentDTO> getPaymentsByStatuses(List<PaymentStatus> statuses);

  BigDecimal getTotalSumByDatePeriod(LocalDateTime from, LocalDateTime to);
}