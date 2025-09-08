package javacore.task2;

import java.util.*;
import java.util.stream.Collectors;
import javacore.task2.enums.OrderStatus;

/**
 * totalIncome currently sums price only, ignoring quantity. - done AverageCheck should compute
 * per-order total as sum of quantity * price of delivered items, then average across delivered
 * orders. - done Rename AverageCheck ? averageCheck (camelCase for methods). - done make sure that
 * you don’t have problem without ovveriding equals/hashCode (for example for Customer) - done
 */
public class Metrics {

  public static Set<String> uniqueCities(List<Order> orders) {
    return orders
        .stream()
        .map(order -> order.getCustomer().getCity())
        .collect(Collectors.toSet());
  }

  public static double totalIncome(List<Order> orders) {
    return orders
        .stream()
        .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
        .flatMap(order -> order.getItems().stream())
        .mapToDouble(item -> item.getPrice() * item.getQuantity()) //done
        .sum();
  }

  public static String mostPopularProduct(List<Order> orders) {
    return orders
        .stream()
        .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
        .flatMap(order -> order.getItems().stream())
        .collect(Collectors.groupingBy(
            item -> item.getProductName() + ", " + item.getCategory()
            , Collectors.summingInt(OrderItem::getQuantity)
        )).entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElse("No product");
  }

  public static double averageCheck(List<Order> orders) {
    return orders.stream()
        .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
        .mapToDouble(order -> order.getItems().stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantity()).sum())
        .average().orElse(0);
  }

  public static List<Customer> haveMoreFiveOrders(List<Order> orders) {
    return orders.stream()
        .collect(Collectors.groupingBy(Order::getCustomer, Collectors.counting()))
        .entrySet().stream().filter(customerLongEntry -> customerLongEntry.getValue() > 5)
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }

}
