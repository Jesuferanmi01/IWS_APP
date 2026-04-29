package com.understandingjava.iws_app.Services;

import com.understandingjava.iws_app.Models.EventLog;
import com.understandingjava.iws_app.Repos.EventLogJdbcRepo;
import com.understandingjava.iws_app.Repos.IEventLogRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * EventLog — application-wide structured logging service.
 *
 * Provides two polymorphic families:
 *
 *   event(...)  — for domain/business events (fraud decision, OTP sent, rate-limit hit …)
 *   action(...) — for system/operational actions (DB call, validation step, JDBC call …)
 *
 * Both families are fully polymorphic (1–5 args) and always write through JDBC
 * via sp_insert_log so the hot path never throws — failures are swallowed and
 * printed to stderr so they never crash the main flow.
 *
 * Read operations go through JPA (ILogRepo) with DESC sort on createdAt.
 *
 * Usage in any service:
 *
 *   @Autowired / constructor-inject EventLog log;
 *
 *   log.event("FRAUD",  "Transaction flagged",  transRef, ip);
 *   log.action("RISK",  "IP score calculated",  ip, String.valueOf(score));
 *   log.action("JDBC",  "sp_flag_transaction called");
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventLogService {

    private final EventLogJdbcRepo logJdbcRepo;   // JDBC write path
    private final IEventLogRepo logRepo;        // JPA read path

    public void event(String service, String message) {
        persist(service, message, null, null, null);
    }


    public void event(String service, String message, String detail1) {
        persist(service, message, detail1, null, null);
    }


    public void event(String service, String message, String detail1, String detail2) {
        persist(service, message, detail1, detail2, null);
    }


    public void event(String service, String message, String detail1, String detail2, String detail3) {
        persist(service, message, detail1, detail2, detail3);
    }


    public void event(String service, String message, String detail1, int detail2) {
        persist(service, message, detail1, String.valueOf(detail2), null);
    }


    public void event(String service, String message, int detail1, int detail2) {
        persist(service, message, String.valueOf(detail1), String.valueOf(detail2), null);
    }



    public void action(String service, String message) {
        persist(service, message, null, null, null);
    }


    public void action(String service, String message, String detail1) {
        persist(service, message, detail1, null, null);
    }

    public void action(String service, String message, String detail1, String detail2) {
        persist(service, message, detail1, detail2, null);
    }

    public void action(String service, String message, String detail1, String detail2, String detail3) {
        persist(service, message, detail1, detail2, detail3);
    }

    public void action(String service, String message, String detail1, int detail2) {
        persist(service, message, detail1, String.valueOf(detail2), null);
    }

    public void action(String service, String message, int detail1, int detail2) {
        persist(service, message, String.valueOf(detail1), String.valueOf(detail2), null);
    }

    public Page<EventLog> getLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return logRepo.findAll(pageable);
    }

    public Page<EventLog> getLogsByService(String service, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return logRepo.findByService(service.toUpperCase(), pageable);
    }


    private void persist(String service, String message, String detail1, String detail2, String detail3) {

        logJdbcRepo.insertLog(service, message, detail1, detail2, detail3);
        // Mirror to SLF4J so the log also appears in the app console/file
        log.info("[{}] {} | {} | {} | {}", service, message, detail1, detail2, detail3);
    }
}