package com.innowisekir.authservice.repository;


import com.innowisekir.authservice.entity.UserCredentials;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Long> {

  Optional<UserCredentials> findByUsername(String username);

  boolean existsByUsername(String username);

  Optional<UserCredentials> findByUserId(Long userId);

}
