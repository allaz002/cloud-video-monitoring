package com.example.cloudvideomonitoring.event;

import com.example.cloudvideomonitoring.common.EventType;
import com.example.cloudvideomonitoring.common.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateCameraEventRequest(
        @NotBlank String cameraId,
        @NotNull EventType type,
        @NotNull Severity severity,
        String message,
        String mediaRef,
        Instant occurredAt
) {
}
