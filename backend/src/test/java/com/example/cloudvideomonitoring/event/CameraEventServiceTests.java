package com.example.cloudvideomonitoring.event;

import com.example.cloudvideomonitoring.camera.Camera;
import com.example.cloudvideomonitoring.camera.CameraRepository;
import com.example.cloudvideomonitoring.common.CameraStatus;
import com.example.cloudvideomonitoring.common.EventType;
import com.example.cloudvideomonitoring.common.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CameraEventServiceTests {

    private final CameraEventService cameraEventService;
    private final CameraRepository cameraRepository;
    private final CameraEventRepository cameraEventRepository;

    @Autowired
    CameraEventServiceTests(
            CameraEventService cameraEventService,
            CameraRepository cameraRepository,
            CameraEventRepository cameraEventRepository) {
        this.cameraEventService = cameraEventService;
        this.cameraRepository = cameraRepository;
        this.cameraEventRepository = cameraEventRepository;
    }

    @BeforeEach
    void cleanDatabase() {
        cameraEventRepository.deleteAll();
        cameraRepository.deleteAll();
    }

    @Test
    void createEvent_withHeartbeat_setsCameraOnlineAndLastSeenAt() {
        Camera camera = saveCamera("cam-heartbeat", CameraStatus.UNKNOWN);
        Instant occurredAt = Instant.parse("2026-05-21T10:15:30Z");

        cameraEventService.createEvent(camera.getId(), EventType.HEARTBEAT, Severity.INFO, null, null, occurredAt);

        Camera reloaded = cameraRepository.findById(camera.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(CameraStatus.ONLINE);
        assertThat(reloaded.getLastSeenAt()).isEqualTo(occurredAt);
    }

    @Test
    void createEvent_withCriticalAlarm_setsCameraAlert() {
        Camera camera = saveCamera("cam-critical-alarm", CameraStatus.UNKNOWN);

        cameraEventService.createEvent(camera.getId(), EventType.ALARM, Severity.CRITICAL, null, null, Instant.now());

        Camera reloaded = cameraRepository.findById(camera.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(CameraStatus.ALERT);
    }

    @Test
    void createEvent_withOffline_setsCameraOffline() {
        Camera camera = saveCamera("cam-offline", CameraStatus.ONLINE);

        cameraEventService.createEvent(camera.getId(), EventType.OFFLINE, Severity.INFO, null, null, Instant.now());

        Camera reloaded = cameraRepository.findById(camera.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(CameraStatus.OFFLINE);
    }

    @Test
    void createEvent_withMotion_doesNotChangeCameraStatus() {
        Camera camera = saveCamera("cam-motion", CameraStatus.ONLINE);

        cameraEventService.createEvent(camera.getId(), EventType.MOTION, Severity.INFO, null, null, Instant.now());

        Camera reloaded = cameraRepository.findById(camera.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(CameraStatus.ONLINE);
    }

    @Test
    void createEvent_withWarningAlarm_doesNotChangeCameraStatus() {
        Camera camera = saveCamera("cam-warning-alarm", CameraStatus.ONLINE);

        cameraEventService.createEvent(camera.getId(), EventType.ALARM, Severity.WARNING, null, null, Instant.now());

        Camera reloaded = cameraRepository.findById(camera.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(CameraStatus.ONLINE);
    }

    private Camera saveCamera(String id, CameraStatus status) {
        Camera camera = new Camera(id, "Test camera " + id, "Test location");
        camera.setStatus(status);
        return cameraRepository.save(camera);
    }
}
