package com.example.cloudvideomonitoring.camera;

import com.example.cloudvideomonitoring.common.CameraStatus;
import com.example.cloudvideomonitoring.event.CameraEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CameraServiceTests {

    private static final Duration TIMEOUT = Duration.ofMinutes(5);
    private static final Instant NOW = Instant.parse("2026-05-21T12:00:00Z");

    private final CameraService cameraService;
    private final CameraRepository cameraRepository;
    private final CameraEventRepository cameraEventRepository;

    @Autowired
    CameraServiceTests(
            CameraService cameraService,
            CameraRepository cameraRepository,
            CameraEventRepository cameraEventRepository) {
        this.cameraService = cameraService;
        this.cameraRepository = cameraRepository;
        this.cameraEventRepository = cameraEventRepository;
    }

    @BeforeEach
    void cleanDatabase() {
        cameraEventRepository.deleteAll();
        cameraRepository.deleteAll();
    }

    @Test
    void markStaleCamerasOffline_withOldOnlineCamera_setsOffline() {
        Camera camera = saveCamera("cam-old-online", CameraStatus.ONLINE, NOW.minus(TIMEOUT).minusSeconds(1));

        int updatedCount = cameraService.markStaleCamerasOffline(TIMEOUT, NOW);

        Camera reloaded = cameraRepository.findById(camera.getId()).orElseThrow();
        assertThat(updatedCount).isEqualTo(1);
        assertThat(reloaded.getStatus()).isEqualTo(CameraStatus.OFFLINE);
    }

    @Test
    void markStaleCamerasOffline_withRecentOnlineCamera_keepsOnline() {
        Camera camera = saveCamera("cam-recent-online", CameraStatus.ONLINE, NOW.minus(TIMEOUT).plusSeconds(1));

        int updatedCount = cameraService.markStaleCamerasOffline(TIMEOUT, NOW);

        Camera reloaded = cameraRepository.findById(camera.getId()).orElseThrow();
        assertThat(updatedCount).isZero();
        assertThat(reloaded.getStatus()).isEqualTo(CameraStatus.ONLINE);
    }

    @Test
    void markStaleCamerasOffline_withOfflineCamera_keepsOffline() {
        Camera camera = saveCamera("cam-offline", CameraStatus.OFFLINE, NOW.minus(TIMEOUT).minusSeconds(1));

        int updatedCount = cameraService.markStaleCamerasOffline(TIMEOUT, NOW);

        Camera reloaded = cameraRepository.findById(camera.getId()).orElseThrow();
        assertThat(updatedCount).isZero();
        assertThat(reloaded.getStatus()).isEqualTo(CameraStatus.OFFLINE);
    }

    @Test
    void markStaleCamerasOffline_withUnknownCameraAndNullLastSeenAt_keepsUnknown() {
        Camera camera = saveCamera("cam-unknown-null", CameraStatus.UNKNOWN, null);

        int updatedCount = cameraService.markStaleCamerasOffline(TIMEOUT, NOW);

        Camera reloaded = cameraRepository.findById(camera.getId()).orElseThrow();
        assertThat(updatedCount).isZero();
        assertThat(reloaded.getStatus()).isEqualTo(CameraStatus.UNKNOWN);
    }

    @Test
    void markStaleCamerasOffline_withOldAlertCamera_setsOffline() {
        Camera camera = saveCamera("cam-old-alert", CameraStatus.ALERT, NOW.minus(TIMEOUT).minusSeconds(1));

        int updatedCount = cameraService.markStaleCamerasOffline(TIMEOUT, NOW);

        Camera reloaded = cameraRepository.findById(camera.getId()).orElseThrow();
        assertThat(updatedCount).isEqualTo(1);
        assertThat(reloaded.getStatus()).isEqualTo(CameraStatus.OFFLINE);
    }

    private Camera saveCamera(String id, CameraStatus status, Instant lastSeenAt) {
        Camera camera = new Camera(id, "Test camera " + id, "Test location");
        camera.setStatus(status);
        camera.setLastSeenAt(lastSeenAt);
        return cameraRepository.save(camera);
    }
}
