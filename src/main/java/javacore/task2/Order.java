package javacore.task2;

import javacore.task2.enums.OrderStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

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
