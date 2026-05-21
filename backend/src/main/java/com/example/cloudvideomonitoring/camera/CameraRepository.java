package com.example.cloudvideomonitoring.camera;

import com.example.cloudvideomonitoring.common.CameraStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface CameraRepository extends JpaRepository<Camera, String> {

    List<Camera> findByStatus(CameraStatus status);

    List<Camera> findByStatusNotAndLastSeenAtBefore(CameraStatus status, Instant lastSeenAt);
}
