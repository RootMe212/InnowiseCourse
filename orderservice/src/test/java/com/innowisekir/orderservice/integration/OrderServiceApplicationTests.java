package com.innowisekir.orderservice.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.innowisekir.orderservice.dto.create.CreateOrderDTO;
import com.innowisekir.orderservice.dto.create.CreateOrderItemDTO;
import com.innowisekir.orderservice.dto.response.UserDTO;
import com.innowisekir.orderservice.entity.Item;
import com.innowisekir.orderservice.entity.Order;
import com.innowisekir.orderservice.entity.OrderItem;
import com.innowisekir.orderservice.repository.ItemRepository;
import com.innowisekir.orderservice.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.liquibase.drop-first=true",
    "spring.liquibase.enabled=true",
    "userservice.base-url=http://localhost:8089"
})
@DisplayName("OrderService integration tests")
class OrderServiceApplicationTests extends TestConfig {



  private WireMockServer wireMockServer;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private ItemRepository itemRepository;

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
  }

  @BeforeEach
  @DisplayName("Setup WireMock server and test data")
  void setUp() {
    wireMockServer = new WireMockServer(8089);
    wireMockServer.start();

    wireMockServer.stubFor(get(urlPathEqualTo("/api/v1/users/email"))
        .withQueryParam("email", equalTo("user@example.com"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(
                "{\"id\":1,\"name\":\"John\",\"surname\":\"Doe\",\"email\":\"user@example.com\",\"birthDate\":\"1990-01-01\"}")));

    setupTestData();
  }

  @AfterEach
  @DisplayName("Cleanup WireMock server")
  void tearDown() {
    if (wireMockServer != null) {
      wireMockServer.stop();
    }
  }

  private void setupTestData() {
    Item item1 = new Item();
    item1.setName("Laptop");
    item1.setPrice(new BigDecimal("999.99"));
    itemRepository.save(item1);

    Item item2 = new Item();
    item2.setName("Mouse");
    item2.setPrice(new BigDecimal("29.99"));
    itemRepository.save(item2);
  }

  @Test
  @DisplayName("Should create order successfully with valid data")
  @WithMockUser
  void shouldCreateOrderSuccessfully() throws Exception {
    String userEmail = "user@example.com";
    UserDTO userDTO = createUserDTO();

    wireMockServer.stubFor(get(urlPathEqualTo("/api/v1/users/email"))
        .withQueryParam("email", equalTo(userEmail))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(objectMapper.writeValueAsString(userDTO))));

    CreateOrderDTO orderDTO = createOrderDTO();

    mockMvc.perform(post("/api/v1/orders")
            .param("userEmail", userEmail)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderDTO))
            .with(csrf()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.order.userId").value(1L))
        .andExpect(jsonPath("$.order.status").value("PENDING"))
        .andExpect(jsonPath("$.order.items").isArray())
        .andExpect(jsonPath("$.order.items.length()").value(2))
        .andExpect(jsonPath("$.user.email").value(userEmail))
        .andExpect(jsonPath("$.user.name").value("Test User"));

    wireMockServer.verify(getRequestedFor(urlPathEqualTo("/api/v1/users/email"))
        .withQueryParam("email", equalTo(userEmail)));
  }

  @Test
  @DisplayName("Should get order by ID when order exists")
  @WithMockUser
  void shouldGetOrderByIdWhenOrderExists() throws Exception {
    // Given
    String userEmail = "user@example.com";
    UserDTO userDTO = createUserDTO();
    Order order = createAndSaveOrder();

    wireMockServer.stubFor(get(urlPathEqualTo("/api/v1/users/email"))
        .withQueryParam("email", equalTo(userEmail))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(objectMapper.writeValueAsString(userDTO))));

    mockMvc.perform(get("/api/v1/orders/{id}", order.getId())
            .param("userEmail", userEmail))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.order.id").value(order.getId()))
        .andExpect(jsonPath("$.order.userId").value(1L))
        .andExpect(jsonPath("$.order.status").value("PENDING"))
        .andExpect(jsonPath("$.user.email").value(userEmail));

    wireMockServer.verify(getRequestedFor(urlPathEqualTo("/api/v1/users/email"))
        .withQueryParam("email", equalTo(userEmail)));
  }

  @Test
  @DisplayName("Should return 404 when order not found")
  @WithMockUser
  void shouldReturn404WhenOrderNotFound() throws Exception {
    String userEmail = "user@example.com";
    Long nonExistentOrderId = 999L;

    mockMvc.perform(get("/api/v1/orders/{id}", nonExistentOrderId)
            .param("userEmail", userEmail))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.message").value("Order with id " + nonExistentOrderId + " not found"));
  }

  @Test
  @WithMockUser
  @DisplayName("Should update order status when valid data provided")
  void shouldUpdateOrderStatusWhenValidDataProvided() throws Exception {
    String userEmail = "user@example.com";
    UserDTO userDTO = createUserDTO();
    Order order = createAndSaveOrder();

    CreateOrderDTO updateDTO = new CreateOrderDTO();
    updateDTO.setUserId(1L);
    updateDTO.setStatus("COMPLETED");
    updateDTO.setItems(Arrays.asList());

    wireMockServer.stubFor(get(urlPathEqualTo("/api/v1/users/email"))
        .withQueryParam("email", equalTo(userEmail))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody(objectMapper.writeValueAsString(userDTO))));

    mockMvc.perform(put("/api/v1/orders/{id}", order.getId())
            .param("userEmail", userEmail)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateDTO))
            .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.order.id").value(order.getId()))
        .andExpect(jsonPath("$.order.status").value("COMPLETED"))
        .andExpect(jsonPath("$.user.email").value(userEmail));

    Order updatedOrder = orderRepository.findById(order.getId()).orElseThrow();
    assertEquals("COMPLETED", updatedOrder.getStatus());
  }

  @Test
  @WithMockUser
  @DisplayName("Should delete order when order exists")
  void shouldDeleteOrderWhenOrderExists() throws Exception {
    Order order = createAndSaveOrder();

    mockMvc.perform(delete("/api/v1/orders/{id}", order.getId())
        .with(csrf()));

    assertFalse(orderRepository.existsById(order.getId()));
  }

  @Test
  @WithMockUser
  @DisplayName("Should return 503 when UserService is unavailable")
  void shouldReturn503WhenUserServiceIsUnavailable() throws Exception {
    String userEmail = "user@example.com";
    CreateOrderDTO orderDTO = createOrderDTO();

    wireMockServer.stubFor(get(urlPathEqualTo("/api/v1/users/email"))
        .withQueryParam("email", equalTo(userEmail))
        .willReturn(aResponse()
            .withStatus(500)
            .withBody("Internal Server Error")));

    mockMvc.perform(post("/api/v1/orders")
            .param("userEmail", userEmail)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderDTO))
            .with(csrf()))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.message").value(containsString("Failed to get user by email")));
  }

  @Test
  @WithMockUser
  @DisplayName("Should validate request data and return 400 for invalid data")
  void shouldValidateRequestDataAndReturn400ForInvalidData() throws Exception {
    String userEmail = "user@example.com";
    CreateOrderDTO invalidOrderDTO = new CreateOrderDTO();

    mockMvc.perform(post("/api/v1/orders")
            .param("userEmail", userEmail)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidOrderDTO))
            .with(csrf()))
        .andExpect(status().isBadRequest());
  }

  private UserDTO createUserDTO() {
    UserDTO userDTO = new UserDTO();
    userDTO.setId(1L);
    userDTO.setName("Test User");
    userDTO.setSurname("Test Surname");
    userDTO.setEmail("user@example.com");
    userDTO.setBirthDate(LocalDate.of(1990, 1, 1));
    return userDTO;
  }

  private CreateOrderDTO createOrderDTO() {
    CreateOrderDTO orderDTO = new CreateOrderDTO();
    orderDTO.setUserId(1L);
    orderDTO.setStatus("PENDING");

    CreateOrderItemDTO item1 = new CreateOrderItemDTO();
    item1.setItemId(1L);
    item1.setQuantity(2);

    CreateOrderItemDTO item2 = new CreateOrderItemDTO();
    item2.setItemId(2L);
    item2.setQuantity(1);

    orderDTO.setItems(Arrays.asList(item1, item2));
    return orderDTO;
  }

  private Order createAndSaveOrder() {
    Order order = new Order();
    order.setUserId(1L);
    order.setStatus("PENDING");
    order.setCreationDate(LocalDateTime.now());

    var items = itemRepository.findAll();
    if (items.size() < 2) {
      throw new IllegalStateException("Not enough test items in database");
    }

    OrderItem orderItem1 = new OrderItem();
    orderItem1.setQuantity(2);
    orderItem1.setItem(items.get(0));
    orderItem1.setOrder(order);

    OrderItem orderItem2 = new OrderItem();
    orderItem2.setQuantity(1);
    orderItem2.setItem(items.get(1));
    orderItem2.setOrder(order);

    order.setOrderItems(Arrays.asList(orderItem1, orderItem2));

    return orderRepository.save(order);
  }
}
