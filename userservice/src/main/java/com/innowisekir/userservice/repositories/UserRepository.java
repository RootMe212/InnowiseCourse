package com.innowisekir.userservice.repositories;

import com.innowisekir.userservice.entities.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  @Query("SELECT u from User u WHERE u.id IN :ids")
  List<User> findByIdIn(@Param("ids") List<Long> ids);

  @Modifying
  @Transactional
  @Query(value = "UPDATE users SET name = :name, surname = :surname, email = :email, birth_date = :birthDate WHERE id = :id",
      nativeQuery = true)
  int updateUserById(@Param("id") Long id, @Param("name") String name,
      @Param("surname") String surname, @Param("email") String email,
      @Param("birthDate") LocalDate birthDate);

  @Modifying
  @Transactional
  @Query(value = "DELETE FROM users WHERE id = :id", nativeQuery = true)
  int deleteUserByIdNative(@Param("id") Long id);
}
