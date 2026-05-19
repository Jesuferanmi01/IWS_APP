package com.understandingjava.iws_app;


import com.understandingjava.iws_app.Services.EventLogService;
import com.understandingjava.iws_app.Services.FraudvalidationServices;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("FraudvalidationServices")
public class FraudvalidationServicesTest {

    @Mock
    EventLogService log;
    @InjectMocks
    FraudvalidationServices validation;

//    @Nested
//    @DisplayName("validateIpAddress()")
//    class ValidateIpAddress {
//        @Test
//        @DisplayName("if time is equal to null return score of 60")
//        void nullTime_returns60() {
//            // Arrange
//            Time time = null;
//            // Act
//            int result = validation.validateIpAddress(time);
//            // Assert
//            assertThat(result).isEqualTo(60);
//        }
//
//        @Test
//        @DisplayName("if time is before 23:00 return score of 10")
//        void beforeTwelve_returns10() {
//            Time time = Time.valueOf(LocalTime.of(14, 30, 0));
//            assertThat(validation.validateIpAddress(time)).isEqualTo(10);
//        }
//
//
//        @Test
//        @DisplayName("if time exactly at 23:00 return score 10 (within boundary)")
//        void exactlyAt2300_returns10() {
//            Time time = Time.valueOf(LocalTime.of(23, 0, 0));  //arrange
//            assertThat(validation.validateIpAddress(time)).isEqualTo(10);  //act and assert
//        }
//
//        @Test
//        @DisplayName("time after 23:00 but before 23:30 → score 20")
//        void between2300and2330_returns20() {
//            Time t = Time.valueOf(LocalTime.of(23, 15, 0));
//            assertThat(validation.validateIpAddress(t)).isEqualTo(20);
//        }
//
//        @Test
//        @DisplayName("time exactly at 23:30 score 20 (boundary — not after)")
//        void exactlyAt2330_returns20() {
//            Time t = Time.valueOf(LocalTime.of(23, 30, 0));
//            assertThat(validation.validateIpAddress(t)).isEqualTo(20);
//        }
//
//        @Test
//        @DisplayName("time after 23:30  score 25 (late night high risk)")
//        void after2330_returns25() {
//            Time t = Time.valueOf(LocalTime.of(23, 45, 0));
//            assertThat(validation.validateIpAddress(t)).isEqualTo(25);
//        }
//
//    }

    @Nested
    @DisplayName("validateAmount()")
    class ValidateAmount {

        @Test
        @DisplayName("if average is nul return score 10 (no history)")
        void nullAverage_returns10() {
            // Arrange
            BigDecimal amount = BigDecimal.valueOf(50000);
            Double average = null;

            // Act
            int result = validation.validateAmount(amount, average);

            // Assert
            assertThat(result).isEqualTo(10);
        }

        @Test
        @DisplayName("zero average score 10 (no history)")
        void zeroAverage_returns10() {
            // Arrange
            BigDecimal amount = BigDecimal.valueOf(40000);
            Double average = 0.0;

            // Act
            int result = validation.validateAmount(amount, average);

            // Assert
            assertThat(result).isEqualTo(10);
        }

        @Test
        @DisplayName("amount below average score 10 (normal)")
        void amountBelowAverage_returns10() {
            // Arrange
            BigDecimal amount = BigDecimal.valueOf(10000);
            Double average = 20000.0;

            // Act
            int result = validation.validateAmount(amount, average);

            // Assert
            assertThat(result).isEqualTo(10);
        }


        @Test
        @DisplayName("amount above average  score 25 (suspicious)")
        void amountAboveAverage_returns25() {
            assertThat(validation.validateAmount(BigDecimal.valueOf(50000), 20000.0))
                    .isEqualTo(25);
        }

    }

    @Nested
    @DisplayName("validateCard()")
    class ValidateCard {

        @Test
        @DisplayName("if card status is null score 60 (treat as blocked)")
        void nullStatus_returns60() {
            assertThat(validation.validateCard(null)).isEqualTo(60);
        }

        @Test
        @DisplayName("if card status is BLOCKED  score 60")
        void blockedUppercase_returns60() {
            // Arrange
            String status = "BLOCKED";

            // Act
            int result = validation.validateCard(status);

            // Assert
            assertThat(result).isEqualTo(60);
        }

        @Test
        @DisplayName("ACTIVE status → score 10")
        void activeStatus_returns10() {
            assertThat(validation.validateCard("ACTIVE")).isEqualTo(10);
        }
    }
}
