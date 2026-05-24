# cloud-video-monitoring

`cloud-video-monitoring` is a camera monitoring application built with a Spring Boot backend, PostgreSQL, an Angular dashboard, and a Python camera simulator.

The system collects simulated camera events, persists camera and event metadata, derives camera status, and exposes the data through REST APIs for the dashboard.

## Features

- Camera and event metadata persistence with PostgreSQL.
- Camera status derivation from heartbeat, alarm, offline, and timeout logic.
- REST API for cameras and events.
- Angular dashboard for camera status and recent events.
- Python simulator for local camera event generation.
- Local operations checks through Spring Boot Actuator health, info, and metrics.
- GitHub Actions build verification for backend and frontend changes.

## Architecture

- Spring Boot backend under `backend/`
- PostgreSQL via root `docker-compose.yml`
- Angular dashboard under `frontend/`
- Python simulator under `simulator/`

## Project Structure

- `backend/`: Spring Boot REST API, persistence, status logic, and tests.
- `frontend/`: Angular dashboard.
- `simulator/`: Python camera event simulator.
- `docker-compose.yml`: local PostgreSQL runtime.
- `.github/workflows/`: GitHub Actions CI workflow.

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

Useful metric examples:

```powershell
Invoke-RestMethod http://localhost:8081/actuator/metrics/http.server.requests
Invoke-RestMethod http://localhost:8081/actuator/metrics/jvm.memory.used
Invoke-RestMethod http://localhost:8081/actuator/metrics/process.uptime
```

Backend logs are written to the backend console. Local logging keeps application and Spring Web output at `INFO`; Hibernate SQL logging is not enabled by default.

Only `health`, `info`, and `metrics` are exposed over HTTP for local checks. Sensitive actuator endpoints such as `env`, `beans`, `mappings`, `configprops`, `heapdump`, and `threaddump` are not exposed.

## Troubleshooting

- Backend unavailable: confirm the backend is running on port `8081` and check the backend console output.
- Database unavailable: start PostgreSQL with `docker compose up -d`, then check `docker ps`.
- Angular cannot reach backend: confirm the backend is running on `http://localhost:8081` and the frontend is running on `http://localhost:4200`.
- Simulator cannot send events: confirm the backend is running, cameras can be listed from `/api/cameras`, and the simulator `--backend-url` points to `http://localhost:8081`.

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

## CI

GitHub Actions runs build checks on push and pull request:

- Backend: Java 21 with Maven Wrapper, running build and tests from `backend/`.
- Frontend: Node.js with `npm ci` and Angular build from `frontend/`.

## Notes

- Local PostgreSQL credentials are for development use only.
- Actuator exposure is intentionally limited to `health`, `info`, and `metrics`.
- `simulator/.venv` and `frontend/node_modules` are local dependency folders and must not be committed.
