package com.understandingjava.iws_app.DTOs;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetectResponseDTO {
    private String transRef;
    private String status;
    private String token;
    private String message;
}

