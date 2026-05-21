package com.example.cloudvideomonitoring.event;

import com.example.cloudvideomonitoring.camera.Camera;
import com.example.cloudvideomonitoring.camera.CameraRepository;
import com.example.cloudvideomonitoring.common.CameraStatus;
import com.example.cloudvideomonitoring.common.EventType;
import com.example.cloudvideomonitoring.common.Severity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class CameraEventService {

    private final CameraEventRepository cameraEventRepository;
    private final CameraRepository cameraRepository;

    public CameraEventService(CameraEventRepository cameraEventRepository, CameraRepository cameraRepository) {
        this.cameraEventRepository = cameraEventRepository;
        this.cameraRepository = cameraRepository;
    }

    @Transactional
    public CameraEvent createEvent(String cameraId, EventType type, Severity severity, String message, String mediaRef, Instant occurredAt) {
        Camera camera = cameraRepository.findById(cameraId)
                .orElseThrow(() -> new IllegalArgumentException("Camera not found: " + cameraId));
        CameraEvent event = new CameraEvent(camera, type, severity, message, mediaRef, occurredAt);
        applyCameraStatusUpdate(camera, type, severity, occurredAt);
        return cameraEventRepository.save(event);
    }

    private void applyCameraStatusUpdate(Camera camera, EventType type, Severity severity, Instant occurredAt) {
        if (type == EventType.HEARTBEAT) {
            camera.setStatus(CameraStatus.ONLINE);
            camera.setLastSeenAt(occurredAt);
        } else if (type == EventType.OFFLINE) {
            camera.setStatus(CameraStatus.OFFLINE);
        } else if (type == EventType.ALARM && severity == Severity.CRITICAL) {
            camera.setStatus(CameraStatus.ALERT);
        }
    }

    @Transactional(readOnly = true)
    public List<CameraEvent> getAllEvents() {
        return cameraEventRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<CameraEvent> getEventsByCameraId(String cameraId) {
        return cameraEventRepository.findByCamera_Id(cameraId);
    }

    @Transactional(readOnly = true)
    public List<CameraEvent> getEventsBySeverity(Severity severity) {
        return cameraEventRepository.findBySeverity(severity);
    }

    @Transactional(readOnly = true)
    public List<CameraEvent> getEventsByType(EventType type) {
        return cameraEventRepository.findByType(type);
    }
}
