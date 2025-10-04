package com.innowisekir.userservice.exception;

public class EntityAlreadyDeletedException extends RuntimeException {

  public EntityAlreadyDeletedException(String message) {
    super(message);
  }
}
