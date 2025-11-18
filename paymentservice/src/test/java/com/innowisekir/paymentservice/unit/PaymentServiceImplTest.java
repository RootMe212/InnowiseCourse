package com.innowisekir.paymentservice.unit;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innowisekir.paymentservice.dto.create.CreatePaymentDTO;
import com.innowisekir.paymentservice.dto.response.PaymentDTO;
import com.innowisekir.paymentservice.entity.Payment;
import com.innowisekir.paymentservice.entity.status.PaymentStatus;
import com.innowisekir.paymentservice.exception.PaymentNotFoundException;
import com.innowisekir.paymentservice.mapper.PaymentMapper;
import com.innowisekir.paymentservice.repository.PaymentRepository;
import com.innowisekir.paymentservice.service.RandomNumberService;
import com.innowisekir.paymentservice.service.impl.PaymentServiceImpl;
import com.innowisekir.paymentservice.service.kafka.PaymentProducer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Payment service unit test")
class PaymentServiceImplTest {

  @Mock
  private PaymentRepository paymentRepository;

  @Mock
  private PaymentMapper paymentMapper;

  @Mock
  private RandomNumberService randomNumberService;

  @Mock
  private PaymentProducer paymentProducer;

  @InjectMocks
  private PaymentServiceImpl paymentService;

  private CreatePaymentDTO createPaymentDTO;
  private Payment payment;
  private PaymentDTO paymentDTO;

  @BeforeEach
  @DisplayName("Setup test data")
  void setUp() {
    createPaymentDTO = new CreatePaymentDTO();
    createPaymentDTO.setOrderId(11L);
    createPaymentDTO.setUserId(8L);
    createPaymentDTO.setPaymentAmount(new BigDecimal("133.33"));

    payment = new Payment();
    payment.setId("1");
    payment.setOrderId(11L);
    payment.setUserId(8L);
    payment.setPaymentAmount(new BigDecimal("133.33"));
    payment.setStatus(PaymentStatus.SUCCESS);
    payment.setTimestamp(LocalDateTime.now());

    paymentDTO = new PaymentDTO();
    paymentDTO.setId("1");
    paymentDTO.setOrderId(11L);
    paymentDTO.setUserId(8L);
    paymentDTO.setPaymentAmount(new BigDecimal("133.33"));
    paymentDTO.setStatus(PaymentStatus.SUCCESS);
    paymentDTO.setTimestamp(LocalDateTime.now());
  }

  @Test
  @DisplayName("create payment with success status")
  void createPaymentWithSuccessStatus(){
    when(paymentMapper.toEntity(createPaymentDTO)).thenReturn(payment);
    when(randomNumberService.getRandomNumber()).thenReturn(2);
    when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
    when(paymentMapper.toDTO(payment)).thenReturn(paymentDTO);

    PaymentDTO paymentDTO = paymentService.createPayment(createPaymentDTO);

    assertNotNull(paymentDTO);
    assertEquals("1", paymentDTO.getId());
    assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
    verify(paymentRepository, times(1)).save(any(Payment.class));
    verify(paymentProducer, times(1)).sendEvent(any());
  }

  @Test
  @DisplayName("create payment with failed status")
  void createPaymentWithFailedStatus() {
    when(paymentMapper.toEntity(createPaymentDTO)).thenReturn(payment);
    when(randomNumberService.getRandomNumber()).thenReturn(1);
    when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
    when(paymentMapper.toDTO(payment)).thenReturn(paymentDTO);

    PaymentDTO paymentDTO = paymentService.createPayment(createPaymentDTO);

    assertNotNull(paymentDTO);
    assertEquals("1", payment.getId());
    assertEquals(PaymentStatus.FAILED, payment.getStatus());
    verify(paymentRepository, times(1)).save(any(Payment.class));
    verify(paymentProducer, times(1)).sendEvent(any());
  }

  @Test
  @DisplayName("Should get payment by ID when payment exists")
  void getPaymentByIdWhenPaymentExists() {
    when(paymentRepository.findById("1")).thenReturn(Optional.of(payment));
    when(paymentMapper.toDTO(payment)).thenReturn(paymentDTO);

    PaymentDTO result = paymentService.getPaymentById("1");

    assertNotNull(result);
    assertEquals("1", result.getId());
    verify(paymentRepository).findById("1");
  }

  @Test
  @DisplayName("Should throw PaymentException when payment not found")
  void shouldThrowPaymentExceptionWhenPaymentNotFound() {
    when(paymentRepository.findById("missing")).thenReturn(Optional.empty());

    assertThrows(PaymentNotFoundException.class, () -> paymentService.getPaymentById("missing"));
    verify(paymentRepository, times(1)).findById("missing");
  }

  @Test
  @DisplayName("Get payments by orderId")
  void getPaymentsByOrderId() {
    List<Payment> payments = List.of(payment);
    when(paymentRepository.findByOrderId(11L)).thenReturn(payments);
    when(paymentMapper.toDTO(payment)).thenReturn(paymentDTO);

    List<PaymentDTO> result = paymentService.getPaymentsByOrderId(11L);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("1", result.get(0).getId());
    verify(paymentRepository, times(1)).findByOrderId(11L);
  }

  @Test
  @DisplayName("Get payments by user ID")
  void getPaymentsByUserId() {
    List<Payment> payments = List.of(payment);
    when(paymentRepository.findByUserId(7L)).thenReturn(payments);
    when(paymentMapper.toDTO(payment)).thenReturn(paymentDTO);

    List<PaymentDTO> result = paymentService.getPaymentsByUserId(7L);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(paymentRepository).findByUserId(7L);
  }

  @Test
  @DisplayName("Get payments by statuses")
  void getPaymentsByStatuses() {
    List<PaymentStatus> statuses = List.of(PaymentStatus.SUCCESS, PaymentStatus.FAILED);
    List<Payment> payments = List.of(payment);
    when(paymentRepository.findByStatusIn(statuses)).thenReturn(payments);
    when(paymentMapper.toDTO(payment)).thenReturn(paymentDTO);

    List<PaymentDTO> result = paymentService.getPaymentsByStatuses(statuses);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(paymentRepository).findByStatusIn(statuses);
  }

  @Test
  @DisplayName("Calculate total sum by date period excluding null amounts")
  void calculateTotalSumByDatePeriodExcludingNullAmounts() {
    LocalDateTime from = LocalDateTime.now().minusDays(1);
    LocalDateTime to = LocalDateTime.now();

    Payment p1 = new Payment();
    p1.setPaymentAmount(new BigDecimal("10.00"));
    Payment p2 = new Payment();
    p2.setPaymentAmount(null);
    Payment p3 = new Payment();
    p3.setPaymentAmount(new BigDecimal("2.50"));

    List<Payment> payments = List.of(p1, p2, p3);
    when(paymentRepository.findByTimestampBetween(from, to)).thenReturn(payments);

    BigDecimal result = paymentService.getTotalSumByDatePeriod(from, to);

    assertNotNull(result);
    assertEquals(new BigDecimal("12.50"), result);
    verify(paymentRepository).findByTimestampBetween(from, to);
  }



}
