package com.understandingjava.iws_app.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class EventLogDTO {

    private final LocalDateTime timestamp;
    private final String        service;
    private final String        message;
    private final String        detail1;
    private final String        detail2;
    private final String        detail3;
}
