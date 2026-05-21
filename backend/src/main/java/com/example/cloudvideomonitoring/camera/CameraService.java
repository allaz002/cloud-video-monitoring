package com.example.cloudvideomonitoring.camera;

import com.example.cloudvideomonitoring.common.CameraStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class CameraService {

    private final CameraRepository cameraRepository;

    public CameraService(CameraRepository cameraRepository) {
        this.cameraRepository = cameraRepository;
    }

    @Transactional
    public Camera createCamera(Camera camera) {
        return cameraRepository.save(camera);
    }

    @Transactional(readOnly = true)
    public List<Camera> getAllCameras() {
        return cameraRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Camera getCameraById(String id) {
        return cameraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Camera not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Camera> getCamerasByStatus(CameraStatus status) {
        return cameraRepository.findByStatus(status);
    }

    @Transactional
    public int markStaleCamerasOffline(Duration timeout, Instant now) {
        Instant cutoff = now.minus(timeout);
        List<Camera> staleCameras = cameraRepository.findByStatusNotAndLastSeenAtBefore(CameraStatus.OFFLINE, cutoff);
        staleCameras.forEach(camera -> camera.setStatus(CameraStatus.OFFLINE));
        return staleCameras.size();
    }
}
