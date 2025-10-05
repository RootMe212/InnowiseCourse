package com.innowisekir.authservice.controller;

import com.innowisekir.authservice.exception.InvalidCredentialsException;
import com.innowisekir.authservice.exception.InvalidTokenException;
import com.innowisekir.authservice.exception.TokenExpiredException;
import com.innowisekir.authservice.exception.UserAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<Map<String, String>> handleInvalidCredentials(
      InvalidCredentialsException ex) {
    log.error("Invalid credentials: {}", ex.getMessage());
    Map<String, String> error = new HashMap<>();
    error.put("error", "INVALID_CREDENTIALS");
    error.put("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
  }

  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<Map<String, String>> handleInvalidToken(InvalidTokenException ex) {
    log.error("Invalid token: {}", ex.getMessage());
    Map<String, String> error = new HashMap<>();
    error.put("error", "INVALID_TOKEN");
    error.put("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
  }

  @ExceptionHandler(TokenExpiredException.class)
  public ResponseEntity<Map<String, String>> handleTokenExpired(TokenExpiredException ex) {
    log.error("Token expired: {}", ex.getMessage());
    Map<String, String> error = new HashMap<>();
    error.put("error", "TOKEN_EXPIRED");
    error.put("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<Map<String, String>> handleUserAlreadyExists(
      UserAlreadyExistsException ex) {
    log.error("User already exists: {}", ex.getMessage());
    Map<String, String> error = new HashMap<>();
    error.put("error", "USER_ALREADY_EXISTS");
    error.put("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    log.error("Validation error: {}", ex.getMessage());
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach( error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
    log.error("Unexpected error: {}", ex.getMessage(), ex);
    Map<String, String> error = new HashMap<>();
    error.put("error", "INTERNAL_SERVER_ERROR");
    error.put("message", "An unexpected error occurred");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}