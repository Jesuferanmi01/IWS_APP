package com.understandingjava.iws_app.DTOs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
public record UserResponseDTO(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String userCode,
        String status,
        String userType,
        String ipAddress
) {}