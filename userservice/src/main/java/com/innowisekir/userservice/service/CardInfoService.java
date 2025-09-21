package com.innowisekir.userservice.service;

import com.innowisekir.userservice.dto.CardInfoDTO;
import com.innowisekir.userservice.entity.CardInfo;
import com.innowisekir.userservice.mapper.CardInfoMapper;
import com.innowisekir.userservice.repository.CardInfoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
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

  public CardInfoDTO createCard(CardInfoDTO cardInfoDTO) {
    CardInfo cardInfo = cardInfoMapper.toEntity(cardInfoDTO);
    CardInfo savedCardInfo = cardInfoRepository.save(cardInfo);
    return cardInfoMapper.toDTO(savedCardInfo);
  }

  public Optional<CardInfoDTO> getCardById(Long id) {
    return cardInfoRepository.findById(id)
        .map(cardInfoMapper::toDTO);
  }

  public List<CardInfoDTO> getCardsByIds(List<Long> ids) {
    List<CardInfo> cardInfos = cardInfoRepository.findAllById(ids);
    return cardInfos
        .stream()
        .map(cardInfoMapper::toDTO)
        .toList();
  }

  @Transactional
  public boolean updateCardInfo(CardInfoDTO cardInfoDTO, Long id) {
    Optional<CardInfo> cardInfo = cardInfoRepository.findById(id);

    if (cardInfo.isPresent()) {
      CardInfo updatedCardInfo = cardInfoMapper.toEntityForUpdate(cardInfoDTO);
      updatedCardInfo.setId(id);
      updatedCardInfo.setUser(cardInfo.get().getUser());

      int updatedRows = cardInfoRepository.updateCardInfoById(
          id,
          updatedCardInfo.getNumber(),
          updatedCardInfo.getHolder(),
          updatedCardInfo.getExpirationDate());

      return updatedRows > 0;
    }
    return false;
  }

  @Transactional
  public boolean deleteCard(Long id) {
    int deletedRows = cardInfoRepository.deleteCardInfoByIdNative(id);
    return deletedRows > 0;
  }


}
