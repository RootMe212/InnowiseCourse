package javacore.task2;

import javacore.task2.enums.Category;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderItem {

  private String productName;
  private int quantity;
  private double price;
  private Category category;

}
