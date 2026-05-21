import argparse
import time
from datetime import datetime, timezone

import requests


DEFAULT_BACKEND_URL = "http://localhost:8081"

CAMERAS = [
    {"id": "cam-001", "name": "Entrance Camera", "location": "Entrance"},
    {"id": "cam-002", "name": "Parking Camera", "location": "Parking Lot"},
    {"id": "cam-003", "name": "Warehouse Camera", "location": "Warehouse"},
]


def parse_args():
    parser = argparse.ArgumentParser(description="Send simulated camera events to the backend.")
    parser.add_argument("--backend-url", default=DEFAULT_BACKEND_URL)
    parser.add_argument("--iterations", type=int, default=5)
    parser.add_argument("--delay-seconds", type=float, default=2.0)
    args = parser.parse_args()

    if args.iterations < 1:
        parser.error("--iterations must be at least 1")
    if args.delay_seconds < 0:
        parser.error("--delay-seconds must be zero or greater")

    return args


def utc_now():
    return datetime.now(timezone.utc).isoformat().replace("+00:00", "Z")


def post_json(session, url, payload):
    try:
        return session.post(url, json=payload, timeout=5)
    except requests.RequestException as exc:
        print(f"request failed {url}: {exc}")
        return None


def ensure_cameras(session, backend_url):
    url = f"{backend_url}/api/cameras"
    for camera in CAMERAS:
        response = post_json(session, url, camera)
        if response is None:
            continue
        if response.status_code in (200, 201, 409):
            print(f"camera ready {camera['id']} status={response.status_code}")
        else:
            print(f"camera setup failed {camera['id']} status={response.status_code} body={response.text}")


def send_event(session, backend_url, camera_id, event_type, severity, message):
    payload = {
        "cameraId": camera_id,
        "type": event_type,
        "severity": severity,
        "message": message,
        "mediaRef": None,
        "occurredAt": utc_now(),
    }
    response = post_json(session, f"{backend_url}/api/events", payload)
    if response is None:
        return
    if 200 <= response.status_code < 300:
        print(f"event sent {camera_id} type={event_type} severity={severity}")
    else:
        print(f"event failed {camera_id} type={event_type} status={response.status_code} body={response.text}")


def run_simulation(backend_url, iterations, delay_seconds):
    backend_url = backend_url.rstrip("/")
    with requests.Session() as session:
        ensure_cameras(session, backend_url)

        for iteration in range(1, iterations + 1):
            print(f"iteration {iteration}/{iterations}")
            for camera in CAMERAS:
                send_event(session, backend_url, camera["id"], "HEARTBEAT", "INFO", "Camera heartbeat")

            if iteration % 3 == 0:
                send_event(session, backend_url, "cam-002", "MOTION", "INFO", "Motion detected")

            if iteration % 5 == 0:
                severity = "CRITICAL" if iteration % 10 == 0 else "WARNING"
                send_event(session, backend_url, "cam-003", "ALARM", severity, "Alarm detected")

            if iteration < iterations and delay_seconds > 0:
                time.sleep(delay_seconds)


def main():
    args = parse_args()
    run_simulation(args.backend_url, args.iterations, args.delay_seconds)


if __name__ == "__main__":
    main()
