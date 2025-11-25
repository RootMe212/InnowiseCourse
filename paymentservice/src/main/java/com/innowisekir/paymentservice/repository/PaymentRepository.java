package com.innowisekir.paymentservice.repository;


import com.innowisekir.paymentservice.entity.Payment;
import com.innowisekir.paymentservice.entity.status.PaymentStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

  List<Payment> findByOrderId(Long orderId);

  List<Payment> findByUserId(Long userId);

  List<Payment> findByStatusIn(List<PaymentStatus> statuses);

  List<Payment> findByTimestampBetween(LocalDateTime from, LocalDateTime to);

}
