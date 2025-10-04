package com.innowisekir.userservice.controller;

import com.innowisekir.userservice.dto.UserDTO;
import com.innowisekir.userservice.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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
 *  Create User/Card Get User/Card by id
 * Get Users/Cards by ids Get User by email Update User/Card by id Delete User/Card by id
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
    UserDTO createdUser = userService.createUser(userDTO);
    return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
    UserDTO user = userService.getUserById(id);
    return ResponseEntity.ok(user);
  }

  @GetMapping("/ids")
  public ResponseEntity<List<UserDTO>> getUsersByIds(@RequestParam("ids") List<Long> ids) {
    List<UserDTO> users = userService.getUsersByIds(ids);
    return ResponseEntity.ok(users);
  }

  @GetMapping("/email")
  public ResponseEntity<UserDTO> getUserByEmail(@RequestParam("email") String email) {
    UserDTO user = userService.getUserByEmail(email);
    return ResponseEntity.ok(user);
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,
      @Valid @RequestBody UserDTO userDTO) {
    UserDTO updated = userService.updateUser(id, userDTO);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}
