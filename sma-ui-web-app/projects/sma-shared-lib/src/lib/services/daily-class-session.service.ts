import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DailyClassSession, DailyClassSessionRequest } from '../models/routine/daily-class-session.model';

@Injectable({
  providedIn: 'root'
})
export class DailyClassSessionService {
  constructor(private http: HttpClient) {}

  createOrUpdateSession(schoolId: number, request: DailyClassSessionRequest): Observable<DailyClassSession> {
    return this.http.post<DailyClassSession>(`/api/schools/${schoolId}/routine/sessions`, request);
  }

  getCompleteSchedule(schoolId: number, academicYearId: number, classId: number, sectionId: number, date: string): Observable<any[]> {
    const params = new HttpParams()
      .set('academicYearId', academicYearId.toString())
      .set('classId', classId.toString())
      .set('sectionId', sectionId.toString())
      .set('date', date);
    return this.http.get<any[]>(`/api/schools/${schoolId}/routine/sessions/complete-schedule`, { params });
  }

  getTeacherSchedule(schoolId: number, academicYearId: number, teacherId: number, date: string): Observable<DailyClassSession[]> {
    const params = new HttpParams()
      .set('academicYearId', academicYearId.toString())
      .set('teacherId', teacherId.toString())
      .set('date', date);
    return this.http.get<DailyClassSession[]>(`/api/schools/${schoolId}/routine/sessions/teacher-schedule`, { params });
  }

  getSessionsInDateRange(schoolId: number, academicYearId: number, classId: number, sectionId: number, startDate: string, endDate: string): Observable<DailyClassSession[]> {
    const params = new HttpParams()
      .set('academicYearId', academicYearId.toString())
      .set('classId', classId.toString())
      .set('sectionId', sectionId.toString())
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get<DailyClassSession[]>(`/api/schools/${schoolId}/routine/sessions/date-range`, { params });
  }

  updateSessionStatus(schoolId: number, sessionId: number, status: string): Observable<DailyClassSession> {
    return this.http.patch<DailyClassSession>(`/api/schools/${schoolId}/routine/sessions/${sessionId}/status`, { status });
  }

  /**
   * Create or update session status
   * If sessionId is null, creates session from routine master first
   */
  createOrUpdateSessionStatus(schoolId: number, sessionId: number | null, routineMasterId: number, sessionDate: string, status: string): Observable<DailyClassSession> {
    return this.http.post<DailyClassSession>(`/api/schools/${schoolId}/routine/sessions/create-and-update-status`, {
      sessionId,
      routineMasterId,
      sessionDate,
      status
    });
  }

  /**
   * Create session from routine master for attendance marking
   */
  createSessionFromRoutine(schoolId: number, routineMasterId: number, sessionDate: string): Observable<DailyClassSession> {
    return this.http.post<DailyClassSession>(`/api/schools/${schoolId}/routine/sessions/create-from-routine`, {
      routineMasterId,
      sessionDate
    });
  }

  getSessionById(schoolId: number, sessionId: number): Observable<DailyClassSession> {
    return this.http.get<DailyClassSession>(`/api/schools/${schoolId}/routine/sessions/${sessionId}`);
  }
}
