package com.understandingjava.iws_app.Repos;

import com.understandingjava.iws_app.DTOs.DetectRequestDto;
import com.understandingjava.iws_app.DTOs.DetectResponseDTO;
import com.understandingjava.iws_app.Execeptions.CustomException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcSideCarRepo {

    private final JdbcTemplate jdbcTemplate;


    public JdbcSideCarRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

   public DetectResponseDTO flagTransaction(DetectRequestDto request, String transRef){

        try{
            SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_flag_transaction");
            Map<String, Object> result = call.execute(Map.of(
                    "p_card_no",       request.getCardNo(),
                    "p_amount",        request.getAmount(),
                    "p_merchant_code", request.getMerchantCode(),
                    "p_ip_address",    request.getIpAddress(),
                    "p_trans_ref",     transRef
            ));


            List<Map<String, Object>> rs =
                    (List<Map<String, Object>>) result.get("#result-set-1");

            Map<String, Object> row = rs.get(0);

            String status = row.get("status").toString();
            String token  = row.get("token") == null ? null : row.get("token").toString();


            // Print OTP to console so merchant can pick it up during tes ting
            System.out.println("==============================================");
            System.out.println("  OTP for userCode [" + request.getMerchantCode() + "]: " + token);
            System.out.println("==============================================");

            return DetectResponseDTO.builder()
                    .transRef(transRef)
                    .status(status)
                    // .token(token)
                    .message("FLAGGED".equals(status)
                            ? "Fraud detected. OTP sent for verification."
                            : "Transaction blocked. Merchant account has been blacklisted.")
                    .build();

        } catch (Exception ex) {
            // log.error("[JDBC] sp_flag_transaction failed: {}", ex.getMessage(), ex);
            throw new CustomException.DatabaseException(
                    "Failed to flag transaction: " + ex.getMessage(), ex);
        }
   }

//   public DetectResponseDTO approveTransaction(){
//        String  messages = "hi there you flipped the switch";
//        String  tokens = "12345";
//        String  transrefs = "TNX-001";
//        String  Status = "PASSED";
//
//        return DetectResponseDTO.builder()
//                .message(messages)
//                .token(tokens)
//                .transRef(transrefs)
//                .status(Status)
//                .build();
//
//
//   }

    public DetectResponseDTO approveTransaction( String cardNo,
                                                BigDecimal amount, String merchantCode,
                                                String ipAddress, String transRef) {
        // log.debug("[JDBC] Calling sp_approve_transaction — userCode={}, transRef={}", userCode, transRef);
        try {
            SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                    .withSchemaName("dbo")
                    .withProcedureName("sp_approve_transaction")
                    .declareParameters(
                            //new org.springframework.jdbc.core.SqlParameter("p_user_code",    Types.VARCHAR),
                            new org.springframework.jdbc.core.SqlParameter("p_card_no",      Types.VARCHAR),
                            new org.springframework.jdbc.core.SqlParameter("p_amount",       Types.DECIMAL),
                            new org.springframework.jdbc.core.SqlParameter("p_merchant_code",Types.VARCHAR),
                            new org.springframework.jdbc.core.SqlParameter("p_ip_address",   Types.VARCHAR),
                            new org.springframework.jdbc.core.SqlParameter("p_trans_ref",    Types.VARCHAR)
//                            new org.springframework.jdbc.core.SqlOutParameter("p_status",    Types.VARCHAR),
//                            new org.springframework.jdbc.core.SqlOutParameter("p_token",     Types.VARCHAR)
                    );

            Map<String, Object> result = call.execute(Map.of(
                    "p_card_no", cardNo,
                    "p_amount", amount,
                    "p_merchant_code", merchantCode,
                    "p_ip_address", ipAddress,
                    "p_trans_ref", transRef
            ));


            List<Map<String, Object>> rs =
                    (List<Map<String, Object>>) result.get("#result-set-1");

            Map<String, Object> row = rs.get(0);

            String status = row.get("status").toString();
            String token  = row.get("token") == null ? null : row.get("token").toString();
            // log.info("[JDBC] sp_approve_transaction — transRef={}, status={}", transRef, status);

            return DetectResponseDTO.builder()
                    .transRef(transRef)
                    .status(status)
                    .token(token)
                    .message("No fraud detected. Transaction approved.")
                    .build();

        } catch (Exception ex) {
            //   log.error("[JDBC] sp_approve_transaction failed: {}", ex.getMessage(), ex);
            throw new CustomException.DatabaseException(
                    "Failed to approve transaction: " + ex.getMessage(), ex);
        }
    }

    public DetectResponseDTO verifyApprove(String transRef, String userCode) {
        //  log.debug("[JDBC] Calling sp_verify_approve — transRef={}, userCode={}", transRef, userCode);
        try {
            SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                    .withSchemaName("dbo")
                    .withProcedureName("sp_verify_approve")
                    .declareParameters(
                            new org.springframework.jdbc.core.SqlParameter("p_trans_ref",  Types.VARCHAR),
                            new org.springframework.jdbc.core.SqlParameter("p_user_code",  Types.VARCHAR),
                            new org.springframework.jdbc.core.SqlOutParameter("p_status",  Types.VARCHAR),
                            new org.springframework.jdbc.core.SqlOutParameter("p_token",   Types.VARCHAR)
                    );

            Map<String, Object> result = call.execute(Map.of(
                    "p_trans_ref", transRef,
                    "p_user_code", userCode
            ));

            String status = result.get("p_status").toString();
            String token  = result.get("p_token").toString();

            // log.info("[JDBC] sp_verify_approve — transRef={}, status={}", transRef, status);

            return DetectResponseDTO.builder()
                    .transRef(transRef)
                    .status(status)
                    .token(token)
                    .message("OTP verified. Transaction approved.")
                    .build();

        } catch (Exception ex) {
            // log.error("[JDBC] sp_verify_approve failed: {}", ex.getMessage(), ex);
            throw new CustomException.DatabaseException(
                    "Failed to verify transaction: " + ex.getMessage(), ex);
        }
    }
}
