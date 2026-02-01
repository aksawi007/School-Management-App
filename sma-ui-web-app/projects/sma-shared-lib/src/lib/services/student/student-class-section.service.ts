import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StudentClassSectionRequest, StudentClassSectionResponse } from '../../models/student/student-class-section.model';

/**
 * Student Class Section Service
 * Manages student assignments to classes and sections
 */
@Injectable({
  providedIn: 'root'
})
export class StudentClassSectionService {
  private readonly API_BASE = '/api/student-class-section';

  constructor(private http: HttpClient) {}

  /**
   * Assign student to a class and section
   */
  assignStudentToClassSection(request: StudentClassSectionRequest): Observable<StudentClassSectionResponse> {
    return this.http.post<StudentClassSectionResponse>(`${this.API_BASE}/assign`, request);
  }

  /**
   * Ensure student is assigned to a class/section for the academic year.
   * Returns existing active mapping if present, otherwise creates a new one.
   */
  ensureAssignStudentToClassSection(request: StudentClassSectionRequest): Observable<StudentClassSectionResponse> {
    return this.http.post<StudentClassSectionResponse>(`${this.API_BASE}/assign/ensure`, request);
  }

  /**
   * Update student's class and section
   */
  updateStudentClassSection(mappingId: number, request: StudentClassSectionRequest): Observable<StudentClassSectionResponse> {
    return this.http.put<StudentClassSectionResponse>(`${this.API_BASE}/update/${mappingId}`, request);
  }

  /**
   * Get students by class and section
   */
  getStudentsByClassAndSection(academicYearId: number, classId: number, sectionId?: number): Observable<StudentClassSectionResponse[]> {
    let params = new HttpParams()
      .set('academicYearId', academicYearId.toString())
      .set('classId', classId.toString());
    
    if (sectionId) {
      params = params.set('sectionId', sectionId.toString());
    }

    return this.http.get<StudentClassSectionResponse[]>(`${this.API_BASE}/students`, { params });
  }

  /**
   * Get student's current assignment
   */
  getStudentCurrentAssignment(studentId: number, academicYearId: number): Observable<StudentClassSectionResponse> {
    const params = new HttpParams()
      .set('studentId', studentId.toString())
      .set('academicYearId', academicYearId.toString());

    return this.http.get<StudentClassSectionResponse>(`${this.API_BASE}/student/current`, { params });
  }

  /**
   * Get student's enrollment history
   */
  getStudentHistory(studentId: number): Observable<StudentClassSectionResponse[]> {
    return this.http.get<StudentClassSectionResponse[]>(`${this.API_BASE}/student/history/${studentId}`);
  }

  /**
   * Deactivate student's assignment
   */
  deactivateStudentAssignment(mappingId: number): Observable<string> {
    return this.http.delete<string>(`${this.API_BASE}/deactivate/${mappingId}`);
  }
}
