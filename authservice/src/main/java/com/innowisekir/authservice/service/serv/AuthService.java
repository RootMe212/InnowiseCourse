package com.innowisekir.authservice.service.serv;

import com.innowisekir.authservice.dto.LoginRequest;
import com.innowisekir.authservice.dto.RegisterRequest;
import com.innowisekir.authservice.dto.TokenResponse;

public interface AuthService {

  TokenResponse login(LoginRequest loginRequest);

  TokenResponse register(RegisterRequest registerRequest);


}
