package com.understandingjava.iws_app.Services;

import com.understandingjava.iws_app.Execeptions.RateLimitExceededException;
import com.understandingjava.iws_app.Repos.IpAddressRepo;
import com.understandingjava.iws_app.Repos.RateLimiterRepo;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final RateLimiterRepo rateLimiterRepo;
    private final EventLogService   log;
    private final IpAddressRepo  ipAddressRepo;
    private final ApplicationContext context;

    //private static final Duration WINDOW = Duration.ofSeconds(60);
    private static final long WINDOW_SECONDS = 60L;
    private static final int  MAX_REQUESTS   = 5;

   // @Transactional
    //public void rateLimiter(String ipAddress) {
   public void rateLimiter(String userId, String ipAddress) {

       int count = rateLimiterRepo.recordAndCount(ipAddress, userId, WINDOW_SECONDS);

       //log.action("RATE_LIMIT", "request count for IP",  "ip=" + ipAddress + " userId=" + userId, "count=" + count, "max=" + MAX_REQUESTS);

       if (count > MAX_REQUESTS) {

           //log.event("RATE_LIMIT", "IP flagged — too many requests",   "ip=" + ipAddress + " userId=" + userId, "count=" + count);
           try {
               context.getBean(IpFlagService.class).flagIp(ipAddress);
           } catch (Exception ex) {
               log.event("RATE_LIMIT", "flag DB write failed — non-critical",   ipAddress, ex.getMessage());
           }

           throw new RateLimitExceededException(ipAddress);
       }
   }


}