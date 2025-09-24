package com.innowisekir.userservice.service;

import com.innowisekir.userservice.dto.CardInfoDTO;
import com.innowisekir.userservice.entity.CardInfo;
import com.innowisekir.userservice.exception.CardInfoNotFoundException;
import com.innowisekir.userservice.mapper.CardInfoMapper;
import com.innowisekir.userservice.repository.CardInfoRepository;
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

  @Autowired
  public CardInfoService(CardInfoRepository cardInfoRepository, CardInfoMapper cardInfoMapper) {
    this.cardInfoRepository = cardInfoRepository;
    this.cardInfoMapper = cardInfoMapper;
  }

  @CachePut(value = "CARD_CACHE", key = "#result.id()")
  public CardInfoDTO createCard(CardInfoDTO cardInfoDTO) {
    CardInfo cardInfo = cardInfoMapper.toEntity(cardInfoDTO);
    CardInfo savedCardInfo = cardInfoRepository.save(cardInfo);
    return cardInfoMapper.toDTO(savedCardInfo);
  }

  @Cacheable(value = "CARD_CACHE", key = "#id")
  public CardInfoDTO getCardById(Long id) {
    return cardInfoRepository.findById(id)
        .map(cardInfoMapper::toDTO)
        .orElseThrow(() -> new CardInfoNotFoundException("Card with " + id + " not found"));
  }

  @Cacheable(value = "CARD_CACHE", key = "'all:'+ ids.hashCode()")
  public List<CardInfoDTO> getCardsByIds(List<Long> ids) {
    List<CardInfo> cardInfos = cardInfoRepository.findAllById(ids);
    return cardInfos
        .stream()
        .map(cardInfoMapper::toDTO)
        .toList();
  }

  @CachePut(value = "CARD_CACHE", key = "#result.id()")
  @Transactional
  public CardInfoDTO updateCardInfo(CardInfoDTO cardInfoDTO, Long id) {
    CardInfo cardInfo = cardInfoRepository.findById(id)
        .orElseThrow(() -> new CardInfoNotFoundException(
            "Failed to update card info with id " + id + " because " + cardInfoDTO + " not found"));

    CardInfo updatedCardInfo = cardInfoMapper.toEntityForUpdate(cardInfoDTO);
    updatedCardInfo.setId(id);
    updatedCardInfo.setUser(cardInfo.getUser());

    int updatedRows = cardInfoRepository.updateCardInfoById(
        id,
        updatedCardInfo.getNumber(),
        updatedCardInfo.getHolder(),
        updatedCardInfo.getExpirationDate());

    if (updatedRows == 0) {
      throw new CardInfoNotFoundException("Failed to update card with id " + id);
    }

    CardInfo updatedCard = cardInfoRepository.findById(id)
        .orElseThrow(() -> new CardInfoNotFoundException("Card with id " + id + " not found"));

    return cardInfoMapper.toDTO(updatedCard);
  }

  @Transactional
  @CacheEvict(value = "CARD_CACHE", key = "#id")
  public void deleteCard(Long id) {
    cardInfoRepository.findById(id)
        .orElseThrow(() -> new CardInfoNotFoundException(
            "Failed to delete card info with id " + id + " because " + " card not found"));

    int deletedRows = cardInfoRepository.deleteCardInfoByIdNative(id);
    if (deletedRows == 0) {
      throw new CardInfoNotFoundException("Failed to delete card with id " + id);
    }
  }


}
