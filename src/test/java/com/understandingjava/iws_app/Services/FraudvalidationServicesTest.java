package com.understandingjava.iws_app.Services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("FraudvalidationServices UnitTest")
class FraudvalidationServicesTest {

    @Mock
    private  EventLogService log;

    @InjectMocks
    private FraudvalidationServices fraudvalidationService;

}