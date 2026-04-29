package com.understandingjava.iws_app.Services;

import com.understandingjava.iws_app.Models.EventLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class FraudvalidationServices {

    private final EventLogService log; // injected — calls log.action(...)

    private static final LocalTime TIME_2300 = LocalTime.of(23, 0);
    private static final LocalTime TIME_2330 = LocalTime.of(23, 30);

    public int validateIpAddress(Time storedTime) {
        if (storedTime == null) {
            log.action("VALIDATION", "IP check — not found anywhere", "score", 60);
            return 60;
        }

        LocalTime ipTime = storedTime.toLocalTime();
        int score;

        if (ipTime.isAfter(TIME_2330)) {
            score = 25;
        } else if (ipTime.isAfter(TIME_2300)) {
            score = 20;
        } else {
            score = 10;
        }

        log.action("VALIDATION", "IP check", "time=" + ipTime, "score", String.valueOf(score));
        return score;
    }

    public int validateAmount(BigDecimal amount, Double average) {
        if (average == null || average == 0) {
            log.action("VALIDATION", "Amount check — no history yet", "score", 10);
            return 10;
        }

        int score = amount.doubleValue() > average ? 25 : 10;
        log.action("VALIDATION", "Amount check",
                "amount=" + amount, "average=" + average, "score=" + score);
        return score;
    }

    public int validateCard(String cardStatus) {
        if (cardStatus == null || "BLOCKED".equalsIgnoreCase(cardStatus)) {
            log.action("VALIDATION", "Card check — blocked/null", "score", 60);
            return 60;
        }
        log.action("VALIDATION", "Card check — ACTIVE", "score", 10);
        return 10;
    }

    public int validateVelocity(int count) {
        int score = count >= 4 ? 30 : 10;
        log.action("VALIDATION", "Velocity check", "count", count);
        return score;
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


