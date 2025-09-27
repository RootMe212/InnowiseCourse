package com.integration;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.innowisekir.userservice.dto.CardInfoDTO;
import com.innowisekir.userservice.dto.UserDTO;
import com.innowisekir.userservice.entity.CardInfo;
import com.innowisekir.userservice.entity.User;
import com.innowisekir.userservice.repository.CardInfoRepository;
import com.innowisekir.userservice.repository.UserRepository;
import java.time.LocalDate;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;
import org.springframework.boot.test.mock.mockito.SpyBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
classes = com.innowisekir.userservice.UserserviceApplication.class)
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserserviceApplicationTest {

  @Container
  @ServiceConnection
  static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:latest"))
      .withExposedPorts(6379);

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
      .withDatabaseName("testDb")
      .withUsername("test")
      .withPassword("test");

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CacheManager cacheManager;

  @SpyBean
  UserRepository userRepositorySpy;

  @Autowired
  private CardInfoRepository cardInfoRepository;

  @SpyBean
  CardInfoRepository cardInfoRepositorySpy;

  private final ObjectMapper mapper = new ObjectMapper();
  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mapper.registerModule(new JavaTimeModule());
    userRepository.deleteAll();
    cardInfoRepository.deleteAll();
    Cache cache = cacheManager.getCache("USER_CACHE");
    if (cache!=null){
      cache.clear();
    }
  }

  /**
   * UserTest
   */
  @Test
  void testCreateUserAndCacheIt() throws Exception {
    UserDTO userDTO = new UserDTO(null,
        "Kirill",
        "Samkov",
        LocalDate.of(2005,5,20),
        "kirill@gmail.com",
        Collections.emptyList());

    MvcResult result = mockMvc.perform(post("/api/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(userDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNumber())
        .andReturn();

    UserDTO createdUserDTO = mapper.readValue(result.getResponse().getContentAsString(), UserDTO.class);
    Long userId = createdUserDTO.getId();

    Assertions.assertTrue(userRepository.findById(userId).isPresent());

    Cache cache = cacheManager.getCache("USER_CACHE");
    assertNotNull(cache);

    mockMvc.perform(get("/api/users/" + userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.email").isString())
        .andReturn();
  }

  @Test
  void testGetUserByIdAndVerifyCache() throws Exception {
    User user = new User();
    user.setName("Kirill");
    user.setSurname("Samkov");
    user.setBirthDate(LocalDate.of(2005, 5, 20));
    user.setEmail("kirill@gmail.com");
    userRepository.save(user);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/users/" + user.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.email").value(user.getEmail()));

    Mockito.verify(userRepositorySpy, Mockito.times(1)).findById(user.getId());

    Mockito.clearInvocations(userRepositorySpy);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/users/" + user.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.email").value(user.getEmail()));

    Mockito.verify(userRepositorySpy, Mockito.times(0)).findById(user.getId());
  }

  @Test
  void testGetUserByEmail() throws Exception {
    User user = new User();
    user.setName("Kirill");
    user.setSurname("Samkov");
    user.setBirthDate(LocalDate.of(2005,5,20));
    user.setEmail("kirill@gmail.com");
    user = userRepository.save(user);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/users/email")
            .param("email", "kirill@gmail.com"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.email").value(user.getEmail()));
  }

  @Test
  void testGetUsersByIds() throws Exception {
    User user1 = new User();
    user1.setName("User1");
    user1.setSurname("Surname1");
    user1.setBirthDate(LocalDate.of(2000,1,1));
    user1.setEmail("user1@gmail.com");
    user1 = userRepository.save(user1);

    User user2 = new User();
    user2.setName("User2");
    user2.setSurname("Surname2");
    user2.setBirthDate(LocalDate.of(2000,1,1));
    user2.setEmail("user2@gmail.com");
    user2 = userRepository.save(user2);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/users/ids")
            .param("ids", user1.getId().toString(), user2.getId().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value(user1.getId()))
        .andExpect(jsonPath("$[1].id").value(user2.getId()));
  }

  @Test
  @Transactional
  void testUpdateUser() throws Exception {
    User user = new User();
    user.setName("Max");
    user.setSurname("Chills");
    user.setBirthDate(LocalDate.of(2000,1,1));
    user.setEmail("max@gmail.com");
    user = userRepository.save(user);

    UserDTO updatedUserDTO = new UserDTO(user.getId(),
        "Updated name",
        "Updated surname",
        user.getBirthDate(),
        "updatedname@gmail.com",
        Collections.emptyList());

    mockMvc.perform(MockMvcRequestBuilders.put("/api/users/"+ user.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(updatedUserDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(user.getId()))
        .andExpect(jsonPath("$.name").value("Updated name"))
        .andExpect(jsonPath("$.email").value("updatedname@gmail.com"));

    Cache cache = cacheManager.getCache("USER_CACHE");
    assertNotNull(cache);
    UserDTO cachedUserDTO = cache.get(user.getId(),UserDTO.class);
    assertNotNull(cachedUserDTO);
    Assertions.assertEquals("Updated name",cachedUserDTO.getName());
    Assertions.assertEquals("updatedname@gmail.com",cachedUserDTO.getEmail());
  }

  @Test
  @Transactional
  void testDeleteUser() throws Exception {
    User user = new User();
    user.setName("Max");
    user.setSurname("surname");
    user.setBirthDate(LocalDate.of(2001,1,1));
    user.setEmail("max@gmail.com");
    user = userRepository.save(user);

    mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/" + user.getId()))
        .andExpect(status().isNoContent());

    Assertions.assertFalse(userRepository.findById(user.getId()).isPresent());

    Cache cache = cacheManager.getCache("USER_CACHE");
    assertNotNull(cache);
    Assertions.assertNull(cache.get(user.getId()));
  }

  /**
   * CardInfoTest
   */

  @Test
  void testCreateCardAndCacheIt() throws Exception {
    User user = new User();
    user.setName("Kirill");
    user.setSurname("Samkov");
    user.setBirthDate(LocalDate.of(2005, 5, 20));
    user.setEmail("kirill@gmail.com");
    user = userRepository.save(user);

    CardInfoDTO cardDTO = new CardInfoDTO(
        null,
        user.getId(),
        "11111",
        "Kirill Samkov",
        LocalDate.of(2025, 12, 31)
    );

    MvcResult result = mockMvc.perform(post("/api/cards")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(cardDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNumber())
        .andReturn();

    CardInfoDTO createdCardDTO = mapper.readValue(result.getResponse().getContentAsString(), CardInfoDTO.class);
    Long cardId = createdCardDTO.getId();

    Assertions.assertTrue(cardInfoRepository.findById(cardId).isPresent());

    Cache cache = cacheManager.getCache("CARD_CACHE");
    assertNotNull(cache);

    mockMvc.perform(get("/api/cards/" + cardId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.number").isString())
        .andReturn();
  }

  @Test
  void testGetCardByIdAndVerifyCache() throws Exception {
    // Сначала создаем пользователя
    User user = new User();
    user.setName("Kirill");
    user.setSurname("Samkov");
    user.setBirthDate(LocalDate.of(2005, 5, 20));
    user.setEmail("kirill@gmail.com");
    user = userRepository.save(user);

    CardInfo card = new CardInfo();
    card.setUser(user);
    card.setNumber("12345");
    card.setHolder("Kirill Samkov");
    card.setExpirationDate(LocalDate.of(2025, 12, 31));
    card = cardInfoRepository.save(card);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/cards/" + card.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(card.getId()))
        .andExpect(jsonPath("$.number").value(card.getNumber()));

    Mockito.verify(cardInfoRepositorySpy, Mockito.times(1)).findById(card.getId());

    Mockito.clearInvocations(cardInfoRepositorySpy);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/cards/" + card.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(card.getId()))
        .andExpect(jsonPath("$.number").value(card.getNumber()));

    Mockito.verify(cardInfoRepositorySpy, Mockito.times(0)).findById(card.getId());
  }

  @Test
  void testGetCardsByIds() throws Exception {
    // Сначала создаем пользователя
    User user = new User();
    user.setName("Kirill");
    user.setSurname("Samkov");
    user.setBirthDate(LocalDate.of(2005, 5, 20));
    user.setEmail("kirill@gmail.com");
    user = userRepository.save(user);

    CardInfo card1 = new CardInfo();
    card1.setUser(user);
    card1.setNumber("12345");
    card1.setHolder("Kirill Samkov");
    card1.setExpirationDate(LocalDate.of(2025, 12, 31));
    card1 = cardInfoRepository.save(card1);

    CardInfo card2 = new CardInfo();
    card2.setUser(user);
    card2.setNumber("67890");
    card2.setHolder("Kirill Samkov");
    card2.setExpirationDate(LocalDate.of(2026, 6, 30));
    card2 = cardInfoRepository.save(card2);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/cards/ids")
            .param("ids", card1.getId().toString(), card2.getId().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value(card1.getId()))
        .andExpect(jsonPath("$[1].id").value(card2.getId()));
  }

  @Test
  void testUpdateCard() throws Exception {
    // Сначала создаем пользователя
    User user = new User();
    user.setName("Kirill");
    user.setSurname("Samkov");
    user.setBirthDate(LocalDate.of(2005, 5, 20));
    user.setEmail("kirill@gmail.com");
    user = userRepository.save(user);

    CardInfo card = new CardInfo();
    card.setUser(user);
    card.setNumber("12345");
    card.setHolder("Kirill Samkov");
    card.setExpirationDate(LocalDate.of(2025, 12, 31));
    card = cardInfoRepository.save(card);

    CardInfoDTO updatedCardDTO = new CardInfoDTO(
        card.getId(),
        user.getId(),
        "54321",
        "Updated Holder",
        LocalDate.of(2026, 1, 1)
    );

    mockMvc.perform(MockMvcRequestBuilders.put("/api/cards/" + card.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(updatedCardDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(card.getId()))
        .andExpect(jsonPath("$.number").value("54321"))
        .andExpect(jsonPath("$.holder").value("Updated Holder"));

    Cache cache = cacheManager.getCache("CARD_CACHE");
    assertNotNull(cache);
    CardInfoDTO cachedCardDTO = cache.get(card.getId(), CardInfoDTO.class);
    assertNotNull(cachedCardDTO);
    Assertions.assertEquals("54321", cachedCardDTO.getNumber());
    Assertions.assertEquals("Updated Holder", cachedCardDTO.getHolder());
  }

  @Test
  void testDeleteCard() throws Exception {
    // Сначала создаем пользователя
    User user = new User();
    user.setName("Kirill");
    user.setSurname("Samkov");
    user.setBirthDate(LocalDate.of(2005, 5, 20));
    user.setEmail("kirill@gmail.com");
    user = userRepository.save(user);

    CardInfo card = new CardInfo();
    card.setUser(user);
    card.setNumber("12345");
    card.setHolder("Kirill Samkov");
    card.setExpirationDate(LocalDate.of(2025, 12, 31));
    card = cardInfoRepository.save(card);

    mockMvc.perform(MockMvcRequestBuilders.delete("/api/cards/" + card.getId()))
        .andExpect(status().isNoContent());

    Assertions.assertFalse(cardInfoRepository.findById(card.getId()).isPresent());

    Cache cache = cacheManager.getCache("CARD_CACHE");
    assertNotNull(cache);
    Assertions.assertNull(cache.get(card.getId()));
  }
}
