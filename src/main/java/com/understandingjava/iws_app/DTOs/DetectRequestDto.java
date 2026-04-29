package com.understandingjava.iws_app.DTOs;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetectRequestDto {

    @NotBlank(message = "Merchant code is required")
    private String merchantCode;

    @NotBlank(message = "Card number is required")
    private String cardNo;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "IP address is required")
    private String ipAddress;
}
