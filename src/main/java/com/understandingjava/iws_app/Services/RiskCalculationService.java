package com.understandingjava.iws_app.Services;

import com.understandingjava.iws_app.DTOs.IpaddressHelpers;
import com.understandingjava.iws_app.Execeptions.CustomException;
import com.understandingjava.iws_app.Models.IpAddress;
import com.understandingjava.iws_app.Models.Logged;
import com.understandingjava.iws_app.Repos.ICardRepo;
import com.understandingjava.iws_app.Repos.ITransactionRepo;
import com.understandingjava.iws_app.Repos.IpAddressRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class RiskCalculationService {

    private final IpAddressRepo          repo;
    private final ICardRepo              cards;
    private final ITransactionRepo       trans;
    private final FraudvalidationServices validation;
    private final EventLogService               log; // injected EventLog
    private final CacheService  cacheService;

//    public int ipAddress(String merchantCode, String ipAddress) {
//        log.action("RISK", "IP check — starting", ipAddress, merchantCode);
//        try {
//            Time storedTime = findTimeByIpAddress(ipAddress, merchantCode);
//            int score = validation.validateIpAddress(storedTime);
//            log.action("RISK", "IP check — complete", ipAddress, "score", String.valueOf(score));
//            return score;
//
//        } catch (CustomException ex) {
//            log.event("RISK", "IP check — known exception", ex.getMessage());
//            throw ex;
//        } catch (Exception ex) {
//            log.event("RISK", "IP check — unexpected failure", ex.getMessage());
//            throw new CustomException.SomethingWentWrongException(
//                    "IP address validation failed. Please try again.", ex);
//        }
//    }


    @Logged(service = "RISK_CALCULATION")
    public int ipAddress(String merchantCode, String ipAddress) {
        //log.action("RISK", "IP check — starting", ipAddress, merchantCode);

        try {
            IpaddressHelpers helper = findTimeByIpAddress(ipAddress, merchantCode);

            Time storedTime = helper.getStoredTime();
            boolean knownUserIp = helper.getTransactionExist();

            int score = validation.validateIpAddress(storedTime, knownUserIp);

            //log.action("RISK", "IP check — complete", ipAddress, "score", String.valueOf(score));
            return score;

        } catch (CustomException ex) {
           // log.event("RISK", "IP check — known exception", ex.getMessage());
            throw ex;

        } catch (Exception ex) {
            //log.event("RISK", "IP check — unexpected failure", ex.getMessage());
            throw new CustomException.SomethingWentWrongException(   "IP address validation failed. Please try again.", ex);
        }
    }

    @Transactional
    @Logged(service = "RISK_CALCULATION")
    public int cardNumber(String cardNo) {
       // log.action("RISK", "Card check — starting");
        try {
            String cardStatus = cards.findStatusByCardNo(cardNo);
            int score = validation.validateCard(cardStatus);
          //  log.action("RISK", "Card check — complete", "score", String.valueOf(score));
            return score;

        } catch (CustomException ex) {
          //  log.event("RISK", "Card check — known exception", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
           // log.event("RISK", "Card check — unexpected failure", ex.getMessage());
            throw new CustomException.SomethingWentWrongException(
                    "Card validation failed. Please try again.", ex);
        }
    }

    @Transactional
    @Logged(service = "RISK_CALCULATION")
    public int amount(String userCode, BigDecimal amount) {
      //  log.action("RISK", "Amount check — starting", userCode, amount.toPlainString());
        try {
            Double average = trans.getAverageAmount(userCode);
            int score = validation.validateAmount(amount, average);
       //     log.action("RISK", "Amount check — complete", userCode, "score", String.valueOf(score));
            return score;

        } catch (CustomException ex) {
       //     log.event("RISK", "Amount check — known exception", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
         //   log.event("RISK", "Amount check — unexpected failure", ex.getMessage());
            throw new CustomException.SomethingWentWrongException(
                    "Amount validation failed. Please try again.", ex);
        }
    }

    @Transactional
    @Logged(service = "RISK_CALCULATION")
    public int velocity(String userCode) {
     //   log.action("RISK", "Velocity check — starting", userCode);
        try {
            LocalDateTime since = LocalDateTime.now(ZoneOffset.UTC).minusHours(1);
            //LocalDateTime since = LocalDateTime.now().minusHours(1);
            int count = Math.toIntExact(
                   // trans.countByUserCodeAndCreatedAtGreaterThanEqual(userCode, since));
                    trans.countByMerchantCodeAndCreatedAtGreaterThanEqual(userCode, since));
            int score = validation.validateVelocity(count);
         //   log.action("RISK", "Velocity check — complete",    userCode, "count=" + count, "score=" + score);
            return score;

        } catch (CustomException ex) {
        //    log.event("RISK", "Velocity check — known exception", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
        //    log.event("RISK", "Velocity check — unexpected failure", ex.getMessage());
            throw new CustomException.SomethingWentWrongException(
                    "Velocity validation failed. Please try again.", ex);
        }
    }

    public String generateTransRef() {
        long count = trans.countAllTransactions();
        return String.format("TXN-%05d", count + 1);
    }

    private IpaddressHelpers findTimeByIpAddress(String ip, String code) {
        boolean existsInTransaction = cacheService.isKnownIpMerchantPair(ip, code);

        Time time = cacheService.getIpTime(ip);

        return new IpaddressHelpers(time, existsInTransaction);
    }
//    private Time findTimeByIpAddress(String ip, String code) {
//        boolean existsInTransaction = trans.existsByIpAddressAndMerchantCode(ip, code);
//
//        if (existsInTransaction) {
//
//            return repo.findByIpAddress(ip)
//                    .map(IpAddress::getTime)
//                    .orElse(null);
//        }
//
//        // Fall back to the IP address table
//        return repo.findByIpAddress(ip)
//                .map(IpAddress::getTime)
//                .orElse(null);
//    }

//    private IpaddressHelpers findTimeByIpAddress(String ip, String code) {
//
//        boolean existsInTransaction =
//                trans.existsByIpAddressAndMerchantCode(ip, code);
//
//        Time time = repo.findByIpAddress(ip)
//                .map(IpAddress::getTime)
//                .orElse(null);
//
//        return new IpaddressHelpers(time, existsInTransaction);
//    }
}

