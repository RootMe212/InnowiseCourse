package com.innowisekir.userservice.service;

import com.innowisekir.userservice.dto.CardInfoDTO;
import com.innowisekir.userservice.dto.UserDTO;
import com.innowisekir.userservice.entity.CardInfo;
import com.innowisekir.userservice.entity.User;
import com.innowisekir.userservice.exception.CardInfoNotFoundException;
import com.innowisekir.userservice.exception.EntityAlreadyDeletedException;
import com.innowisekir.userservice.mapper.CardInfoMapper;
import com.innowisekir.userservice.repository.CardInfoRepository;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CardInfoServiceImpl implements CardInfoService {

  private final CardInfoRepository cardInfoRepository;
  private final CardInfoMapper cardInfoMapper;
  private final UserService userService;


  public CardInfoServiceImpl(CardInfoRepository cardInfoRepository, CardInfoMapper cardInfoMapper,
      UserService userService) {
    this.cardInfoRepository = cardInfoRepository;
    this.cardInfoMapper = cardInfoMapper;
    this.userService = userService;
  }

  @Override
  @CachePut(value = "CARD_CACHE", key = "#result.id", condition = "#result != null and #result.id != null")
  @CacheEvict(value = "USER_CACHE", key = "#cardInfoDTO.userId", condition = "#cardInfoDTO != null and #cardInfoDTO.userId != null")
  public CardInfoDTO createCard(CardInfoDTO cardInfoDTO) {
    UserDTO userDTO = userService.getUserById(cardInfoDTO.getUserId());

    User user = new User();
    user.setId(userDTO.getId());
    user.setName(userDTO.getName());
    user.setSurname(userDTO.getSurname());
    user.setEmail(userDTO.getEmail());
    user.setBirthDate(userDTO.getBirthDate());

    CardInfo cardInfo = cardInfoMapper.toEntity(cardInfoDTO);
    cardInfo.setUser(user);
    CardInfo savedCardInfo = cardInfoRepository.save(cardInfo);
    return cardInfoMapper.toDTO(savedCardInfo);
  }

  @Override
  @Cacheable(value = "CARD_CACHE", key = "#id", condition = "#id != null")
  public CardInfoDTO getCardById(Long id) {
    return cardInfoRepository.findById(id)
        .map(cardInfoMapper::toDTO)
        .orElseThrow(() -> new CardInfoNotFoundException("Card with " + id + " not found"));
  }

  @Override
  @Cacheable(value = "CARD_CACHE", key = "'all:'+ #ids.hashCode()", condition = "#ids != null")
  public List<CardInfoDTO> getCardsByIds(List<Long> ids) {
    List<CardInfo> cardInfos = cardInfoRepository.findAllById(ids);
    return cardInfos
        .stream()
        .map(cardInfoMapper::toDTO)
        .toList();
  }

  @Override
  @CachePut(value = "CARD_CACHE", key = "#result.id", condition = "#result != null and #result.id != null")
  @CacheEvict(value = "USER_CACHE", key = "#cardInfoDTO.userId", condition = "#cardInfoDTO != null and #cardInfoDTO.userId != null")
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

  @Override
  @Transactional
  @CacheEvict(value = "CARD_CACHE", key = "#id", condition = "#id != null")
  public void deleteCard(Long id) {
    if (!cardInfoRepository.existsById(id)) {
      throw new EntityAlreadyDeletedException("Card with id " + id + " has already been deleted or does not exist");
    }

    cardInfoRepository.deleteCardInfoByIdNative(id);
  }


}
