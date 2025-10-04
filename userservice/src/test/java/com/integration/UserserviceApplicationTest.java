package com.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.innowisekir.userservice.dto.CardInfoDTO;
import com.innowisekir.userservice.dto.UserDTO;
import com.innowisekir.userservice.entity.CardInfo;
import com.innowisekir.userservice.entity.User;
import com.innowisekir.userservice.exception.CardInfoNotFoundException;
import com.innowisekir.userservice.exception.EntityAlreadyDeletedException;
import com.innowisekir.userservice.exception.UserNotFoundException;
import com.innowisekir.userservice.repository.CardInfoRepository;
import com.innowisekir.userservice.repository.UserRepository;
import com.innowisekir.userservice.service.CardInfoService;
import com.innowisekir.userservice.service.UserService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(classes = com.innowisekir.userservice.UserserviceApplication.class)
@Testcontainers
@ActiveProfiles("test")
class UserserviceApplicationTest {

  @Container
  @ServiceConnection
  static GenericContainer<?> redisContainer = new GenericContainer<>(
      DockerImageName.parse("redis:latest"))
      .withExposedPorts(6379);

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
      .withDatabaseName("testDb")
      .withUsername("test")
      .withPassword("test");

  @Autowired
  private UserService userService;

  @Autowired
  private CardInfoService cardInfoService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CardInfoRepository cardInfoRepository;

  @Autowired
  private CacheManager cacheManager;

  private UserDTO testUserDTO;
  private CardInfoDTO testCardDTO;

  @BeforeEach
  void setUp() {
    cardInfoRepository.deleteAll();
    userRepository.deleteAll();

    clearCache();

    testUserDTO = createTestUserDTO();
    testCardDTO = createTestCardDTO();
  }

  private void clearCache() {
    Cache userCache = cacheManager.getCache("USER_CACHE");
    if (userCache != null) {
      userCache.clear();
    }
    Cache cardCache = cacheManager.getCache("CARD_CACHE");
    if (cardCache != null) {
      cardCache.clear();
    }
  }

  private UserDTO createTestUserDTO() {
    UserDTO userDTO = new UserDTO();
    userDTO.setName("Kirill");
    userDTO.setSurname("Samkov");
    userDTO.setEmail("kirill"+ System.currentTimeMillis()+ "@gmail.com");
    userDTO.setBirthDate(LocalDate.of(2005, 5, 20));
    return userDTO;
  }

  private CardInfoDTO createTestCardDTO() {
    CardInfoDTO cardDTO = new CardInfoDTO();
    cardDTO.setNumber("11111");
    cardDTO.setHolder("Kirill Samkov");
    cardDTO.setExpirationDate(LocalDate.of(2025, 12, 31));
    // НЕ устанавливаем userId здесь - он будет установлен в каждом тесте отдельно
    return cardDTO;
  }

  @Test
  @DisplayName("Should create user successfully with real database")
  void shouldCreateUserSuccessfully() {
    UserDTO createdUser = userService.createUser(testUserDTO);

    assertNotNull(createdUser);
    assertNotNull(createdUser.getId());
    assertEquals(testUserDTO.getName(), createdUser.getName());
    assertEquals(testUserDTO.getEmail(), createdUser.getEmail());

    User savedUser = userRepository.findById(createdUser.getId()).orElse(null);
    assertNotNull(savedUser);
    assertEquals(testUserDTO.getName(), savedUser.getName());
    assertEquals(testUserDTO.getEmail(), savedUser.getEmail());
  }

  @Test
  @DisplayName("Should get user by id with caching")
  void shouldGetUserByIdWithCaching() {
    UserDTO createdUser = userService.createUser(testUserDTO);
    Long userId = createdUser.getId();

    UserDTO retrievedUser1 = userService.getUserById(userId);

    assertNotNull(retrievedUser1);
    assertEquals(createdUser.getId(), retrievedUser1.getId());
    assertEquals(createdUser.getName(), retrievedUser1.getName());

    Cache userCache = cacheManager.getCache("USER_CACHE");
    assertNotNull(userCache);
    UserDTO cachedUser = userCache.get(userId, UserDTO.class);
    assertNotNull(cachedUser);
    assertEquals(createdUser.getName(), cachedUser.getName());
  }

  @Test
  @DisplayName("Should throw UserNotFoundException when user not found")
  void shouldThrowUserNotFoundExceptionWhenUserNotFound() {
    assertThrows(UserNotFoundException.class, () -> userService.getUserById(999L));
  }

  @Test
  @DisplayName("Should get users by ids successfully")
  void shouldGetUsersByIdsSuccessfully() {
    UserDTO user1 = userService.createUser(testUserDTO);

    UserDTO user2DTO = new UserDTO();
    user2DTO.setName("Bob");
    user2DTO.setSurname("Smith");
    user2DTO.setEmail("bob@gmail.com");
    user2DTO.setBirthDate(LocalDate.of(2000, 1, 1));
    UserDTO user2 = userService.createUser(user2DTO);

    List<UserDTO> users = userService.getUsersByIds(List.of(user1.getId(), user2.getId()));

    assertThat(users).hasSize(2);
    assertThat(users).extracting(UserDTO::getId).contains(user1.getId(), user2.getId());
  }

  @Test
  @DisplayName("Should get user by email successfully")
  void shouldGetUserByEmailSuccessfully() {
    UserDTO createdUser = userService.createUser(testUserDTO);

    UserDTO retrievedUser = userService.getUserByEmail(createdUser.getEmail());

    assertNotNull(retrievedUser);
    assertEquals(createdUser.getId(), retrievedUser.getId());
    assertEquals(createdUser.getEmail(), retrievedUser.getEmail());
  }

  @Test
  @DisplayName("Should update user successfully")
  @Transactional
  void shouldUpdateUserSuccessfully() {
    UserDTO createdUser = userService.createUser(testUserDTO);
    Long userId = createdUser.getId();

    UserDTO updateDTO = new UserDTO();
    updateDTO.setId(userId);
    updateDTO.setName("Updated Name");
    updateDTO.setSurname("Updated Surname");
    updateDTO.setEmail("updated@gmail.com");
    updateDTO.setBirthDate(LocalDate.of(2000, 1, 1));

    UserDTO updatedUser = userService.updateUser(userId, updateDTO);

    assertNotNull(updatedUser);
    assertEquals("Updated Name", updatedUser.getName());
    assertEquals("updated@gmail.com", updatedUser.getEmail());

    User savedUser = userRepository.findById(userId).orElse(null);
    assertNotNull(savedUser);
    assertEquals("Updated Name", savedUser.getName());
    assertEquals("updated@gmail.com", savedUser.getEmail());
  }

  @Test
  @DisplayName("Should delete user successfully")
  @Transactional
  void shouldDeleteUserSuccessfully() {
    UserDTO createdUser = userService.createUser(testUserDTO);
    Long userId = createdUser.getId();

    userService.deleteUser(userId);

    assertThat(userRepository.findById(userId)).isEmpty();

    Cache userCache = cacheManager.getCache("USER_CACHE");
    assertNotNull(userCache);
    assertThat(userCache.get(userId)).isNull();
  }

  @Test
  @DisplayName("Should throw EntityAlreadyDeletedException when deleting non-existent user")
  void shouldThrowEntityAlreadyDeletedExceptionWhenDeletingNonExistentUser() {
    assertThrows(EntityAlreadyDeletedException.class, () -> userService.deleteUser(999L));
  }

  @Test
  @DisplayName("Should create card successfully with real database")
  void shouldCreateCardSuccessfully() {
    UserDTO createdUser = userService.createUser(testUserDTO);
    testCardDTO.setUserId(createdUser.getId());

    CardInfoDTO createdCard = cardInfoService.createCard(testCardDTO);

    assertNotNull(createdCard);
    assertNotNull(createdCard.getId());
    assertEquals(testCardDTO.getNumber(), createdCard.getNumber());
    assertEquals(createdUser.getId(), createdCard.getUserId());

    CardInfo savedCard = cardInfoRepository.findById(createdCard.getId()).orElse(null);
    assertNotNull(savedCard);
    assertEquals(testCardDTO.getNumber(), savedCard.getNumber());
    assertEquals(createdUser.getId(), savedCard.getUser().getId());
  }

  @Test
  @DisplayName("Should get card by id with caching")
  void shouldGetCardByIdWithCaching() {
    UserDTO createdUser = userService.createUser(testUserDTO);
    testCardDTO.setUserId(createdUser.getId());
    CardInfoDTO createdCard = cardInfoService.createCard(testCardDTO);
    Long cardId = createdCard.getId();

    CardInfoDTO retrievedCard1 = cardInfoService.getCardById(cardId);

    assertNotNull(retrievedCard1);
    assertEquals(createdCard.getId(), retrievedCard1.getId());
    assertEquals(createdCard.getNumber(), retrievedCard1.getNumber());

    Cache cardCache = cacheManager.getCache("CARD_CACHE");
    assertNotNull(cardCache);
    CardInfoDTO cachedCard = cardCache.get(cardId, CardInfoDTO.class);
    assertNotNull(cachedCard);
    assertEquals(createdCard.getNumber(), cachedCard.getNumber());
  }

  @Test
  @DisplayName("Should throw CardInfoNotFoundException when card not found")
  void shouldThrowCardInfoNotFoundExceptionWhenCardNotFound() {
    assertThrows(CardInfoNotFoundException.class, () -> cardInfoService.getCardById(999L));
  }

  @Test
  @DisplayName("Should get cards by ids successfully")
  void shouldGetCardsByIdsSuccessfully() {
    UserDTO createdUser = userService.createUser(testUserDTO);

    testCardDTO.setUserId(createdUser.getId());
    CardInfoDTO card1 = cardInfoService.createCard(testCardDTO);

    CardInfoDTO card2DTO = new CardInfoDTO();
    card2DTO.setUserId(createdUser.getId());
    card2DTO.setNumber("22222");
    card2DTO.setHolder("Kirill Samkov");
    card2DTO.setExpirationDate(LocalDate.of(2026, 6, 30));
    CardInfoDTO card2 = cardInfoService.createCard(card2DTO);

    List<CardInfoDTO> cards = cardInfoService.getCardsByIds(List.of(card1.getId(), card2.getId()));

    assertThat(cards).hasSize(2);
    assertThat(cards).extracting(CardInfoDTO::getId).contains(card1.getId(), card2.getId());
  }

  @Test
  @DisplayName("Should update card successfully")
  @Transactional
  void shouldUpdateCardSuccessfully() {
    UserDTO createdUser = userService.createUser(testUserDTO);
    testCardDTO.setUserId(createdUser.getId());
    CardInfoDTO createdCard = cardInfoService.createCard(testCardDTO);
    Long cardId = createdCard.getId();

    CardInfoDTO updateDTO = new CardInfoDTO();
    updateDTO.setId(cardId);
    updateDTO.setUserId(createdUser.getId());
    updateDTO.setNumber("54321");
    updateDTO.setExpirationDate(LocalDate.of(2026, 1, 1));

    CardInfoDTO updatedCard = cardInfoService.updateCardInfo(updateDTO, cardId);

    assertNotNull(updatedCard);
    assertEquals("54321", updatedCard.getNumber());
    assertEquals(LocalDate.of(2026, 1, 1), updatedCard.getExpirationDate());

    CardInfo savedCard = cardInfoRepository.findById(cardId).orElse(null);
    assertNotNull(savedCard);
    assertEquals("54321", savedCard.getNumber());
    assertEquals(LocalDate.of(2026, 1, 1), savedCard.getExpirationDate());
  }

  @Test
  @DisplayName("Should delete card successfully")

  void shouldDeleteCardSuccessfully() {
    UserDTO createdUser = userService.createUser(testUserDTO);
    testCardDTO.setUserId(createdUser.getId());
    CardInfoDTO createdCard = cardInfoService.createCard(testCardDTO);
    Long cardId = createdCard.getId();

    cardInfoService.deleteCard(cardId);

    assertThat(cardInfoRepository.findById(cardId)).isEmpty();

    Cache cardCache = cacheManager.getCache("CARD_CACHE");
    assertNotNull(cardCache);
    assertThat(cardCache.get(cardId)).isNull();
  }

  @Test
  @DisplayName("Should throw EntityAlreadyDeletedException when deleting non-existent card")
  void shouldThrowEntityAlreadyDeletedExceptionWhenDeletingNonExistentCard() {
    assertThrows(EntityAlreadyDeletedException.class, () -> cardInfoService.deleteCard(999L));
  }

  @Test
  @DisplayName("Should create card with user relationship successfully")
  @Transactional
  void shouldCreateCardWithUserRelationshipSuccessfully() {
    UserDTO createdUser = userService.createUser(testUserDTO);
    testCardDTO.setUserId(createdUser.getId());

    CardInfoDTO createdCard = cardInfoService.createCard(testCardDTO);

    assertNotNull(createdCard);
    assertEquals(createdUser.getId(), createdCard.getUserId());

    CardInfo savedCard = cardInfoRepository.findById(createdCard.getId()).orElse(null);
    assertNotNull(savedCard);
    assertNotNull(savedCard.getUser());
    assertEquals(createdUser.getId(), savedCard.getUser().getId());
    assertEquals(createdUser.getName(), savedCard.getUser().getName());
  }
}