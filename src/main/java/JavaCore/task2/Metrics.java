package JavaCore.task2;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Metrics {
    public static Set<String> uniqueCities(List<Order> orders){
        return orders
                .stream()
                .map(order -> order.getCustomer().getCity())
                .collect(Collectors.toSet());
    }
    public static double totalIncome(List<Order> orders){
        return orders
                .stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .flatMap(order -> order.getItems().stream()).mapToDouble(OrderItem::getPrice).sum();
    }

    public static String mostPopularProduct(List<Order> orders){
        return orders
                .stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .flatMap(order -> order.getItems().stream())
                .collect(Collectors.groupingBy(
                        OrderItem::getProductName,Collectors.summingInt(OrderItem::getQuantity)
                )).entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No product");
    }
    public static double AverageCheck(List<Order> orders){
        return orders.stream()
                .filter(order -> order.getStatus()== OrderStatus.DELIVERED)
                .mapToDouble(order -> order.getItems().stream()
                        .mapToDouble(OrderItem::getPrice).sum())
                .average().orElse(0);
    }

    public static List<Customer> haveMoreFiveOrders(List<Order> orders){
        return orders.stream()
                .collect(Collectors.groupingBy(Order::getCustomer,Collectors.counting()))
                .entrySet().stream().filter(customerLongEntry -> customerLongEntry.getValue()>5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

}
