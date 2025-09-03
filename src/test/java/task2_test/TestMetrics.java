package task2_test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task2.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 Metrics
 List of unique cities where orders came from
 Total income for all completed orders
 The most popular product by sales
 Average check for successfully delivered orders
 Customers who have more than 5 orders
 */
public class TestMetrics {
    private List<Order> orderList;

    @BeforeEach
    public void setUp() {
        Customer customer1 = new Customer("1", "Kirill", "kiril@gmail.com", LocalDateTime.now(), 20, "Minsk");
        Customer customer2 = new Customer("2", "John", "John@gmail.com",  LocalDateTime.now(), 22, "Brest");
        Customer customer3 = new Customer("3", "Bob", "bob@gmail.com", LocalDateTime.now(), 21, "Minsk");

        OrderItem item1 = new OrderItem("Laptop", 1, 1201.0, Category.ELECTRONICS);
        OrderItem item2 = new OrderItem("Mouse", 2, 25.0, Category.ELECTRONICS);
        OrderItem item3 = new OrderItem("Shirt", 1, 20.0, Category.CLOTHING);
        OrderItem item4 = new OrderItem("Book", 1, 15.0, Category.BOOKS);

        Order order1 = new Order("O1", LocalDateTime.now(), customer1, List.of(item1), OrderStatus.DELIVERED);
        Order order2 = new Order("O2", LocalDateTime.now(), customer2,  List.of(item3), OrderStatus.DELIVERED);
        Order order3 = new Order("O3", LocalDateTime.now(), customer3,  List.of(item4), OrderStatus.DELIVERED);
        Order order4 = new Order("O4", LocalDateTime.now(), customer1,  List.of(item2), OrderStatus.CANCELLED);
        Order order5 = new Order("O5", LocalDateTime.now(), customer1,  List.of(item4), OrderStatus.PROCESSING);
        Order order6 = new Order("O6", LocalDateTime.now(), customer1,  List.of(item4), OrderStatus.SHIPPED);
        Order order7 = new Order("O7", LocalDateTime.now(), customer1,  List.of(item4), OrderStatus.NEW);
        Order order8 = new Order("O8", LocalDateTime.now(), customer1,  List.of(item4), OrderStatus.DELIVERED);
        orderList = new ArrayList<>(List.of(order1, order2, order3, order4, order5, order6, order7,order8));
    }

    @Test
    void testGetUniqueCities() {
        Set<String> uniqueCities = Metrics.uniqueCities(orderList) ;

        assertEquals(2, uniqueCities.size());
        assertEquals(Set.of("Brest", "Minsk"), uniqueCities);
    }

    @Test
    void testGetTotalIncome() {
        double totalIncome = Metrics.totalIncome(orderList);

        //1201 + 20+15 +15 = 1251
        assertEquals(1251.0, totalIncome, 0.001);
    }

    @Test
    void testGetMostPopularProduct() {
        String mostPopular = Metrics.mostPopularProduct(orderList);

        assertEquals("Book", mostPopular);
    }

    @Test
    void testGetAverageCheck() {
        double averageCheck = Metrics.AverageCheck(orderList);

        //1251/4 = 312.75
        assertEquals(312.75, averageCheck, 0.01);
    }

    @Test
    void testHaveMore5Orders() {
        List<Customer> customers = Metrics.haveMoreFiveOrders(orderList);


        assertEquals(1, customers.size());
        assertEquals("1", customers.get(0).getCustomerId());
        assertEquals("Kirill", customers.get(0).getName());
    }


}