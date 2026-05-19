package com.understandingjava.iws_app.DTOs;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {

    private String token;
    private String message;
    private String name;
    private String role;
    private String userCode;
    private String email;
}
