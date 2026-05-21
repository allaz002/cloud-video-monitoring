# Camera Simulator

Simple Python simulator for the local cloud video monitoring backend.

## Setup

```powershell
cd simulator
python -m pip install -r requirements.txt
```

## Run

Start the backend first:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Then run the simulator from another terminal:

```powershell
cd simulator
python camera_simulator.py
```

With explicit options:

```powershell
python camera_simulator.py --backend-url http://localhost:8081 --iterations 5 --delay-seconds 2
```

## Behavior

- Ensures `cam-001`, `cam-002`, and `cam-003` exist through `POST /api/cameras`.
- Sends `HEARTBEAT` events for all cameras in every iteration.
- Sends occasional `MOTION` events for `cam-002`.
- Sends occasional `ALARM` events for `cam-003`.
- Prints concise logs for camera setup and event delivery.
