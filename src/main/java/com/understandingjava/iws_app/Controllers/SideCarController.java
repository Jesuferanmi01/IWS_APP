package com.understandingjava.iws_app.Controllers;


import com.understandingjava.iws_app.DTOs.DetectRequestDto;
import com.understandingjava.iws_app.DTOs.DetectResponseDTO;
import com.understandingjava.iws_app.Services.SideCarServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/IWS")
@RequiredArgsConstructor
public class SideCarController {

    private final SideCarServices sideCarServices;

    @PostMapping("/detect")
    public ResponseEntity<DetectResponseDTO> detect(@Valid @RequestBody DetectRequestDto request){
        //log.info("[CONTROLLER] /detect — userCode={}", request.getUserCode());
        DetectResponseDTO response = sideCarServices.isFraudulent(request);

        return ResponseEntity.ok(response);
    }

}
