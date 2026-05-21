import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { finalize, forkJoin } from 'rxjs';

import { ApiService } from './api.service';
import { Camera, CameraEvent, CameraStatus, Severity } from './models';

@Component({
  selector: 'app-root',
  imports: [CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  cameras: Camera[] = [];
  events: CameraEvent[] = [];
  isLoading = true;
  errorMessage = '';

  constructor(
    private readonly apiService: ApiService,
    private readonly changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  get totalCameras(): number {
    return this.cameras.length;
  }

  get onlineCameras(): number {
    return this.countByStatus('ONLINE');
  }

  get alertCameras(): number {
    return this.countByStatus('ALERT');
  }

  get offlineCameras(): number {
    return this.countByStatus('OFFLINE');
  }

  get unknownCameras(): number {
    return this.countByStatus('UNKNOWN');
  }

  get recentEvents(): CameraEvent[] {
    return [...this.events]
      .sort((left, right) => Date.parse(right.occurredAt) - Date.parse(left.occurredAt))
      .slice(0, 10);
  }

  statusClass(status: CameraStatus): string {
    return `status status-${status.toLowerCase()}`;
  }

  severityClass(severity: Severity): string {
    return `severity severity-${severity.toLowerCase()}`;
  }

  private loadDashboard(): void {
    this.isLoading = true;
    this.errorMessage = '';

    forkJoin({
      cameras: this.apiService.getCameras(),
      events: this.apiService.getEvents(),
    })
      .pipe(finalize(() => {
        this.isLoading = false;
        this.changeDetectorRef.detectChanges();
      }))
      .subscribe({
        next: ({ cameras, events }) => {
          this.cameras = cameras;
          this.events = events;
        },
        error: () => {
          this.errorMessage = 'Backend unavailable';
        },
      });
  }

  private countByStatus(status: CameraStatus): number {
    return this.cameras.filter((camera) => camera.status === status).length;
  }
}
