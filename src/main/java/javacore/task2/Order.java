package javacore.task2;

import java.time.LocalDateTime;
import java.util.List;
import javacore.task2.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Order {

  private String orderId;
  private LocalDateTime orderDate;
  private Customer customer;
  private List<OrderItem> items;
  private OrderStatus status;
}
