package com.example.cloudvideomonitoring.camera;

import com.example.cloudvideomonitoring.common.CameraStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/cameras")
public class CameraController {

    private final CameraService cameraService;

    public CameraController(CameraService cameraService) {
        this.cameraService = cameraService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CameraResponse createCamera(@Valid @RequestBody CreateCameraRequest request) {
        Camera camera = new Camera(request.id(), request.name(), request.location());
        return CameraResponse.from(cameraService.createCamera(camera));
    }

    @GetMapping
    public List<CameraResponse> getCameras(@RequestParam(required = false) CameraStatus status) {
        List<Camera> cameras = status == null
                ? cameraService.getAllCameras()
                : cameraService.getCamerasByStatus(status);
        return cameras.stream()
                .map(CameraResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public CameraResponse getCameraById(@PathVariable String id) {
        try {
            return CameraResponse.from(cameraService.getCameraById(id));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
        }
    }
}
