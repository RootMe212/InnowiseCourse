package com.innowisekir.paymentservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowisekir.paymentservice.exception.RandomNumberApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RandomNumberService {
  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private static final String API_URL =
      "https://www.randomnumberapi.com/api/v1.0/random?min=1&max=100&count=1";

  private final String apiUrl;

  public RandomNumberService(RestTemplate restTemplate,
      @Value("${random.number.api.url:" + API_URL + "}") String apiUrl) {
    this.restTemplate = restTemplate;
    this.apiUrl = apiUrl;
  }

  public int getRandomNumber() {
    try {
      String body = restTemplate.getForObject(apiUrl, String.class);
      if (body == null || body.isBlank()) {
        throw new RandomNumberApiException("Empty response from random API");
      }
      int[] arr = objectMapper.readValue(body, int[].class);
      if (arr.length == 0) {
        throw new RandomNumberApiException("No numbers in API response");
      }
      int n = arr[0];
      if (n < 1 || n > 100) {
        throw new RandomNumberApiException("Out of range: " + n);
      }
      return n;
    } catch (Exception e) {
      throw new RandomNumberApiException("Random API failed", e);
    }
  }
}