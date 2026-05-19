package com.understandingjava.iws_app.Util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // omit null fields from JSON
public class ApiResponse<T> {

    private StatusBlock success;
    private StatusBlock error;
    private T           content;
    private long        timeStamp;

    // ── Inner block ───────────────────────────────────────────────────────────

    @Data
    @Builder
    public static class StatusBlock {
        private int    status;   // 1 = success, 0 = error
        private String message;
    }

    // ── Factory methods ───────────────────────────────────────────────────────

    public static <T> ApiResponse<T> success(String message, T content) {
        return ApiResponse.<T>builder()
                .success(StatusBlock.builder()
                        .status(1)
                        .message(message)
                        .build())
                .content(content)
                .timeStamp(Instant.now().toEpochMilli())
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .error(StatusBlock.builder()
                        .status(0)
                        .message(message)
                        .build())
                .timeStamp(Instant.now().toEpochMilli())
                .build();
    }}