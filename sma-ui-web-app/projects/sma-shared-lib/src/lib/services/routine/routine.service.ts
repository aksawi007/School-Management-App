import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  RoutineTimeSlot,
  RoutineTimeSlotRequest,
  ClassRoutineMaster,
  ClassRoutineMasterRequest,
  DailyClassSession,
  DailyClassSessionRequest,
  StudentAttendance,
  BulkAttendanceRequest
} from '../../models/routine';

@Injectable({
  providedIn: 'root'
})
export class RoutineTimeSlotService {
  constructor(private http: HttpClient) {}

  getBaseUrl(schoolId: number): string {
    return `/api/schools/${schoolId}/routine/time-slots`;
  }

  createTimeSlot(schoolId: number, request: RoutineTimeSlotRequest): Observable<RoutineTimeSlot> {
    return this.http.post<RoutineTimeSlot>(this.getBaseUrl(schoolId), request);
  }

  updateTimeSlot(schoolId: number, slotId: number, request: RoutineTimeSlotRequest): Observable<RoutineTimeSlot> {
    return this.http.put<RoutineTimeSlot>(`${this.getBaseUrl(schoolId)}/${slotId}`, request);
  }

  deleteTimeSlot(schoolId: number, slotId: number): Observable<any> {
    return this.http.delete(`${this.getBaseUrl(schoolId)}/${slotId}`);
  }

  getActiveTimeSlots(schoolId: number): Observable<RoutineTimeSlot[]> {
    return this.http.get<RoutineTimeSlot[]>(this.getBaseUrl(schoolId));
  }

  getTimeSlotsByType(schoolId: number, slotType: string): Observable<RoutineTimeSlot[]> {
    return this.http.get<RoutineTimeSlot[]>(`${this.getBaseUrl(schoolId)}/type/${slotType}`);
  }

  getTimeSlotById(schoolId: number, slotId: number): Observable<RoutineTimeSlot> {
    return this.http.get<RoutineTimeSlot>(`${this.getBaseUrl(schoolId)}/${slotId}`);
  }
}

@Injectable({
  providedIn: 'root'
})
export class ClassRoutineMasterService {
  constructor(private http: HttpClient) {}

  getBaseUrl(schoolId: number): string {
    return `/api/schools/${schoolId}/routine/master`;
  }

  createOrUpdateRoutine(schoolId: number, request: ClassRoutineMasterRequest): Observable<ClassRoutineMaster> {
    return this.http.post<ClassRoutineMaster>(this.getBaseUrl(schoolId), request);
  }

  getWeeklyRoutine(schoolId: number, academicYearId: number, classId: number, sectionId: number): Observable<ClassRoutineMaster[]> {
    const params = new HttpParams()
      .set('academicYearId', academicYearId.toString())
      .set('classId', classId.toString())
      .set('sectionId', sectionId.toString());
    return this.http.get<ClassRoutineMaster[]>(`${this.getBaseUrl(schoolId)}/weekly`, { params });
  }

  getDailyRoutine(schoolId: number, academicYearId: number, classId: number, sectionId: number, dayOfWeek: string): Observable<ClassRoutineMaster[]> {
    const params = new HttpParams()
      .set('academicYearId', academicYearId.toString())
      .set('classId', classId.toString())
      .set('sectionId', sectionId.toString())
      .set('dayOfWeek', dayOfWeek);
    return this.http.get<ClassRoutineMaster[]>(`${this.getBaseUrl(schoolId)}/daily`, { params });
  }

  getTeacherRoutine(schoolId: number, academicYearId: number, teacherId: number, dayOfWeek: string): Observable<ClassRoutineMaster[]> {
    const params = new HttpParams()
      .set('academicYearId', academicYearId.toString())
      .set('teacherId', teacherId.toString())
      .set('dayOfWeek', dayOfWeek);
    return this.http.get<ClassRoutineMaster[]>(`${this.getBaseUrl(schoolId)}/teacher`, { params });
  }

  deleteRoutineEntry(schoolId: number, routineId: number): Observable<any> {
    return this.http.delete(`${this.getBaseUrl(schoolId)}/${routineId}`);
  }

  getRoutineById(schoolId: number, routineId: number): Observable<ClassRoutineMaster> {
    return this.http.get<ClassRoutineMaster>(`${this.getBaseUrl(schoolId)}/${routineId}`);
  }

  checkTeacherAvailability(
    schoolId: number,
    teacherId: number,
    timeSlotId: number,
    academicYearId: number,
    classId: number,
    sectionId: number,
    dayOfWeek?: string
  ): Observable<{ available: boolean; conflictingRoutines: ClassRoutineMaster[] }> {
    let params = new HttpParams()
      .set('teacherId', teacherId.toString())
      .set('timeSlotId', timeSlotId.toString())
      .set('academicYearId', academicYearId.toString())
      .set('classId', classId.toString())
      .set('sectionId', sectionId.toString());
    
    if (dayOfWeek) {
      params = params.set('dayOfWeek', dayOfWeek);
    }
    
    return this.http.get<{ available: boolean; conflictingRoutines: ClassRoutineMaster[] }>(
      `${this.getBaseUrl(schoolId)}/check-availability`,
      { params }
    );
  }

  getAvailableTeachers(
    schoolId: number,
    timeSlotId: number,
    academicYearId: number,
    dayOfWeek?: string
  ): Observable<any[]> {
    let params = new HttpParams()
      .set('timeSlotId', timeSlotId.toString())
      .set('academicYearId', academicYearId.toString());

    if (dayOfWeek) {
      params = params.set('dayOfWeek', dayOfWeek);
    }

    return this.http.get<any[]>(
      `${this.getBaseUrl(schoolId)}/available-teachers`,
      { params }
    );
  }

  /**
   * Get available teachers and filter by staff type on client-side
   * This analyzes the response and returns only teachers matching the specified type
   */
  getAvailableTeachersByType(
    schoolId: number,
    timeSlotId: number,
    academicYearId: number,
    staffType: string,
    dayOfWeek?: string
  ): Observable<any[]> {
    return new Observable(observer => {
      this.getAvailableTeachers(schoolId, timeSlotId, academicYearId, dayOfWeek)
        .subscribe({
          next: (teachers) => {
            // Client-side filtering based on staffType
            const filteredTeachers = teachers.filter(teacher => 
              teacher.staffType && teacher.staffType.toUpperCase() === staffType.toUpperCase()
            );
            observer.next(filteredTeachers);
            observer.complete();
          },
          error: (error) => {
            observer.error(error);
          }
        });
    });
  }
}

@Injectable({
  providedIn: 'root'
})
export class DailyClassSessionService {
  constructor(private http: HttpClient) {}

  getBaseUrl(schoolId: number): string {
    return `/api/schools/${schoolId}/routine/sessions`;
  }

  createOrUpdateSession(schoolId: number, request: DailyClassSessionRequest): Observable<DailyClassSession> {
    return this.http.post<DailyClassSession>(this.getBaseUrl(schoolId), request);
  }

  getCompleteSchedule(schoolId: number, academicYearId: number, classId: number, sectionId: number, date: string): Observable<any[]> {
    const params = new HttpParams()
      .set('academicYearId', academicYearId.toString())
      .set('classId', classId.toString())
      .set('sectionId', sectionId.toString())
      .set('date', date);
    return this.http.get<any[]>(`${this.getBaseUrl(schoolId)}/complete-schedule`, { params });
  }

  getTeacherSchedule(schoolId: number, academicYearId: number, teacherId: number, date: string): Observable<DailyClassSession[]> {
    const params = new HttpParams()
      .set('academicYearId', academicYearId.toString())
      .set('teacherId', teacherId.toString())
      .set('date', date);
    return this.http.get<DailyClassSession[]>(`${this.getBaseUrl(schoolId)}/teacher-schedule`, { params });
  }

  getSessionsInDateRange(schoolId: number, academicYearId: number, classId: number, sectionId: number, startDate: string, endDate: string): Observable<DailyClassSession[]> {
    const params = new HttpParams()
      .set('academicYearId', academicYearId.toString())
      .set('classId', classId.toString())
      .set('sectionId', sectionId.toString())
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get<DailyClassSession[]>(`${this.getBaseUrl(schoolId)}/date-range`, { params });
  }

  updateSessionStatus(schoolId: number, sessionId: number, status: string): Observable<DailyClassSession> {
    return this.http.patch<DailyClassSession>(`${this.getBaseUrl(schoolId)}/${sessionId}/status`, { status });
  }

  getSessionById(schoolId: number, sessionId: number): Observable<DailyClassSession> {
    return this.http.get<DailyClassSession>(`${this.getBaseUrl(schoolId)}/${sessionId}`);
  }
}

@Injectable({
  providedIn: 'root'
})
export class StudentAttendanceService {
  constructor(private http: HttpClient) {}

  getBaseUrl(schoolId: number): string {
    return `/api/schools/${schoolId}/attendance`;
  }

  markBulkAttendance(schoolId: number, request: BulkAttendanceRequest): Observable<StudentAttendance[]> {
    return this.http.post<StudentAttendance[]>(`${this.getBaseUrl(schoolId)}/bulk`, request);
  }

  getSessionAttendance(schoolId: number, sessionId: number): Observable<StudentAttendance[]> {
    return this.http.get<StudentAttendance[]>(`${this.getBaseUrl(schoolId)}/session/${sessionId}`);
  }

  getStudentAttendanceHistory(schoolId: number, studentId: number, startDate: string, endDate: string): Observable<StudentAttendance[]> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get<StudentAttendance[]>(`${this.getBaseUrl(schoolId)}/student/${studentId}/history`, { params });
  }

  getClassAttendanceForDate(schoolId: number, academicYearId: number, classId: number, sectionId: number, date: string): Observable<StudentAttendance[]> {
    const params = new HttpParams()
      .set('academicYearId', academicYearId.toString())
      .set('classId', classId.toString())
      .set('sectionId', sectionId.toString())
      .set('date', date);
    return this.http.get<StudentAttendance[]>(`${this.getBaseUrl(schoolId)}/class/date`, { params });
  }

  getStudentAttendanceCount(schoolId: number, studentId: number, startDate: string, endDate: string, status: string): Observable<{count: number}> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate)
      .set('status', status);
    return this.http.get<{count: number}>(`${this.getBaseUrl(schoolId)}/student/${studentId}/count`, { params });
  }

  getAttendanceSummary(schoolId: number, academicYearId: number, classId: number, sectionId: number, startDate: string, endDate: string): Observable<any[]> {
    const params = new HttpParams()
      .set('academicYearId', academicYearId.toString())
      .set('classId', classId.toString())
      .set('sectionId', sectionId.toString())
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get<any[]>(`${this.getBaseUrl(schoolId)}/class/summary`, { params });
  }

  updateAttendance(schoolId: number, attendanceId: number, status: string, remarks?: string): Observable<StudentAttendance> {
    return this.http.patch<StudentAttendance>(`${this.getBaseUrl(schoolId)}/${attendanceId}`, { status, remarks });
  }

  getAttendanceById(schoolId: number, attendanceId: number): Observable<StudentAttendance> {
    return this.http.get<StudentAttendance>(`${this.getBaseUrl(schoolId)}/${attendanceId}`);
  }
}
