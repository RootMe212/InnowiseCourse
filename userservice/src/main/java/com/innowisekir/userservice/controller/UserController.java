package com.innowisekir.userservice.controller;

import com.innowisekir.userservice.entities.User;
import com.innowisekir.userservice.service.UserService;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Controller
@RequestMapping("/api/users")
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping
  public ResponseEntity<User> createUser(@RequestBody User user) {
    User createdUser = userService.createUser(user);
    return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<User> getUserById(@PathVariable Long id) {
    Optional<User> user = userService.getUserById(id);
    return new ResponseEntity<>(user.orElse(null), HttpStatus.OK);
  }

  @GetMapping("/ids")
  public ResponseEntity<List<User>> getUsersByIds(@RequestParam("ids") List<Long> ids) {
    List<User> users = userService.getUsersByIds(ids);
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @GetMapping("/email")
  public ResponseEntity<User> getUserByEmail(@RequestParam("email") String email) {
    Optional<User> user = userService.findUserByEmail(email);
    return new ResponseEntity<>(user.orElse(null), HttpStatus.OK);
  }

  @PutMapping("/{id}")
  public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody User user) {
    try {
      boolean updated = userService.updateUser(id, user);
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
  public ResponseEntity<User> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
