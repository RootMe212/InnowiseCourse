package com.innowisekir.authservice.service.serv;

public interface UserValidationService {

  void ensureUserExistsInUserService(Long userId);

  void ensureUsernameAvailable(String username);

  void ensureNoCredentialsForUserId(Long userId);
}
