import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SubjectMaster, SubjectMasterRequest, SubjectMasterResponse } from '../../models/master';

/**
 * SubjectMaster Service
 * Handles all subject master data related API calls
 */
@Injectable({
  providedIn: 'root'
})
export class SubjectMasterService {
  private readonly API_BASE = '/api/subject';

  constructor(private http: HttpClient) {}

  /**
   * Create a new subject
   */
  createSubject(request: SubjectMasterRequest): Observable<SubjectMasterResponse> {
    return this.http.post<SubjectMasterResponse>(`${this.API_BASE}/create`, request);
  }

  /**
   * Get subject by ID
   */
  getSubject(subjectId: string): Observable<SubjectMasterResponse> {
    return this.http.get<SubjectMasterResponse>(`${this.API_BASE}/get`, {
      params: { subjectId }
    });
  }

  /**
   * Get all subjects for a school
   */
  getAllSubjectsBySchool(schoolId: number): Observable<SubjectMasterResponse[]> {
    return this.http.get<SubjectMasterResponse[]>(`${this.API_BASE}/school/${schoolId}`);
  }

  /**
   * Get subjects by type
   */
  getSubjectsByType(schoolId: number, subjectType: string): Observable<SubjectMasterResponse[]> {
    return this.http.get<SubjectMasterResponse[]>(
      `${this.API_BASE}/school/${schoolId}/type/${subjectType}`
    );
  }

  /**
   * Update subject
   */
  updateSubject(subjectId: string, request: SubjectMasterRequest): Observable<SubjectMasterResponse> {
    return this.http.put<SubjectMasterResponse>(`${this.API_BASE}/update`, request, {
      params: { subjectId }
    });
  }

  /**
   * Delete subject
   */
  deleteSubject(subjectId: string): Observable<void> {
    return this.http.delete<void>(`${this.API_BASE}/delete`, {
      params: { subjectId }
    });
  }
}
