package com.innowisekir.paymentservice.exception;

public class RandomNumberApiException extends RuntimeException {

  public RandomNumberApiException(String m) {
    super(m);
  }

  public RandomNumberApiException(String message, Throwable cause) {
    super(message, cause);
  }
}
