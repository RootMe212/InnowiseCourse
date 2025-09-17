package javacore.task2;

import javacore.task2.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderItem {

  private String productName;
  private int quantity;
  private double price;
  private Category category;

}
