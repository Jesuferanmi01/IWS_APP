package com.understandingjava.iws_app.Repos;

import com.understandingjava.iws_app.Models.Transactions;
import com.understandingjava.iws_app.Services.IFlagResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ITransactionRepo extends JpaRepository<Transactions, UUID> {
    //double getAverageAmount(String userCode);
    @Query("SELECT AVG(t.amount) FROM Transactions t WHERE t.userCode = :userCode")
    Double getAverageAmount(@Param("userCode") String userCode);

    long countByUserCodeAndCreatedAtGreaterThanEqual(String userCode, LocalDateTime since);

    @Query(value = "SELECT COUNT(*) FROM transactions", nativeQuery = true)
    long countAllTransactions();

    boolean existsByIpAddressAndMerchantCode(String ipAddress, String merchantCode);

    long countByMerchantCodeAndStatusIn(String merchantCode, List<String> statusList);

    @Query(value = """
            EXEC dbo.sp_flag_transaction
                 :cardNo,
                 :amount,
                 :merchantCode,
                 :ipAddress,
                 :transRef
            """, nativeQuery = true)
    IFlagResult callFlagTransaction(

            @Param("cardNo")        String     cardNo,
            @Param("amount")        BigDecimal amount,
            @Param("merchantCode")  String     merchantCode,
            @Param("ipAddress")     String     ipAddress,
            @Param("transRef")      String     transRef
    );

    // ── Supporting queries ────────────────────────────────────────────────────

    @Query(value = """
            SELECT COUNT(*) FROM  WHERE  merchant_Code = :merchantCode AND   status    IN ('FLAGGED', 'BLACKLISTED')      """, nativeQuery = true)
    int countFlaggedOrBlacklistedByMerchantCode(@Param("userCode") String userCode);

    @Query(value = """
            SELECT COUNT(*) FROM transactions  WHERE  user_code = :userCode  AND    status    IN ('FLAGGED', 'BLACKLISTED')    """, nativeQuery = true)
    int countFlaggedOrBlacklistedByUserCode(@Param("userCode") String userCode);



    @Query(value = "SELECT * FROM transactions " +
            "WHERE trans_ref = :transRef AND user_code = :userCode " +
            "AND status IN ('FLAGGED', 'BLACKLISTED')", nativeQuery = true)
    Transactions findFlaggedTransaction(@Param("transRef") String transRef,
                                       @Param("userCode") String userCode);
}