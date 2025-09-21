package com.innowisekir.userservice.controller;

import com.innowisekir.userservice.dto.CardInfoDTO;
import com.innowisekir.userservice.service.CardInfoService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Create entities for tables in DB. Implement CRUD operations: Create User/Card Get User/Card by id
 * Get Users/Cards by ids Get User by email Update User/Card by id Delete User/Card by id
 */
@Controller
@RequestMapping("/api/cards")
public class CardInfoController {

  @Autowired
  CardInfoService cardInfoService;


  @PostMapping
  public ResponseEntity<CardInfoDTO> createCardInfo(@RequestBody CardInfoDTO cardInfoDTO) {
    CardInfoDTO createdCard = cardInfoService.createCard(cardInfoDTO);
    return new ResponseEntity<>(createdCard, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CardInfoDTO> getCardInfoById(@PathVariable Long id) {
    Optional<CardInfoDTO> cardInfo = cardInfoService.getCardById(id);
    return new ResponseEntity<>(cardInfo.orElse(null), HttpStatus.OK);
  }

  @GetMapping("/ids")
  public ResponseEntity<List<CardInfoDTO>> getUsersByIds(@RequestParam("ids") List<Long> ids) {
    List<CardInfoDTO> cards = cardInfoService.getCardsByIds(ids);
    return new ResponseEntity<>(cards, HttpStatus.OK);
  }

  @PutMapping("/{id}")
  public ResponseEntity<String> updateCardInfo(@PathVariable Long id,
      @Valid @RequestBody CardInfoDTO cardInfoDTO) {
    try {
      boolean updated = cardInfoService.updateCardInfo(cardInfoDTO, id);
      if (updated) {
        return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
      } else {
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteCard(@PathVariable Long id) {
    cardInfoService.deleteCard(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
