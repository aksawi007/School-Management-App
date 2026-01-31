import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RoutineTimeSlot, RoutineTimeSlotRequest } from '../models/routine/routine-time-slot.model';

@Injectable({
  providedIn: 'root'
})
export class RoutineTimeSlotService {
  constructor(private http: HttpClient) {}

  createTimeSlot(schoolId: number, request: RoutineTimeSlotRequest): Observable<RoutineTimeSlot> {
    return this.http.post<RoutineTimeSlot>(`/api/schools/${schoolId}/routine/time-slots`, request);
  }

  updateTimeSlot(schoolId: number, slotId: number, request: RoutineTimeSlotRequest): Observable<RoutineTimeSlot> {
    return this.http.put<RoutineTimeSlot>(`/api/schools/${schoolId}/routine/time-slots/${slotId}`, request);
  }

  deleteTimeSlot(schoolId: number, slotId: number): Observable<any> {
    return this.http.delete(`/api/schools/${schoolId}/routine/time-slots/${slotId}`);
  }

  getActiveTimeSlots(schoolId: number): Observable<RoutineTimeSlot[]> {
    return this.http.get<RoutineTimeSlot[]>(`/api/schools/${schoolId}/routine/time-slots`);
  }

  getTimeSlotsByType(schoolId: number, slotType: string): Observable<RoutineTimeSlot[]> {
    return this.http.get<RoutineTimeSlot[]>(`/api/schools/${schoolId}/routine/time-slots/type/${slotType}`);
  }

  getTimeSlotById(schoolId: number, slotId: number): Observable<RoutineTimeSlot> {
    return this.http.get<RoutineTimeSlot>(`/api/schools/${schoolId}/routine/time-slots/${slotId}`);
  }
}
