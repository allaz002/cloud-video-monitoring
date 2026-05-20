package com.example.cloudvideomonitoring.camera;

import jakarta.validation.constraints.NotBlank;

public record CreateCameraRequest(
        @NotBlank String id,
        @NotBlank String name,
        String location
) {
}
