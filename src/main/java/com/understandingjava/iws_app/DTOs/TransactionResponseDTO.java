package com.understandingjava.iws_app.DTOs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TransactionResponseDTO {

    private String transRef;
    private String userCode;
    private BigDecimal amount;
    private String  status;
    private String cardNo;
    private LocalDateTime createdAt;
    private String ipAddress;
    private String merchantCode;
}


