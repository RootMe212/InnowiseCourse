package com.unit;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.innowisekir.userservice.dto.CardInfoDTO;
import com.innowisekir.userservice.entity.CardInfo;
import com.innowisekir.userservice.entity.User;
import com.innowisekir.userservice.exception.CardInfoNotFoundException;
import com.innowisekir.userservice.mapper.CardInfoMapper;
import com.innowisekir.userservice.repository.CardInfoRepository;
import com.innowisekir.userservice.repository.UserRepository;
import com.innowisekir.userservice.service.CardInfoService;
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
class CardInfoServiceUnitTest {

  @Mock
  CardInfoRepository cardInfoRepository;

  @Mock
  UserRepository userRepository;

  @Mock
  CardInfoMapper cardInfoMapper;

  @InjectMocks
  CardInfoService cardInfoService;

  private CardInfo testCard;
  private CardInfoDTO testCardDTO;
  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId(1L);
    testUser.setName("Kirill");
    testUser.setSurname("Samkov");
    testUser.setEmail("kirill@gmail.com");
    testUser.setBirthDate(LocalDate.of(2005, 5, 20));

    testCard = new CardInfo();
    testCard.setId(1L);
    testCard.setUser(testUser);
    testCard.setNumber("11111");
    testCard.setExpirationDate(LocalDate.of(2025, 10, 10));

    testCardDTO = new CardInfoDTO();
    testCardDTO.setId(1L);
    testCardDTO.setUserId(1L);
    testCardDTO.setNumber("11111");
    testCardDTO.setExpirationDate(LocalDate.of(2025, 10, 10));
  }

  @Test
  void createCard() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(cardInfoMapper.toEntity(testCardDTO)).thenReturn(testCard);
    when(cardInfoRepository.save(any(CardInfo.class))).thenAnswer(inv -> inv.getArgument(0));
    when(cardInfoMapper.toDTO(any(CardInfo.class))).thenReturn(testCardDTO);

    CardInfoDTO resultCard = cardInfoService.createCard(testCardDTO);

    assertNotNull(resultCard);
    assertEquals(testCardDTO.getId(), resultCard.getId());
    assertEquals(testCardDTO.getUserId(), resultCard.getUserId());
    assertEquals(testCardDTO.getNumber(), resultCard.getNumber());
    assertEquals(testCardDTO.getExpirationDate(), resultCard.getExpirationDate());
    verify(userRepository).findById(1L);
    verify(cardInfoRepository).save(any(CardInfo.class));
    verify(cardInfoMapper).toEntity(testCardDTO);
    verify(cardInfoMapper).toDTO(any(CardInfo.class));
  }

  @Test
  void getCardInfoByIdFound() {
    when(cardInfoRepository.findById(1L)).thenReturn(Optional.of(testCard));
    when(cardInfoMapper.toDTO(testCard)).thenReturn(testCardDTO);

    CardInfoDTO resultCard = cardInfoService.getCardById(1L);

    assertNotNull(resultCard);
    assertEquals(testCardDTO.getId(), resultCard.getId());
    assertEquals(testCardDTO.getNumber(), resultCard.getNumber());
    verify(cardInfoRepository).findById(1L);
    verify(cardInfoMapper).toDTO(testCard);
  }

  @Test
  void getCardInfoByIdNotFound() {
    when(cardInfoRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(CardInfoNotFoundException.class, () -> cardInfoService.getCardById(1L));
    verify(cardInfoRepository).findById(1L);
  }


  @Test
  void getCardsByIds() {
    List<Long> ids = Arrays.asList(1L, 2L);
    CardInfo card2 = new CardInfo();
    card2.setId(2L);
    card2.setUser(testUser);
    card2.setNumber("22222");
    card2.setHolder("Bob");
    card2.setExpirationDate(LocalDate.of(2026, 1, 1));

    CardInfoDTO cardDTO2 = new CardInfoDTO();
    cardDTO2.setId(2L);
    cardDTO2.setUserId(1L);
    cardDTO2.setNumber("22222");
    cardDTO2.setExpirationDate(LocalDate.of(2026, 1, 1));

    when(cardInfoRepository.findAllById(ids)).thenReturn(Arrays.asList(testCard, card2));
    when(cardInfoMapper.toDTO(testCard)).thenReturn(testCardDTO);
    when(cardInfoMapper.toDTO(card2)).thenReturn(cardDTO2);

    List<CardInfoDTO> resultCards = cardInfoService.getCardsByIds(ids);

    assertNotNull(resultCards);
    assertEquals(2, resultCards.size());
    verify(cardInfoRepository).findAllById(ids);
  }


  @Test
  void updateCardInfo() {
    CardInfoDTO updateDTO = new CardInfoDTO();
    updateDTO.setNumber("333");
    updateDTO.setExpirationDate(LocalDate.of(2027, 12, 31));

    CardInfo updatedCard = new CardInfo();
    updatedCard.setId(1L);
    updatedCard.setUser(testUser);
    updatedCard.setNumber("333");
    updatedCard.setExpirationDate(LocalDate.of(2027, 12, 31));

    CardInfoDTO updatedCardDTO = new CardInfoDTO();
    updatedCardDTO.setId(1L);
    updatedCardDTO.setUserId(1L);
    updatedCardDTO.setNumber("333");
    updatedCardDTO.setExpirationDate(LocalDate.of(2027, 12, 31));

    when(cardInfoRepository.findById(1L)).thenReturn(Optional.of(testCard));
    when(cardInfoRepository.updateCardInfoById(1L, "333",
        LocalDate.of(2027, 12, 31))).thenReturn(1);
    when(cardInfoRepository.findById(1L)).thenReturn(Optional.of(updatedCard));
    when(cardInfoMapper.toDTO(updatedCard)).thenReturn(updatedCardDTO);

    CardInfoDTO resultCard = cardInfoService.updateCardInfo(updateDTO, 1L);

    assertThat(resultCard).isNotNull();
    assertThat(resultCard.getNumber()).isEqualTo("333");
    verify(cardInfoRepository, times(2)).findById(1L);
    verify(cardInfoRepository).updateCardInfoById(1L, "333", LocalDate.of(2027, 12, 31));
  }

  @Test
  void deleteCard() {
    when(cardInfoRepository.findById(1L)).thenReturn(Optional.of(testCard));
    when(cardInfoRepository.deleteCardInfoByIdNative(1L)).thenReturn(1);

    cardInfoService.deleteCard(1L);

    verify(cardInfoRepository).findById(1L);
    verify(cardInfoRepository).deleteCardInfoByIdNative(1L);
  }
}
