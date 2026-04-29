package com.understandingjava.iws_app.Services;

import com.understandingjava.iws_app.Execeptions.RateLimitExceededException;
import com.understandingjava.iws_app.Repos.IpAddressRepo;
import com.understandingjava.iws_app.Repos.RateLimiterRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final RateLimiterRepo rateLimiterRepo;
    private final EventLogService   log;
    private final IpAddressRepo  ipAddressRepo;

    private static final long WINDOW_SECONDS = 60L;
    private static final int  MAX_REQUESTS   = 5;

    @Transactional
    public void rateLimiter(String ipAddress) {


        int count = rateLimiterRepo.recordAndCount(ipAddress, WINDOW_SECONDS);

        log.action("RATE_LIMIT", "request count for IP",
                "ip=" + ipAddress, "count=" + count, "max=" + MAX_REQUESTS);

        if (count > MAX_REQUESTS) {


            ipAddressRepo.upsertFlagged(ipAddress);

            log.event("RATE_LIMIT", "IP flagged — too many requests",
                    "ip=" + ipAddress, "count=" + count);

            throw new RateLimitExceededException(ipAddress);
        }
    }
}