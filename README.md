# cloud-video-monitoring

`cloud-video-monitoring` is a camera monitoring application built with a Spring Boot backend, PostgreSQL, an Angular dashboard, and a Python camera simulator.

The system collects simulated camera events, persists camera and event metadata, derives camera status, and exposes the data through REST APIs for the dashboard.

## Architecture

- Spring Boot backend under `backend/`
- PostgreSQL via root `docker-compose.yml`
- Angular dashboard under `frontend/`
- Python simulator under `simulator/`

## Prerequisites

- Java 21
- Docker Desktop
- Python 3
- Git
- Node.js and npm
- Maven Wrapper included; no global Maven installation required

## Local Startup

Start PostgreSQL:

```powershell
docker compose up -d
```

Start the backend:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Check backend health:

```powershell
Invoke-RestMethod http://localhost:8081/actuator/health
```

Start the frontend in another terminal:

```powershell
cd frontend
npm.cmd start
```

Open the dashboard:

```text
http://localhost:4200
```

## Operations Checks

Health check:

```powershell
Invoke-RestMethod http://localhost:8081/actuator/health
```

Application info:

```powershell
Invoke-RestMethod http://localhost:8081/actuator/info
```

Metrics overview:

```powershell
Invoke-RestMethod http://localhost:8081/actuator/metrics
```

Only `health`, `info`, and `metrics` are exposed over HTTP for local checks. Sensitive actuator endpoints such as `env`, `beans`, `mappings`, `configprops`, `heapdump`, and `threaddump` are not exposed.

## API Examples

Create a camera:

```powershell
Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:8081/api/cameras `
  -ContentType "application/json" `
  -Body '{"id":"cam-001","name":"Entrance Camera","location":"Entrance"}'
```

List cameras:

```powershell
Invoke-RestMethod http://localhost:8081/api/cameras
```

Create an event:

```powershell
Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:8081/api/events `
  -ContentType "application/json" `
  -Body '{"cameraId":"cam-001","type":"HEARTBEAT","severity":"INFO","message":"Camera heartbeat"}'
```

List events:

```powershell
Invoke-RestMethod http://localhost:8081/api/events
```

Filter events by camera:

```powershell
Invoke-RestMethod "http://localhost:8081/api/events?cameraId=cam-001"
```

## Python Simulator

Create and activate a virtual environment:

```powershell
cd simulator
python -m venv .venv
.\.venv\Scripts\Activate.ps1
```

Install dependencies:

```powershell
python -m pip install -r requirements.txt
```

Run the simulator:

```powershell
python camera_simulator.py --iterations 5 --delay-seconds 2
```

`simulator/.venv` is local-only and must not be committed.

## Useful Ports

- Backend API: `8081`
- Angular frontend: `4200`
- PostgreSQL host port: `5433`
- PostgreSQL container port: `5432`
