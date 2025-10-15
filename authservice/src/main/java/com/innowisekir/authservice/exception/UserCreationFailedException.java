package com.innowisekir.authservice.exception;

public class UserCreationFailedException extends RuntimeException {

  public UserCreationFailedException(String message) {
    super(message);
  }
}
