package com.innowisekir.orderservice.repository;

import com.innowisekir.orderservice.entity.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

  @Query("select o from Order o where o.id in :ids")
  List<Order> findOrdersByIds(@Param("ids") List<Long> ids);

  @Query("select o from Order o where o.status in :statuses")
  List<Order> findOrdersByStatuses(@Param("statuses") List<String> statuses);

  @Modifying(clearAutomatically = true)
  @Transactional
  @Query(value = "update orders set status = :status where id = :id", nativeQuery = true)
  int updateOrderById(@Param("status") String status, @Param("id") Long id);

  @Modifying(clearAutomatically = true)
  @Transactional
  @Query(value = "delete from orders where id = :id", nativeQuery = true)
  int deleteOrderById(@Param("id") Long id);
}
