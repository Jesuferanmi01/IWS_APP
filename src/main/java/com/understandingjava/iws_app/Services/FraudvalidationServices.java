package com.understandingjava.iws_app.Services;

import com.understandingjava.iws_app.Execeptions.CustomException;
import com.understandingjava.iws_app.Models.EventLog;
import com.understandingjava.iws_app.Models.Logged;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class FraudvalidationServices {

    private static final LocalTime TIME_2330 = LocalTime.of(23, 30);
    private static final LocalTime TIME_0430 = LocalTime.of(4, 30);

    private static final int SCORE_HIGH_RISK   = 60;
    private static final int SCORE_VERY_HIGH   = 25;
    private static final int SCORE_HIGH        = 20;
    private static final int SCORE_MEDIUM_HIGH = 18;
    private static final int SCORE_MEDIUM      = 15;
    private static final int SCORE_LOW         = 10;
    private static final int SCORE_MINIMAL     = 5;

    private static final int VELOCITY_LOW_LIMIT    = 5;
    private static final int VELOCITY_MEDIUM_LIMIT = 15;
    private static final int VELOCITY_HIGH_LIMIT   = 30;



    @Logged(service = "VALIDATION")
    public int validateIpAddress(Time storedTime, boolean knownUserIp) {

        if (storedTime == null) {
            // Unknown IP — never seen in our system
            return SCORE_HIGH_RISK;
        }

        LocalTime ipAccessTime = storedTime.toLocalTime();

        if (!knownUserIp) {
            // IP exists in system but not for this merchant
            return SCORE_MEDIUM_HIGH;
        }

        if (!ipAccessTime.isBefore(TIME_2330)) {
            // Known IP, after 23:30 — elevated late-night risk
            return SCORE_MEDIUM;
        }

        if (ipAccessTime.isBefore(TIME_0430)) {
            // Known IP, between midnight and 04:30 — high risk window
            return SCORE_HIGH;
        }

        // Known IP, normal hours
        return SCORE_LOW;
    }



    @Logged(service = "VALIDATION")
    public int validateAmount(BigDecimal transactionAmount, Double averageAmount) {

        if (transactionAmount == null) {
            throw new CustomException.ValidationException(
                    "Transaction amount cannot be null during fraud validation.");
        }

        if (transactionAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomException.ValidationException(
                    "Transaction amount must be greater than zero. Received: "
                            + transactionAmount);
        }

        if (averageAmount == null || averageAmount == 0) {

            return SCORE_LOW;
        }

        return transactionAmount.doubleValue() > averageAmount
                ? SCORE_VERY_HIGH
                : SCORE_LOW;
    }


    @Logged(service = "VALIDATION")
    public int validateCard(String cardStatus) {

        if (cardStatus == null) {
            // Card not found in our system — treat as blocked
            return SCORE_HIGH_RISK;
        }

        return switch (cardStatus.toUpperCase()) {
            case "ACTIVE"     -> SCORE_LOW;
            case "BLOCKED"    -> SCORE_HIGH_RISK;
            case "EXPIRED"    -> SCORE_HIGH_RISK;
            case "SUSPENDED"  -> SCORE_VERY_HIGH;
            case "RESTRICTED" -> SCORE_MEDIUM_HIGH;
            default -> throw new CustomException.ValidationException(   "Unrecognised card status: '" + cardStatus
                            + "'. Expected: ACTIVE, BLOCKED, SUSPENDED, EXPIRED, RESTRICTED.");
        };
    }



    @Logged(service = "VALIDATION")
    public int validateVelocity(int transactionCount) {

        if (transactionCount < 0) {
            throw new CustomException.ValidationException(
                    "Transaction count cannot be negative. Received: " + transactionCount);
        }

        if (transactionCount <= VELOCITY_LOW_LIMIT)    return SCORE_MINIMAL;
        if (transactionCount <= VELOCITY_MEDIUM_LIMIT) return SCORE_LOW;
        if (transactionCount <= VELOCITY_HIGH_LIMIT)   return SCORE_MEDIUM;

        return SCORE_HIGH;
    }



    public Time generateRandomTime() {
        LocalTime randomTime = LocalTime.of(
                (int) (Math.random() * 24),
                (int) (Math.random() * 60),
                (int) (Math.random() * 60)
        );
        return Time.valueOf(randomTime);
    }
}

