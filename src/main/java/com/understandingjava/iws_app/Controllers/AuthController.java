package com.understandingjava.iws_app.Controllers;


import com.understandingjava.iws_app.DTOs.LoginRequestDTO;
import com.understandingjava.iws_app.DTOs.LoginResponseDTO;
import com.understandingjava.iws_app.Services.AuthServices;
import com.understandingjava.iws_app.Services.EventLogService;
import com.understandingjava.iws_app.Util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthServices authService;
    private final EventLogService log;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {

        log.action("[AUTH CONTROLLER] Login request — username={}", request.getUsername());
        LoginResponseDTO response = authService.loginUser(request);

        return ResponseEntity.ok(response);
    }
}
