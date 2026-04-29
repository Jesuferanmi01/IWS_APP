package com.understandingjava.iws_app.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyRequestDTO {
    @NotBlank(message = "Transaction reference is required")
    private String transRef;

    @NotBlank(message = "OTP is required")
    private String otp;
}
