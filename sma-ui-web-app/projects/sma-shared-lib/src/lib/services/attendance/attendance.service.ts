import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DeviceWebhookRequest {
  deviceId?: string;
  deviceTxnId?: string;
  timestamp?: string; // ISO
  items?: Array<{ targetType: 'STUDENT' | 'STAFF', targetId: number, sessionId?: number, attendanceStatus: string, remarks?: string }>;
}

@Injectable({ providedIn: 'root' })
export class AttendanceService {
  constructor(private http: HttpClient) {}

  // Minimal placeholder: forwards device payload to backend webhook endpoint
  postDeviceWebhook(schoolId: number, payload: DeviceWebhookRequest): Observable<any> {
    return this.http.post(`/api/schools/${schoolId}/attendance/device-webhook`, payload);
  }
}
