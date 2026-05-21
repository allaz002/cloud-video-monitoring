export type CameraStatus = 'UNKNOWN' | 'ONLINE' | 'OFFLINE' | 'ALERT';

export type EventType = 'HEARTBEAT' | 'MOTION' | 'ALARM' | 'OFFLINE';

export type Severity = 'INFO' | 'WARNING' | 'CRITICAL';

export interface Camera {
  id: string;
  name: string;
  location: string | null;
  status: CameraStatus;
  lastSeenAt: string | null;
  createdAt: string;
}

export interface CameraEvent {
  id: number;
  cameraId: string;
  type: EventType;
  severity: Severity;
  message: string | null;
  mediaRef: string | null;
  occurredAt: string;
  receivedAt: string;
}
