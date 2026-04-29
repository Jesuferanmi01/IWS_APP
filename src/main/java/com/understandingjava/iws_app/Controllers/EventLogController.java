package com.understandingjava.iws_app.Controllers;

import com.understandingjava.iws_app.DTOs.EventLogDTO;
import com.understandingjava.iws_app.Models.EventLog;
import com.understandingjava.iws_app.Services.EventLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class EventLogController {

    private final EventLogService logService;


    @GetMapping
    public Page<EventLogDTO> getAllLogs(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        return logService.getLogs(page, size).map(this::toDTO);
    }


    @GetMapping("/service/{service}")
    public Page<EventLogDTO> getLogsByService(@PathVariable String service, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        return logService.getLogsByService(service, page, size).map(this::toDTO);
    }


    private EventLogDTO toDTO(EventLog log) {
        return EventLogDTO.builder()
                .timestamp(log.getCreatedAt())
                .service(log.getService())
                .message(log.getMessage())
                .detail1(log.getDetail1())
                .detail2(log.getDetail2())
                .detail3(log.getDetail3())
                .build();
    }
}