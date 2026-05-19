package com.understandingjava.iws_app.Repos;

import com.understandingjava.iws_app.DTOs.DetectRequestDto;
import com.understandingjava.iws_app.DTOs.DetectResponseDTO;
import com.understandingjava.iws_app.Execeptions.CustomException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcSideCarRepo {

    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcCall flagCall;
    private final SimpleJdbcCall approveCall;

        public JdbcSideCarRepo(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;

            this.flagCall = new SimpleJdbcCall(jdbcTemplate)
                    .withSchemaName("dbo")
                    .withProcedureName("sp_flag_transaction")
                    .withoutProcedureColumnMetaDataAccess()
                    .declareParameters(
                            new SqlParameter("p_card_no", Types.VARCHAR),
                            new SqlParameter("p_amount", Types.DECIMAL),
                            new SqlParameter("p_merchant_code", Types.VARCHAR),
                            new SqlParameter("p_ip_address", Types.VARCHAR),
                            new SqlParameter("p_trans_ref", Types.VARCHAR)
                    );

            this.approveCall = new SimpleJdbcCall(jdbcTemplate)
                    .withSchemaName("dbo")
                    .withProcedureName("sp_approve_transaction")
                    .withoutProcedureColumnMetaDataAccess()
                    .declareParameters(
                            new SqlParameter("p_card_no", Types.VARCHAR),
                            new SqlParameter("p_amount", Types.DECIMAL),
                            new SqlParameter("p_merchant_code", Types.VARCHAR),
                            new SqlParameter("p_ip_address", Types.VARCHAR),
                            new SqlParameter("p_trans_ref", Types.VARCHAR)
                    );
        }

        public DetectResponseDTO flagTransaction(DetectRequestDto request, String transRef) {
            try {
                Map<String, Object> result = flagCall.execute(Map.of(
                        "p_card_no", request.getCardNo(),
                        "p_amount", request.getAmount(),
                        "p_merchant_code", request.getMerchantCode(),
                        "p_ip_address", request.getIpAddress(),
                        "p_trans_ref", transRef
                ));

                List<Map<String, Object>> rs =
                        (List<Map<String, Object>>) result.get("#result-set-1");

                Map<String, Object> row = rs.get(0);

                String status = row.get("status").toString();
                String token  = row.get("token") == null ? null : row.get("token").toString();

                System.out.println("-----------------------------");
                System.out.println("token: " + token);
                System.out.println("-----------------------------");

                return DetectResponseDTO.builder()
                        .transRef(transRef)
                        .status(status)
                        .message("FLAGGED".equals(status)
                                ? "Fraud detected. OTP sent for verification."
                                : "Transaction blocked. Merchant account has been blacklisted.")
                        .build();

            } catch (Exception ex) {
                throw new CustomException.DatabaseException(
                        "Failed to flag transaction: " + ex.getMessage(), ex);
            }
        }

        public DetectResponseDTO approveTransaction(String cardNo,
                                                    BigDecimal amount,
                                                    String merchantCode,
                                                    String ipAddress,
                                                    String transRef) {
            try {
                Map<String, Object> result = approveCall.execute(Map.of(
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

                System.out.println("token: " + token);
                return DetectResponseDTO.builder()
                        .transRef(transRef)
                        .status(status)
                        .token(token)
                        .message("No fraud detected. Transaction approved.")
                        .build();

            } catch (Exception ex) {
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

                Map<String, Object> params = new HashMap<>();
                params.put("p_trans_ref", transRef);
                params.put("p_user_code", userCode);

                Map<String, Object> result = call.execute(params);

                String status = result.get("p_status") != null
                        ? result.get("p_status").toString()
                        : null;

                String token = result.get("p_token") != null
                        ? result.get("p_token").toString()
                        : null;

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
