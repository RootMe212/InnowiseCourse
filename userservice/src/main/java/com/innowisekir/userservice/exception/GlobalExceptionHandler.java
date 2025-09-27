package com.innowisekir.userservice.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final String ERROR_KEY = "error";
  private static final String MESSAGE_KEY = "message";

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<Map<String, String>> userNotFoundException(
      UserNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Map.of(ERROR_KEY, "User not found", MESSAGE_KEY, exception.getMessage()));
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<Map<String, String>> validationException(
      ValidationException exception
  ) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(Map.of(ERROR_KEY, "Check valid", MESSAGE_KEY, exception.getMessage()));
  }

  @ExceptionHandler(InvalidEmailException.class)
  public ResponseEntity<Map<String, String>> invalidEmailException(
      InvalidEmailException exception
  ) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(Map.of(ERROR_KEY, "Invalid email", MESSAGE_KEY, exception.getMessage()));
  }

  @ExceptionHandler(CardInfoNotFoundException.class)
  public ResponseEntity<Map<String, String>> cardInfoNotFoundException(
      CardInfoNotFoundException exception
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Map.of(ERROR_KEY, "Card not found", MESSAGE_KEY, exception.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> validationError(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(Map.of("error", "Validation failed", "message", message));
  }
}
