import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StudentAttendance, BulkAttendanceRequest, AttendanceSummary } from '../models/routine/student-attendance.model';

@Injectable({
  providedIn: 'root'
})
export class StudentAttendanceService {
  constructor(private http: HttpClient) {}

  markBulkAttendance(schoolId: number, request: BulkAttendanceRequest): Observable<StudentAttendance[]> {
    return this.http.post<StudentAttendance[]>(`/api/schools/${schoolId}/attendance/bulk`, request);
  }

  getSessionAttendance(schoolId: number, sessionId: number): Observable<StudentAttendance[]> {
    return this.http.get<StudentAttendance[]>(`/api/schools/${schoolId}/attendance/session/${sessionId}`);
  }

  getStudentAttendanceHistory(schoolId: number, studentId: number, startDate: string, endDate: string): Observable<StudentAttendance[]> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get<StudentAttendance[]>(`/api/schools/${schoolId}/attendance/student/${studentId}/history`, { params });
  }

  getClassAttendanceForDate(schoolId: number, academicYearId: number, classId: number, sectionId: number, date: string): Observable<StudentAttendance[]> {
    const params = new HttpParams()
      .set('academicYearId', academicYearId.toString())
      .set('classId', classId.toString())
      .set('sectionId', sectionId.toString())
      .set('date', date);
    return this.http.get<StudentAttendance[]>(`/api/schools/${schoolId}/attendance/class/date`, { params });
  }

  getStudentAttendanceCount(schoolId: number, studentId: number, startDate: string, endDate: string, status: string): Observable<{ count: number }> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate)
      .set('status', status);
    return this.http.get<{ count: number }>(`/api/schools/${schoolId}/attendance/student/${studentId}/count`, { params });
  }

  getAttendanceSummary(schoolId: number, academicYearId: number, classId: number, sectionId: number, startDate: string, endDate: string): Observable<any[]> {
    const params = new HttpParams()
      .set('academicYearId', academicYearId.toString())
      .set('classId', classId.toString())
      .set('sectionId', sectionId.toString())
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get<any[]>(`/api/schools/${schoolId}/attendance/class/summary`, { params });
  }

  updateAttendance(schoolId: number, attendanceId: number, status: string, remarks?: string): Observable<StudentAttendance> {
    return this.http.patch<StudentAttendance>(`/api/schools/${schoolId}/attendance/${attendanceId}`, { status, remarks });
  }

  getAttendanceById(schoolId: number, attendanceId: number): Observable<StudentAttendance> {
    return this.http.get<StudentAttendance>(`/api/schools/${schoolId}/attendance/${attendanceId}`);
  }
}
