package com.understandingjava.iws_app.Controllers;


import com.understandingjava.iws_app.DTOs.DetectRequestDto;
import com.understandingjava.iws_app.DTOs.DetectResponseDTO;
import com.understandingjava.iws_app.DTOs.VerifyRequestDTO;
import com.understandingjava.iws_app.Services.SideCarServices;
import com.understandingjava.iws_app.Util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ISW")
@RequiredArgsConstructor
public class SideCarController {

    private final SideCarServices sideCarServices;

    @PostMapping("/detect")
    public ResponseEntity<ApiResponse<DetectResponseDTO>> detect(
            @Valid @RequestBody DetectRequestDto request) {

        DetectResponseDTO response = sideCarServices.isFraudulent(request);

        return ResponseEntity.ok(  ApiResponse.success(response.getMessage(), response) );
    }

    @PostMapping("/verify")
    //@PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<DetectResponseDTO> verify(
            @Valid @RequestBody VerifyRequestDTO request) {

        //log.info("[CONTROLLER] /verify — transRef={}", request.getTransRef());
        DetectResponseDTO response = sideCarServices.verify(request);
        return ResponseEntity.ok(response);
    }

}
