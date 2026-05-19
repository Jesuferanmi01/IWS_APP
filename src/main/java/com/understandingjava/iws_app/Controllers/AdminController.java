package com.understandingjava.iws_app.Controllers;

import com.understandingjava.iws_app.DTOs.PagedResponse;
import com.understandingjava.iws_app.DTOs.TransactionResponseDTO;
import com.understandingjava.iws_app.DTOs.UserResponseDTO;
import com.understandingjava.iws_app.Models.EventLog;
import com.understandingjava.iws_app.Models.IpAddress;
import com.understandingjava.iws_app.Models.Users;
import com.understandingjava.iws_app.Repos.IUseRepo;
import com.understandingjava.iws_app.Repos.IpAddressRepo;
import com.understandingjava.iws_app.Services.AdminService;
import com.understandingjava.iws_app.Services.EventLogService;
import com.understandingjava.iws_app.Util.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final IUseRepo  userRepo;

//    @GetMapping("/transactions")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Page<TransactionResponseDTO>> getTransactions(@RequestParam(required = false) String status, @RequestParam(defaultValue = "0")  int page, @RequestParam(defaultValue = "10") int size) {
//
//        log.action("[ADMIN CONTROLLER] get all transaction — username={}", "test");
//        return ResponseEntity.ok(adminService.getTransactions(status, page, size));
//    }

    @GetMapping("/transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<TransactionResponseDTO>>> getTransactions(
            @RequestParam(required = false)              String status,
            @RequestParam(defaultValue = "desc")         String sortDir,
            @RequestParam(defaultValue = "0")            int    page,
            @RequestParam(defaultValue = "10")           int    size) {

        log.action("ADMIN", "getTransactions",     "status=" + status, "sortDir=" + sortDir,   "page=" + page + " size=" + size);

        Page<TransactionResponseDTO> result =
                adminService.getTransactions(status, sortDir, page, size);

        return ResponseEntity.ok(
                ApiResponse.success(  "Transactions retrieved successfully",  PagedResponse.from(result)  )
        );
    }



    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<EventLog>> getLogs(@RequestParam(required = false) String service, @RequestParam(defaultValue = "0")  int page, @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(adminService.getLogs(service, page, size));
    }

    @GetMapping("/blacklisted-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponseDTO>>> getBlacklistedUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Users> usersPage =   userRepo.findByStatusIgnoreCase("BLACKLISTED", pageable);

        Page<UserResponseDTO> result = usersPage.map(user ->
                new UserResponseDTO(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getUserCode(),
                        user.getStatus(),
                        user.getUserType().name(),
                        user.getIpAddress()
                )
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Blacklisted users retrieved successfully",
                        PagedResponse.from(result)
                )
        );
    }

    @GetMapping("/flagged-ips")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<IpAddress>>> getFlaggedIps(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        log.action("ADMIN", "getFlaggedIps", "page=" + page, "size=" + size);

        Pageable pageable = PageRequest.of(page, size);
        Page<IpAddress> result = ipAddressRepo.findByIsFlaggedTrue(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Flagged IPs retrieved successfully",
                        PagedResponse.from(result)
                )
        );
    }
}
