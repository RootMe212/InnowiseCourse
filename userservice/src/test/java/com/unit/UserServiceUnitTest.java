package com.unit;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.innowisekir.userservice.dto.CardInfoDTO;
import com.innowisekir.userservice.dto.UserDTO;
import com.innowisekir.userservice.entity.CardInfo;
import com.innowisekir.userservice.entity.User;
import com.innowisekir.userservice.exception.UserNotFoundException;
import com.innowisekir.userservice.mapper.CardInfoListMapper;
import com.innowisekir.userservice.mapper.UserMapper;
import com.innowisekir.userservice.repository.UserRepository;
import com.innowisekir.userservice.service.UserService;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private CardInfoListMapper cardInfoListMapper;

  private User testUser;
  private UserDTO testUserDTO;
  private CardInfo testCard;
  private CardInfoDTO testCardDTO;

  @BeforeEach
  void setUp() {

    testUser = new User();
    testUser.setId(1L);
    testUser.setName("Kirill");
    testUser.setSurname("Samkov");
    testUser.setEmail("kirill@gmail.com");
    testUser.setBirthDate(LocalDate.of(2005, 5, 20));

    testUserDTO = new UserDTO();
    testUserDTO.setId(1L);
    testUserDTO.setName("Kirill");
    testUserDTO.setSurname("Samkov");
    testUserDTO.setEmail("kirill@gmail.com");
    testUserDTO.setBirthDate(LocalDate.of(2005, 5, 20));

    testCard = new CardInfo();
    testCard.setId(1L);
    testCard.setUser(testUser);
    testCard.setNumber("11111");
    testCard.setHolder("Samkov Kirill");
    testCard.setExpirationDate(LocalDate.of(2025, 10, 10));

    testCardDTO = new CardInfoDTO();
    testCardDTO.setId(1L);
    testCardDTO.setUserId(1L);
    testCardDTO.setNumber("11111");
    testCardDTO.setExpirationDate(LocalDate.of(2025, 10, 10));

    testUser.setCards(Arrays.asList(testCard));
    testUserDTO.setCards(Arrays.asList(testCardDTO));
  }

  @Test
  void createUser() {
    when(userMapper.toEntity(testUserDTO)).thenReturn(testUser);
    when(userRepository.save(testUser)).thenReturn(testUser);
    when(userMapper.toDTO(testUser)).thenReturn(testUserDTO);
    when(cardInfoListMapper.toDTOList(testUser.getCards())).thenReturn(Arrays.asList(testCardDTO));

    UserDTO resultUser = userService.createUser(testUserDTO);

    assertNotNull(resultUser);
    assertEquals(testUserDTO.getId(), resultUser.getId());
    assertEquals(testUserDTO.getName(), resultUser.getName());
    assertEquals(testUserDTO.getEmail(), resultUser.getEmail());
    verify(userRepository).save(testUser);
    verify(userMapper).toEntity(testUserDTO);
    verify(userMapper).toDTO(testUser);
  }

  @Test
  void getUserByIdFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(testUser));
    when(userMapper.toDTO(testUser)).thenReturn(testUserDTO);
    when(cardInfoListMapper.toDTOList(testUser.getCards())).thenReturn(Arrays.asList(testCardDTO));

    UserDTO resultUser = userService.getUserById(1L);

    assertNotNull(resultUser);
    assertEquals(testUserDTO.getId(), resultUser.getId());
    assertEquals(testUserDTO.getName(), resultUser.getName());
    assertEquals(testUserDTO.getEmail(), resultUser.getEmail());
    verify(userRepository).findById(1L);
    verify(userMapper).toDTO(testUser);
  }

  @Test
  void getUserByIdNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    verify(userRepository).findById(1L);
  }


  @Test
  void getUsersByIds() {
    List<Long> ids = Arrays.asList(1L, 2L);
    User testUser2 = new User();
    testUser2.setId(2L);
    testUser2.setName("Bob");
    testUser2.setSurname("Smith");
    testUser2.setEmail("bob@gmail.com");
    testUser2.setBirthDate(LocalDate.of(2000, 4, 21));

    UserDTO testUserDTO2 = new UserDTO();
    testUserDTO2.setId(2L);
    testUserDTO2.setName("Bob");
    testUserDTO2.setSurname("Smith");
    testUserDTO2.setEmail("bob@gmail.com");
    testUserDTO2.setBirthDate(LocalDate.of(2000, 4, 21));

    when(userRepository.findByIdIn(ids)).thenReturn(Arrays.asList(testUser, testUser2));
    when(userMapper.toDTO(testUser)).thenReturn(testUserDTO);
    when(userMapper.toDTO(testUser2)).thenReturn(testUserDTO2);
    when(cardInfoListMapper.toDTOList(testUser.getCards())).thenReturn(Arrays.asList(testCardDTO));

    List<UserDTO> resultUsers = userService.getUsersByIds(ids);

    assertNotNull(resultUsers);
    assertEquals(2, resultUsers.size());
    assertEquals(testUserDTO.getId(), resultUsers.get(0).getId());
    assertEquals(testUserDTO2.getId(), resultUsers.get(1).getId());
    verify(userRepository).findByIdIn(ids);
    verify(userMapper).toDTO(testUser);
    verify(userMapper).toDTO(testUser2);
    verify(cardInfoListMapper).toDTOList(testUser.getCards());

  }


  @Test
  void getUserByEmail() {
    when(userRepository.findByEmail("kirill@gmail.com")).thenReturn(Optional.ofNullable(testUser));
    when(userMapper.toDTO(testUser)).thenReturn(testUserDTO);
    when(cardInfoListMapper.toDTOList(testUser.getCards())).thenReturn(Arrays.asList(testCardDTO));

    UserDTO resultUser = userService.getUserByEmail("kirill@gmail.com");
    assertNotNull(resultUser);
    assertEquals(testUserDTO.getEmail(), resultUser.getEmail());
    verify(userRepository).findByEmail("kirill@gmail.com");
  }

  @Test
  void updateUser() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(userRepository.updateUserById(anyLong(), any(), any(), any(), any())).thenReturn(1);
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(userMapper.toDTO(testUser)).thenReturn(testUserDTO);
    when(cardInfoListMapper.toDTOList(testUser.getCards())).thenReturn(Arrays.asList(testCardDTO));

    UserDTO resultUser = userService.updateUser(1L, testUserDTO);

    assertThat(resultUser).isNotNull();
    assertThat(resultUser.getName()).isEqualTo("Kirill");
    verify(userRepository, times(2)).findById(1L);
    verify(userRepository).updateUserById(1L, "Kirill", "Samkov", "kirill@gmail.com",
        LocalDate.of(2005, 5, 20));

  }

  @Test
  void deleteUser() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(userRepository.deleteUserByIdNative(1L)).thenReturn(1);

    userService.deleteUser(1L);
    verify(userRepository, times(1)).deleteUserByIdNative(1L);
  }
}
