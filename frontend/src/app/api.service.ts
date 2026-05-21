import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { Camera, CameraEvent } from './models';

const API_BASE_URL = 'http://localhost:8081/api';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  constructor(private readonly http: HttpClient) {}

  getCameras(): Observable<Camera[]> {
    return this.http.get<Camera[]>(`${API_BASE_URL}/cameras`);
  }

  getEvents(): Observable<CameraEvent[]> {
    return this.http.get<CameraEvent[]>(`${API_BASE_URL}/events`);
  }
}
