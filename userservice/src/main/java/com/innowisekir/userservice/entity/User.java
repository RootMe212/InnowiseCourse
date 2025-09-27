package com.innowisekir.userservice.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Name cannot be blank")
  @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
  @Column(name = "name", nullable = false)
  private String name;

  @NotBlank(message = "Surname cannot be blank")
  @Size(min = 2, max = 50, message = "Surname must be between 2 and 50 characters")
  @Column(name = "surname", nullable = false)
  private String surname;

  @Column(name = "birth_date", nullable = false)
  private LocalDate birthDate;

  @Email(message = "Invalid email format")
  @NotBlank(message = "Email cannot be blank")
  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  private List<CardInfo> cards;
}
