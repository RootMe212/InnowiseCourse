package com.innowisekir.userservice.service;

import com.innowisekir.userservice.dto.CardInfoDTO;
import com.innowisekir.userservice.entity.CardInfo;
import com.innowisekir.userservice.entity.User;
import com.innowisekir.userservice.exception.CardInfoNotFoundException;
import com.innowisekir.userservice.mapper.CardInfoMapper;
import com.innowisekir.userservice.repository.CardInfoRepository;
import com.innowisekir.userservice.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CardInfoService {

  private final CardInfoRepository cardInfoRepository;
  private final CardInfoMapper cardInfoMapper;
  private final UserRepository userRepository;

  @Autowired
  public CardInfoService(CardInfoRepository cardInfoRepository, CardInfoMapper cardInfoMapper,
      UserRepository userRepository) {
    this.cardInfoRepository = cardInfoRepository;
    this.cardInfoMapper = cardInfoMapper;
    this.userRepository = userRepository;
  }

  @CachePut(value = "CARD_CACHE", key = "#result.id")
  @CacheEvict(value = "USER_CACHE", key = "#cardInfoDTO.userId")
  public CardInfoDTO createCard(CardInfoDTO cardInfoDTO) {
    User user = userRepository.findById(cardInfoDTO.getUserId())
        .orElseThrow(() -> new CardInfoNotFoundException(
            "User with id " + cardInfoDTO.getUserId() + " not found"));

    CardInfo cardInfo = cardInfoMapper.toEntity(cardInfoDTO);
    cardInfo.setUser(user);
    CardInfo savedCardInfo = cardInfoRepository.save(cardInfo);
    return cardInfoMapper.toDTO(savedCardInfo);
  }

  @Cacheable(value = "CARD_CACHE", key = "#id")
  public CardInfoDTO getCardById(Long id) {
    return cardInfoRepository.findById(id)
        .map(cardInfoMapper::toDTO)
        .orElseThrow(() -> new CardInfoNotFoundException("Card with " + id + " not found"));
  }

  @Cacheable(value = "CARD_CACHE", key = "'all:'+ #ids.hashCode()")
  public List<CardInfoDTO> getCardsByIds(List<Long> ids) {
    List<CardInfo> cardInfos = cardInfoRepository.findAllById(ids);
    return cardInfos
        .stream()
        .map(cardInfoMapper::toDTO)
        .toList();
  }

  @CachePut(value = "CARD_CACHE", key = "#result.id")
  @CacheEvict(value = "USER_CACHE", key = "#cardInfoDTO.userId")
  @Transactional
  public CardInfoDTO updateCardInfo(CardInfoDTO cardInfoDTO, Long id) {
    cardInfoRepository.findById(id)
        .orElseThrow(() -> new CardInfoNotFoundException("Card with id " + id + " not found"));

    cardInfoRepository.updateCardInfoById(
        id,
        cardInfoDTO.getNumber(),
        cardInfoDTO.getExpirationDate()
    );

    CardInfo updatedCard = cardInfoRepository.findById(id)
        .orElseThrow(() -> new CardInfoNotFoundException("Card with id " + id + " not found"));

    return cardInfoMapper.toDTO(updatedCard);
  }

  @Transactional
  @CacheEvict(value = "CARD_CACHE", key = "#id")
  public void deleteCard(Long id) {
    cardInfoRepository.findById(id)
        .orElseThrow(() -> new CardInfoNotFoundException("Card with id " + id + " not found"));
    cardInfoRepository.deleteCardInfoByIdNative(id);
  }


}
