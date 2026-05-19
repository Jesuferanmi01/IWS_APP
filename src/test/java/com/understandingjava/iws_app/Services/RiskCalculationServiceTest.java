package com.understandingjava.iws_app.Services;


import com.understandingjava.iws_app.Repos.ICardRepo;
import com.understandingjava.iws_app.Repos.ITransactionRepo;
import com.understandingjava.iws_app.Repos.IpAddressRepo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;


import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RiskCalculationServices")
class RiskCalculationServiceTest {
    @Mock
    private ICardRepo cards;
    @Mock
    private FraudvalidationServices validation;
    @Mock
    private EventLogService log;
    @Mock
    IpAddressRepo repo;
    @Mock
    ITransactionRepo transactionRepo;

    @InjectMocks
    private RiskCalculationService service;

    @InjectMocks
    private RiskCalculationService riskCalculationService;

    private static final String MERCHANT = "MECT03";
    private static final String IP       = "197.210.64.1";
    private static final String CARD     = "4916338506082832";


//    @Nested
//    @DisplayName("ipAddress()")
//    class IpAddressCheck {
//
//        @Test
//        @DisplayName("check if IP exists in transactions / random time / returns validation score")
//        void ipInTransactions_returnsScore() {
//            // Arrange
//            when(transactionRepo.existsByIpAddressAndMerchantCode(IP, MERCHANT)).thenReturn(true);
//            Time randomTime = Time.valueOf(LocalTime.of(10, 0, 0));
//            when(validation.generateRandomTime()).thenReturn(randomTime);
//            when(validation.validateIpAddress(randomTime)).thenReturn(10);
//
//            // Act
//            int result = riskCalculationService.ipAddress(MERCHANT, IP);
//
//            // Assert
//            assertThat(result).isEqualTo(10);
//        }
//
//        @Test
//        @DisplayName("IP not in transactions but found in ip_address table uses stored time (time from db)")
//        void ipInTable_usesStoredTime() {
//            // Arrange
//            Time storedTime = Time.valueOf(LocalTime.of(23, 45, 0));
//            IpAddress ipRecord = new IpAddress(1, IP, storedTime, false);
//
//            when(transactionRepo.existsByIpAddressAndMerchantCode(IP, MERCHANT)).thenReturn(false);
//            when(repo.findByIpAddress(IP)).thenReturn(Optional.of(ipRecord));
//            when(validation.validateIpAddress(storedTime)).thenReturn(25);
//
//            // Act
//            int result = riskCalculationService.ipAddress(MERCHANT, IP);
//
//            // Assert
//            assertThat(result).isEqualTo(25);
//        }
//
//        @Test
//        @DisplayName("IP not found anywhere / null time / validation returns 60")
//        void ipNotFound_returns60() {
//            // Arrange
//            when(transactionRepo.existsByIpAddressAndMerchantCode(IP, MERCHANT)).thenReturn(false);
//            when(repo.findByIpAddress(IP)).thenReturn(Optional.empty());
//            when(validation.validateIpAddress(null)).thenReturn(60);
//
//            // Act
//            int result = riskCalculationService.ipAddress(MERCHANT, IP);
//
//            // Assert
//            assertThat(result).isEqualTo(60);
//        }
//    }

    @Nested
    @DisplayName("cardNumber()")
    class CardNumberCheck {

        @Test
        @DisplayName("if card status is ACTIVE returns score 10")
        void activeCard_returns10() {
            // Arrange
            String status = "ACTIVE";
            when(cards.findStatusByCardNo(CARD)).thenReturn(status);
            when(validation.validateCard(status)).thenReturn(10);

            // Act
            int result = riskCalculationService.cardNumber(CARD);

            // Assert
            assertThat(result).isEqualTo(10);
        }

        @Test
        @DisplayName("if card status is BLOCKED returns score 60")
        void blockedCard_returns60() {
            // Arrange
            String status = "BLOCKED";
            when(cards.findStatusByCardNo(CARD)).thenReturn(status);
            when(validation.validateCard(status)).thenReturn(60);

            // Act
            int result = riskCalculationService.cardNumber(CARD);

            // Assert
            assertThat(result).isEqualTo(60);
        }

        @Test
        @DisplayName("if card status is null returns score 60")
        void nullCardStatus_returns60() {
            // Arrange
            String status = null;
            when(cards.findStatusByCardNo(CARD)).thenReturn(status);
            when(validation.validateCard(status)).thenReturn(60);

            // Act
            int result = riskCalculationService.cardNumber(CARD);

            // Assert
            assertThat(result).isEqualTo(60);


        }

    }
    @Nested
    @DisplayName("amount()")
    class AmountCheck {

        @Test
        @DisplayName("if amount is below average returns score 10")
        void belowAverage_returns10() {
            when(transactionRepo.getAverageAmount(MERCHANT)).thenReturn(20000.0);
            when(validation.validateAmount(BigDecimal.valueOf(5000), 20000.0)).thenReturn(10);

            assertThat(riskCalculationService.amount(MERCHANT, BigDecimal.valueOf(5000))).isEqualTo(10);
        }

        @Test
        @DisplayName("amount above average returns score 25")
        void aboveAverage_returns25() {
            when(transactionRepo.getAverageAmount(MERCHANT)).thenReturn(20000.0);
            when(validation.validateAmount(BigDecimal.valueOf(99999), 20000.0)).thenReturn(25);

            assertThat(riskCalculationService.amount(MERCHANT, BigDecimal.valueOf(99999))).isEqualTo(25);
        }

        @Test
        @DisplayName("if average is null (no history) returns score 10")
        void nullAverage_returns10() {
            when(transactionRepo.getAverageAmount(MERCHANT)).thenReturn(null);
            when(validation.validateAmount(BigDecimal.valueOf(5000), null)).thenReturn(10);

            assertThat(riskCalculationService.amount(MERCHANT, BigDecimal.valueOf(5000))).isEqualTo(10);
        }

    }

//        @Nested
//        @DisplayName("velocity()")
//        class VelocityCheck {
//
//            @Test
//            @DisplayName("low transaction count → returns score 10")
//            void lowCount_returns10() {
//                when(trans.countByUserCodeAndCreatedAtGreaterThanEqual(
//                        eq(USER), any(LocalDateTime.class))).thenReturn(2L);
//                when(validation.validateVelocity(2)).thenReturn(10);
//
//                assertThat(risk.velocity(USER)).isEqualTo(10);
//            }
//
//            @Test
//            @DisplayName("high transaction count (>=4) → returns score 30")
//            void highCount_returns30() {
//                when(trans.countByUserCodeAndCreatedAtGreaterThanEqual(
//                        eq(USER), any(LocalDateTime.class))).thenReturn(5L);
//                when(validation.validateVelocity(5)).thenReturn(30);
//
//                assertThat(risk.velocity(USER)).isEqualTo(30);
//            }
//
//            @Test
//            @DisplayName("exactly 4 transactions → returns score 30 (threshold)")
//            void exactlyFour_returns30() {
//                when(trans.countByUserCodeAndCreatedAtGreaterThanEqual(
//                        eq(USER), any(LocalDateTime.class))).thenReturn(4L);
//                when(validation.validateVelocity(4)).thenReturn(30);
//
//                assertThat(risk.velocity(USER)).isEqualTo(30);
//            }
//
//            @Test
//            @DisplayName("zero transactions → returns score 10")
//            void zeroCount_returns10() {
//                when(trans.countByUserCodeAndCreatedAtGreaterThanEqual(
//                        eq(USER), any(LocalDateTime.class))).thenReturn(0L);
//                when(validation.validateVelocity(0)).thenReturn(10);
//
//                assertThat(risk.velocity(USER)).isEqualTo(10);
//            }
//
//            @Test
//            @DisplayName("known CustomException is re-thrown as-is")
//            void knownExceptionRethrown() {
//                when(trans.countByUserCodeAndCreatedAtGreaterThanEqual(
//                        eq(USER), any(LocalDateTime.class)))
//                        .thenThrow(new CustomException.DatabaseException("DB error", null));
//
//                assertThatThrownBy(() -> risk.velocity(USER))
//                        .isInstanceOf(CustomException.DatabaseException.class);
//            }
//
//            @Test
//            @DisplayName("unexpected exception → wrapped in SomethingWentWrongException")
//            void unexpectedExceptionWrapped() {
//                when(trans.countByUserCodeAndCreatedAtGreaterThanEqual(
//                        eq(USER), any(LocalDateTime.class)))
//                        .thenThrow(new RuntimeException("timeout"));
//
//                assertThatThrownBy(() -> risk.velocity(USER))
//                        .isInstanceOf(CustomException.SomethingWentWrongException.class)
//                        .hasMessageContaining("Velocity validation failed");
//            }
    }
