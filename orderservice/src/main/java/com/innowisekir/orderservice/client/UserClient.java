package com.innowisekir.orderservice.client;

import com.innowisekir.orderservice.dto.response.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "user-service",
    url = "${userservice.base-url:http://localhost:8081}"
)
public interface UserClient {

  @GetMapping("/api/v1/users/email")
  UserDTO getByEmail(@RequestParam("email") String email);
}
