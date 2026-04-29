package com.understandingjava.iws_app.Services;

import com.understandingjava.iws_app.Models.Transactions;
import com.understandingjava.iws_app.Repos.ITransactionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Random;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FlagTransactionService {

    private final ITransactionRepo transactionRepo;

    @Transactional
    public Transactions handleFlaggedTransaction(Transactions tx) {


        long flaggedCount = transactionRepo.countByMerchantCodeAndStatusIn(
                tx.getMerchantCode(),
                List.of("FLAGGED", "BLACKLISTED")
        );


        if (flaggedCount > 2) {
            tx.setStatus("BLACKLISTED");
        } else {
            tx.setStatus("FLAGGED");
        }


        String token = String.format("%06d", new Random().nextInt(1_000_000));
        tx.setToken(token);


        tx.setCreatedAt(LocalDateTime.now());


        return transactionRepo.save(tx);
    }
}