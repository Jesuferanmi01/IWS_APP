package com.understandingjava.iws_app.Repos;

import com.understandingjava.iws_app.Models.Cards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ICardRepo extends JpaRepository<Cards, Integer> {


     @Query("SELECT c.status FROM Cards c WHERE c.cardNo = :cardNo")
     String findStatusByCardNo(@Param("cardNo") String cardNo);
}
