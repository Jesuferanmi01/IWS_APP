package com.understandingjava.iws_app.Services;

import com.understandingjava.iws_app.DTOs.TransactionResponseDTO;
import com.understandingjava.iws_app.Models.EventLog;
import com.understandingjava.iws_app.Models.Transactions;
import com.understandingjava.iws_app.Repos.IAdminRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final IAdminRepo adminRepo;
    private final EventLogService logService;

//    public Page<TransactionResponseDTO> getTransactions(String status, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size,
//                Sort.by(Sort.Direction.DESC, "createdAt"));
//
//        Page<Transactions> results = (status == null || status.isBlank())
//                ? adminRepo.findAll(pageable)
//                : adminRepo.findByStatus(status.toUpperCase(), pageable);
//
//        return results.map(t -> TransactionResponseDTO.builder()
//                .transRef(t.getTransRef())
//               // .userCode(t.getUserCode())
//                .cardNo(t.getCardNo())
//                .amount(t.getAmount())
//                .merchantCode(t.getMerchantCode())
//                .ipAddress(t.getIpAddress())
//                .status(t.getStatus())
//                .createdAt(t.getCreatedAt())
//                .build());
//    }
    public Page<TransactionResponseDTO> getTransactions(
            String status, String sortDir, int page, int size) {

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size,    Sort.by(direction, "createdAt"));

        Page<Transactions> results;

        if (status == null || status.isBlank()) {

            results = adminRepo.findAll(pageable);

        } else if ("FRAUD".equalsIgnoreCase(status)) {
            // Shorthand — return both flagged and blacklisted
            results = adminRepo.findByStatusIn(
                    List.of("FLAGGED", "BLACKLISTED"), pageable);

        } else {
            // Single status — PASSED, FLAGGED, or BLACKLISTED
            results = adminRepo.findByStatus(status.toUpperCase(), pageable);
        }

        return results.map(t -> TransactionResponseDTO.builder()
                .transRef(t.getTransRef())
                .cardNo(t.getCardNo())
                .amount(t.getAmount())
                .merchantCode(t.getMerchantCode())
                .ipAddress(t.getIpAddress())
                .status(t.getStatus())
                .createdAt(t.getCreatedAt())
                .build());
    }


    public Page<EventLog> getLogs(String service, int page, int size) {
        return (service == null || service.isBlank())
                ? logService.getLogs(page, size)
                : logService.getLogsByService(service, page, size);
    }
}
