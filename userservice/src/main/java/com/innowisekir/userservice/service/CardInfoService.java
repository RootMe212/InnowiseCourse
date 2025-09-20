package com.innowisekir.userservice.service;

import com.innowisekir.userservice.entities.CardInfo;
import com.innowisekir.userservice.repositories.CardInfoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardInfoService {

  private final CardInfoRepository cardInfoRepository;

  @Autowired
  public CardInfoService(CardInfoRepository cardInfoRepository) {
    this.cardInfoRepository = cardInfoRepository;
  }

  public CardInfo createCard(CardInfo cardInfo) {
    return cardInfoRepository.save(cardInfo);
  }

  public Optional<CardInfo> getCardById(Long id) {
    return cardInfoRepository.findById(id);
  }

  public List<CardInfo> getCardsByIds(List<Long> ids) {
    return cardInfoRepository.findByIdIn(ids);
  }

  public boolean updateCardInfo(CardInfo cardInfo, Long id) {
    int updatedRows = cardInfoRepository.updateCardInfoById(
        id,
        cardInfo.getNumber(),
        cardInfo.getHolder(),
        cardInfo.getExpirationDate());

    return updatedRows > 0;
  }

  public boolean deleteCard(Long id) {
    int deletedRows = cardInfoRepository.deleteCardInfoByIdNative(id);
    return deletedRows > 0;
  }


}
