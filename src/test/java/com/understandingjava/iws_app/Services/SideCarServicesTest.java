package com.understandingjava.iws_app.Services;

import com.understandingjava.iws_app.Repos.ITransactionRepo;
import com.understandingjava.iws_app.Repos.IpAddressRepo;
import com.understandingjava.iws_app.Repos.JdbcSideCarRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("SideCarServices UnitTest")
class SideCarServicesTest {

    private  RiskCalculationService risk;
    private  IpAddressRepo repo;
    private  JdbcSideCarRepo jdbcRepo;
    //private final RateLimiterService     rateLimiter; // ← NEW
    private  EventLogService log;         // ← renamed from LogService
    private  ITransactionRepo transactionRepo;

    private  SideCarServices  sideCarService;
}