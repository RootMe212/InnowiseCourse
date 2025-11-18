package com.innowisekir.paymentservice.controller;

import com.innowisekir.paymentservice.dto.create.CreatePaymentDTO;
import com.innowisekir.paymentservice.dto.response.PaymentDTO;
import com.innowisekir.paymentservice.entity.status.PaymentStatus;
import com.innowisekir.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  @PostMapping
  public ResponseEntity<PaymentDTO> createPayment(@Valid @RequestBody CreatePaymentDTO paymentDTO) {
    PaymentDTO paymentResponse = paymentService.createPayment(paymentDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponse);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable String id) {
    PaymentDTO paymentResponse = paymentService.getPaymentById(id);
    return ResponseEntity.ok(paymentResponse);
  }

  @GetMapping("/order/{orderId}")
  public ResponseEntity<List<PaymentDTO>> getPaymentsByOrderId(@PathVariable Long orderId) {
    List<PaymentDTO> payments = paymentService.getPaymentsByOrderId(orderId);
    return ResponseEntity.ok(payments);
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<PaymentDTO>> getPaymentsByUserId(@PathVariable Long userId) {
    List<PaymentDTO> payments = paymentService.getPaymentsByUserId(userId);
    return ResponseEntity.ok(payments);
  }

  @GetMapping("/statuses")
  public ResponseEntity<List<PaymentDTO>> getPaymentsByStatuses(
      @RequestParam("statuses") List<PaymentStatus> statuses) {
    List<PaymentDTO> payments = paymentService.getPaymentsByStatuses(statuses);
    return ResponseEntity.ok(payments);
  }

  @GetMapping("/total-sum")
  public ResponseEntity<BigDecimal> getTotalSumByDatePeriod(
      @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
      @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
    BigDecimal totalSum = paymentService.getTotalSumByDatePeriod(from, to);
    return ResponseEntity.ok(totalSum);
  }
}