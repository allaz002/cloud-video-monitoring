package com.example.cloudvideomonitoring.event;

import com.example.cloudvideomonitoring.common.EventType;
import com.example.cloudvideomonitoring.common.Severity;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class CameraEventController {

    private final CameraEventService cameraEventService;

    public CameraEventController(CameraEventService cameraEventService) {
        this.cameraEventService = cameraEventService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CameraEventResponse createEvent(@Valid @RequestBody CreateCameraEventRequest request) {
        Instant occurredAt = request.occurredAt() == null ? Instant.now() : request.occurredAt();
        try {
            CameraEvent event = cameraEventService.createEvent(
                    request.cameraId(),
                    request.type(),
                    request.severity(),
                    request.message(),
                    request.mediaRef(),
                    occurredAt
            );
            return CameraEventResponse.from(event);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
        }
    }

    @GetMapping
    public List<CameraEventResponse> getEvents(
            @RequestParam(required = false) String cameraId,
            @RequestParam(required = false) Severity severity,
            @RequestParam(required = false) EventType type
    ) {
        List<CameraEvent> events;
        if (cameraId != null) {
            events = cameraEventService.getEventsByCameraId(cameraId);
        } else if (severity != null) {
            events = cameraEventService.getEventsBySeverity(severity);
        } else if (type != null) {
            events = cameraEventService.getEventsByType(type);
        } else {
            events = cameraEventService.getAllEvents();
        }
        return events.stream()
                .map(CameraEventResponse::from)
                .toList();
    }
}
