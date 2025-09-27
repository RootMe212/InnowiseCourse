package com.innowisekir.userservice.controller;

import com.innowisekir.userservice.dto.CardInfoDTO;
import com.innowisekir.userservice.service.CardInfoService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Create entities for tables in DB. Implement CRUD operations: Create User/Card Get User/Card by id
 * Get Users/Cards by ids Get User by email Update User/Card by id Delete User/Card by id
 */
@Slf4j
@RestController
@RequestMapping("/api/cards")
public class CardInfoController {

  @Autowired
  CardInfoService cardInfoService;


  @PostMapping
  public ResponseEntity<CardInfoDTO> createCardInfo(@Valid @RequestBody CardInfoDTO cardInfoDTO) {
    CardInfoDTO createdCard = cardInfoService.createCard(cardInfoDTO);
    return new ResponseEntity<>(createdCard, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CardInfoDTO> getCardInfoById(@PathVariable Long id) {
    CardInfoDTO cardInfo = cardInfoService.getCardById(id);
    return ResponseEntity.ok(cardInfo);
  }

  @GetMapping("/ids")
  public ResponseEntity<List<CardInfoDTO>> getCardsByIds(@RequestParam("ids") List<Long> ids) {
    List<CardInfoDTO> cards = cardInfoService.getCardsByIds(ids);
    return ResponseEntity.ok(cards);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CardInfoDTO> updateCardInfo(@PathVariable Long id,
      @Valid @RequestBody CardInfoDTO cardInfoDTO) {
    CardInfoDTO updated = cardInfoService.updateCardInfo(cardInfoDTO, id);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
    cardInfoService.deleteCard(id);
    return ResponseEntity.noContent().build();
  }
}
