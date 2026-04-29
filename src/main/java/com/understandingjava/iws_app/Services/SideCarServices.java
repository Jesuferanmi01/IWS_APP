package com.understandingjava.iws_app.Services;

import com.understandingjava.iws_app.DTOs.DetectRequestDto;
import com.understandingjava.iws_app.DTOs.DetectResponseDTO;
import com.understandingjava.iws_app.DTOs.VerifyRequestDTO;
import com.understandingjava.iws_app.Execeptions.CustomException;
import com.understandingjava.iws_app.Models.Logged;
import com.understandingjava.iws_app.Models.Transactions;
import com.understandingjava.iws_app.Repos.ITransactionRepo;
import com.understandingjava.iws_app.Repos.IpAddressRepo;
import com.understandingjava.iws_app.Repos.JdbcSideCarRepo;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class SideCarServices {

    private final RiskCalculationService risk;
    private final IpAddressRepo repo;
    private final JdbcSideCarRepo jdbcRepo;
    private final EventLogService log;
    private final RateLimiterService  rateLimiter;
    private final CacheService cacheService;
    private final ITransactionRepo transactionRepo;
    private final AuthHelpersServices authHelpers;

    private static final int FRAUD_THRESHOLD = 50;


    @Logged(service = "SIDECAR")
    public DetectResponseDTO isFraudulent(DetectRequestDto request) {

        log.event("SIDECAR", "isFraudulent — started", request.getMerchantCode(), request.getIpAddress());

        rateLimiter.rateLimiter(request.getIpAddress());

        if (cacheService.isBlacklisted(request.getMerchantCode())) {
            throw new CustomException.ValidationException("Merchant is blacklisted");
        }

        int risk1 = risk.ipAddress(request.getMerchantCode(), request.getIpAddress());
        // log.action("SIDECAR", "risk1 ipAddress", "score", risk1);

        int risk2 = risk.amount(request.getMerchantCode(), request.getAmount());
        //log.action("SIDECAR", "risk2 amount", "score", risk2);

        int risk3 = risk.velocity(request.getMerchantCode());
        //log.action("SIDECAR", "risk3 velocity", "score", risk3);

        int risk4 = risk.cardNumber(request.getCardNo());
        // log.action("SIDECAR", "risk4 cardNumber", "score", risk4);


        int sum = risk1 + risk2 + risk3 + risk4;
        System.out.println("risk = " + risk1 + " " + risk2 + " " + risk3 + " " + risk4);

        String transRef = risk.generateTransRef();

        // Step 5: Decision
        if (sum > FRAUD_THRESHOLD) {
            //log.warn("[SIDECAR] Fraud detected — score={}, threshold={}, transRef={}", sum, FRAUD_THRESHOLD, transRef);
            return jdbcRepo.flagTransaction(request,
                    transRef
            );
        } else {
            //log.info("[SIDECAR] No fraud detected — score={}, transRef={}", sum, transRef);
            return jdbcRepo.approveTransaction(
                    //request.getUserCode(),
                    request.getCardNo(),
                    request.getAmount(),
                    request.getMerchantCode(),
                    request.getIpAddress(),
                    transRef
            );
        }
    }

    public DetectResponseDTO verify(VerifyRequestDTO request, String jwtToken) {
        // log.info("[SIDECAR] OTP verification — transRef={}", request.getTransRef());

        String userCode = extractUserCodeFromToken(jwtToken);


        Transactions transaction = transactionRepo.findFlaggedTransaction(request.getTransRef(), userCode);
        if (transaction == null) {
            throw new CustomException.NotFoundException(
                    "No flagged transaction found for transRef '" + request.getTransRef() + "'.");
        }


        if (!request.getOtp().equals(transaction.getToken())) {
            //   log.warn("[SIDECAR] Invalid OTP for transRef={}", request.getTransRef());
            throw new CustomException.ValidationException(
                    "Invalid OTP. Please check and try again.");
        }

        // log.info("[SIDECAR] OTP validated — calling sp_verify_approve for transRef={}",request.getTransRef());

        return jdbcRepo.verifyApprove(request.getTransRef(), userCode);
    }

    private String extractUserCodeFromToken(String jwtToken) {
        try {
            String token = jwtToken.startsWith("Bearer ")
                    ? jwtToken.substring(7)
                    : jwtToken;
            Claims claims = authHelpers.extractClaims(token);
            return claims.get("userCode", String.class);
        } catch (Exception ex) {
            throw new CustomException.ValidationException("Invalid or expired token.");
        }
    }
}


