package com.example.cloudvideomonitoring.event;

import com.example.cloudvideomonitoring.common.EventType;
import com.example.cloudvideomonitoring.common.Severity;

import java.time.Instant;

public record CameraEventResponse(
        Long id,
        String cameraId,
        EventType type,
        Severity severity,
        String message,
        String mediaRef,
        Instant occurredAt,
        Instant receivedAt
) {

    public static CameraEventResponse from(CameraEvent event) {
        return new CameraEventResponse(
                event.getId(),
                event.getCamera().getId(),
                event.getType(),
                event.getSeverity(),
                event.getMessage(),
                event.getMediaRef(),
                event.getOccurredAt(),
                event.getReceivedAt()
        );
    }
}
