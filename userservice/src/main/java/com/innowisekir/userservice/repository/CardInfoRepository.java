package com.innowisekir.userservice.repository;

import com.innowisekir.userservice.entity.CardInfo;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CardInfoRepository extends JpaRepository<CardInfo, Long> {

  @Query("SELECT c from CardInfo c where c.id in :ids")
  List<CardInfo> findByIdIn(@Param("ids") List<Long> ids);

  @Modifying(clearAutomatically = true)
  @Transactional
  @Query(value = "UPDATE card_info SET number =:number, expiration_date =:expirationDate where id =:id", nativeQuery = true)
  int updateCardInfoById(
      @Param("id") Long id,
      @Param("number") String number,
      @Param("expirationDate") LocalDate expirationDate
  );

  @Modifying
  @Transactional
  @Query(value = "DELETE from card_info where id=:id", nativeQuery = true)
  int deleteCardInfoByIdNative(@Param("id") Long id);
}
