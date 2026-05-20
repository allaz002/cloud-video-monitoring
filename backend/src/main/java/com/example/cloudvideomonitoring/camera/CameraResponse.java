package com.example.cloudvideomonitoring.camera;

import com.example.cloudvideomonitoring.common.CameraStatus;

import java.time.Instant;

public record CameraResponse(
        String id,
        String name,
        String location,
        CameraStatus status,
        Instant lastSeenAt,
        Instant createdAt
) {

    public static CameraResponse from(Camera camera) {
        return new CameraResponse(
                camera.getId(),
                camera.getName(),
                camera.getLocation(),
                camera.getStatus(),
                camera.getLastSeenAt(),
                camera.getCreatedAt()
        );
    }
}
