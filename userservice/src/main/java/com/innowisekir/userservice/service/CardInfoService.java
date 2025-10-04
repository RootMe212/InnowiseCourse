package com.innowisekir.userservice.service;

import com.innowisekir.userservice.dto.CardInfoDTO;
import java.util.List;

public interface CardInfoService {

  CardInfoDTO createCard(CardInfoDTO cardInfoDTO);

  CardInfoDTO getCardById(Long id);

  List<CardInfoDTO> getCardsByIds(List<Long> ids);

  CardInfoDTO updateCardInfo(CardInfoDTO cardInfoDTO, Long id);

  void deleteCard(Long id);
}
