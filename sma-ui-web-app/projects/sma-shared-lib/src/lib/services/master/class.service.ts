import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ClassMaster, ClassMasterRequest, ClassMasterResponse } from '../../models/master';

/**
 * ClassMaster Service
 * Handles all class/grade master data related API calls
 */
@Injectable({
  providedIn: 'root'
})
export class ClassMasterService {
  private readonly API_BASE = '/api/class';

  constructor(private http: HttpClient) {}

  /**
   * Create a new class
   */
  createClass(request: ClassMasterRequest): Observable<ClassMasterResponse> {
    return this.http.post<ClassMasterResponse>(`${this.API_BASE}/create`, request);
  }

  /**
   * Get class by ID
   */
  getClass(classId: string): Observable<ClassMasterResponse> {
    return this.http.get<ClassMasterResponse>(`${this.API_BASE}/get`, {
      params: { classId }
    });
  }

  /**
   * Get all classes for a school
   */
  getAllClassesBySchool(schoolId: number): Observable<ClassMasterResponse[]> {
    return this.http.get<ClassMasterResponse[]>(`${this.API_BASE}/school/${schoolId}`);
  }

  /**
   * Get all classes for an academic year
   */
  getAllClassesByAcademicYear(academicYearId: number): Observable<ClassMasterResponse[]> {
    return this.http.get<ClassMasterResponse[]>(`${this.API_BASE}/academicYear/${academicYearId}`);
  }

  /**
   * Update class
   */
  updateClass(classId: string, request: ClassMasterRequest): Observable<ClassMasterResponse> {
    return this.http.put<ClassMasterResponse>(`${this.API_BASE}/update`, request, {
      params: { classId }
    });
  }

  /**
   * Delete class
   */
  deleteClass(classId: string): Observable<void> {
    return this.http.delete<void>(`${this.API_BASE}/delete`, {
      params: { classId }
    });
  }
}
