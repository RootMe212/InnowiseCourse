package com.innowisekir.userservice.exception;

public class InvalidEmailException extends RuntimeException{
  public InvalidEmailException(String message){
    super(message);
  }

}
