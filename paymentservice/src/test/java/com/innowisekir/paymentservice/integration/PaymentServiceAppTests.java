package com.innowisekir.paymentservice.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.innowisekir.paymentservice.dto.create.CreatePaymentDTO;
import com.innowisekir.paymentservice.dto.response.PaymentDTO;
import com.innowisekir.paymentservice.entity.Payment;
import com.innowisekir.paymentservice.repository.PaymentRepository;
import com.innowisekir.paymentservice.service.PaymentService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("PaymentService integration tests")
class PaymentServiceAppTests extends TestConfig {

  private static final WireMockServer wireMock = new WireMockServer();

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private PaymentService paymentService;

  @Autowired
  private PaymentRepository paymentRepository;

  @BeforeAll
  static void startWireMock() {
    wireMock.start();
    WireMock.configureFor("localhost", wireMock.port());
  }

  @AfterAll
  static void stopWireMock() {
    wireMock.stop();
  }

  @DynamicPropertySource
  static void dynamicProps(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    registry.add("spring.liquibase.url", mongoDBContainer::getReplicaSetUrl);
    registry.add("spring.data.mongodb.auto-index-creation", () -> "false");
    registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    registry.add("random.number.api.url",
        () -> wireMock.baseUrl() + "/api/v1.0/random?min=1&max=100&count=1");
  }

  @BeforeEach
  void cleanDbAndStubRandomApi() {
    paymentRepository.deleteAll();
    wireMock.resetAll();
    stubRandomNumber(42);
  }

  @Test
  @DisplayName("POST /payments should create payment, persist to MongoDB and send to Kafka")
  void createPayment_shouldPersistToMongoDBAndSendToKafka() throws Exception {
    CreatePaymentDTO request = paymentRequest(new BigDecimal("150.00"));

    mockMvc.perform(post("/api/v1/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.orderId").value(11))
        .andExpect(jsonPath("$.userId").value(8))
        .andExpect(jsonPath("$.paymentAmount").value(150.00))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.status").exists())
        .andExpect(jsonPath("$.timestamp").exists());

    assertThat(paymentRepository.count()).isEqualTo(1);
    Payment saved = paymentRepository.findAll().get(0);
    assertThat(saved.getOrderId()).isEqualTo(11L);
    assertThat(saved.getPaymentAmount()).isEqualByComparingTo("150.00");
    assertThat(saved.getStatus()).isNotNull();


  }

  @Test
  @DisplayName("GET /payments/{id} should return payment from MongoDB")
  void getPaymentById_shouldReturnFromMongoDB() throws Exception {
    PaymentDTO saved = paymentService.createPayment(paymentRequest(new BigDecimal("99.99")));

    mockMvc.perform(get("/api/v1/payments/{id}", saved.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(saved.getId()))
        .andExpect(jsonPath("$.orderId").value(11))
        .andExpect(jsonPath("$.userId").value(8))
        .andExpect(jsonPath("$.paymentAmount").value(99.99));
  }

  @Test
  @DisplayName("GET /payments/order/{orderId} should query MongoDB and return payments")
  void getPaymentsByOrderId_shouldQueryMongoDB() throws Exception {
    paymentService.createPayment(paymentRequest(new BigDecimal("10.00")));

    mockMvc.perform(get("/api/v1/payments/order/{orderId}", 11))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].orderId").value(11));
  }

  @Test
  @DisplayName("GET /payments/user/{userId} should query MongoDB and return payments")
  void getPaymentsByUserId_shouldQueryMongoDB() throws Exception {
    paymentService.createPayment(paymentRequest(new BigDecimal("10.00")));

    mockMvc.perform(get("/api/v1/payments/user/{userId}", 8))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].userId").value(8));
  }

  @Test
  @DisplayName("GET /payments/statuses should filter payments from MongoDB")
  void getPaymentsByStatuses_shouldFilterFromMongoDB() throws Exception {
    PaymentDTO payment = paymentService.createPayment(paymentRequest(new BigDecimal("10.00")));
    String actualStatus = payment.getStatus().name();

    mockMvc.perform(get("/api/v1/payments/statuses")
            .param("statuses", actualStatus))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].status").value(actualStatus));
  }

  @Test
  @DisplayName("GET /payments/total-sum should calculate sum from MongoDB")
  void calculateTotalSumByDatePeriod_shouldCalculateFromMongoDB() throws Exception {
    paymentService.createPayment(paymentRequest(new BigDecimal("90.00")));
    paymentService.createPayment(paymentRequest(new BigDecimal("10.00")));

    LocalDateTime from = LocalDateTime.now().minusDays(1);
    LocalDateTime to = LocalDateTime.now().plusDays(1);

    mockMvc.perform(get("/api/v1/payments/total-sum")
            .param("from", from.toString())
            .param("to", to.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").value(100.0));
  }

  @Test
  @DisplayName("POST /payments should return 400 for invalid request data")
  void createPayment_shouldReturn400ForInvalidData() throws Exception {
    mockMvc.perform(post("/api/v1/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isBadRequest());
  }

  private void stubRandomNumber(int number) {
    wireMock.stubFor(WireMock.get(urlPathEqualTo("/api/v1.0/random"))
        .willReturn(okJson("[" + number + "]")));
  }

  private CreatePaymentDTO paymentRequest(BigDecimal amount) {
    CreatePaymentDTO dto = new CreatePaymentDTO();
    dto.setOrderId(11L);
    dto.setUserId(8L);
    dto.setPaymentAmount(amount);
    return dto;
  }
}