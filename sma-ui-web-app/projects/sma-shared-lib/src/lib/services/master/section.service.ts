import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SectionMaster, SectionMasterRequest, SectionMasterResponse } from '../../models/master';

/**
 * SectionMaster Service
 * Handles all section master data related API calls
 */
@Injectable({
  providedIn: 'root'
})
export class SectionMasterService {
  private readonly API_BASE = '/api/section';

  constructor(private http: HttpClient) {}

  /**
   * Create a new section
   */
  createSection(request: SectionMasterRequest): Observable<SectionMasterResponse> {
    return this.http.post<SectionMasterResponse>(`${this.API_BASE}/create`, request);
  }

  /**
   * Get section by ID
   */
  getSection(sectionId: string): Observable<SectionMasterResponse> {
    return this.http.get<SectionMasterResponse>(`${this.API_BASE}/get`, {
      params: { sectionId }
    });
  }

  /**
   * Get all sections for a class
   */
  getSectionsByClass(schoolId: number, classId: string): Observable<SectionMasterResponse[]> {
    return this.http.get<SectionMasterResponse[]>(
      `${this.API_BASE}/school/${schoolId}/class/${classId}`
    );
  }

  /**
   * Update section
   */
  updateSection(sectionId: string, request: SectionMasterRequest): Observable<SectionMasterResponse> {
    return this.http.put<SectionMasterResponse>(`${this.API_BASE}/update`, request, {
      params: { sectionId }
    });
  }

  /**
   * Delete section
   */
  deleteSection(sectionId: string): Observable<void> {
    return this.http.delete<void>(`${this.API_BASE}/delete`, {
      params: { sectionId }
    });
  }
}
