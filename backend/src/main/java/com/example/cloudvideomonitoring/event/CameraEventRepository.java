package com.example.cloudvideomonitoring.event;

import com.example.cloudvideomonitoring.common.EventType;
import com.example.cloudvideomonitoring.common.Severity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CameraEventRepository extends JpaRepository<CameraEvent, Long> {

    List<CameraEvent> findByCamera_Id(String cameraId);

    List<CameraEvent> findBySeverity(Severity severity);

    List<CameraEvent> findByType(EventType type);
}
