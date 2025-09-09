package javacore.task2;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Customer {

  private String customerId;

  private String name;

  private String email;

  private LocalDateTime registeredAt;

  private int age;

  private String city;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Customer)) {
      return false;
    }
    Customer customer = (Customer) o;
    return Objects.equals(customerId, customer.customerId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(customerId);
  }
}
