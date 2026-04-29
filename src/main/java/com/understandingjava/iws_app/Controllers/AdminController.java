package com.understandingjava.iws_app.Controllers;

import com.understandingjava.iws_app.DTOs.TransactionResponseDTO;
import com.understandingjava.iws_app.Models.EventLog;
import com.understandingjava.iws_app.Models.IpAddress;
import com.understandingjava.iws_app.Repos.IpAddressRepo;
import com.understandingjava.iws_app.Services.AdminService;
import com.understandingjava.iws_app.Services.EventLogService;
import com.understandingjava.iws_app.Util.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final IpAddressRepo ipAddressRepo;
    private final EventLogService log;

    @GetMapping("/transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TransactionResponseDTO>> getTransactions(@RequestParam(required = false) String status, @RequestParam(defaultValue = "0")  int page, @RequestParam(defaultValue = "10") int size) {

        log.action("[ADMIN CONTROLLER] get all transaction — username={}", "test");
        return ResponseEntity.ok(adminService.getTransactions(status, page, size));
    }


    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<EventLog>> getLogs(@RequestParam(required = false) String service, @RequestParam(defaultValue = "0")  int page, @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(adminService.getLogs(service, page, size));
    }

    @GetMapping("/flagged-ips")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<IpAddress>>> getFlaggedIps() {

        List<IpAddress> flagged = ipAddressRepo.findByIsFlaggedTrue();

        return ResponseEntity.ok(ApiResponse.success("sucess", flagged ));
    }
}
