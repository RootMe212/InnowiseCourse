package com.innowisekir.userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "card_info")
public class CardInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private User user;

  @Column(name = "number", nullable = false, unique = true)
  private String number;

  @Formula("(SELECT CONCAT(u.name, ' ', u.surname) FROM users u WHERE u.id = user_id)")
  private String holder;

  @Column(name = "expiration_date", nullable = false)
  private LocalDate expirationDate;

}
