import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SchoolProfile, SchoolProfileRequest, SchoolProfileResponse } from '../../models/school';

/**
 * School Profile Service
 * Handles all school profile related API calls
 */
@Injectable({
  providedIn: 'root'
})
export class SchoolProfileService {
  private readonly API_BASE = '/api/school/profile';

  constructor(private http: HttpClient) {}

  /**
   * Create a new school profile
   */
  createSchool(request: SchoolProfileRequest): Observable<SchoolProfileResponse> {
    return this.http.post<SchoolProfileResponse>(`${this.API_BASE}/create`, request);
  }

  /**
   * Get school profile by ID
   */
  getSchool(schoolId: number): Observable<SchoolProfileResponse> {
    const params = new HttpParams().set('schoolId', schoolId.toString());
    return this.http.get<SchoolProfileResponse>(`${this.API_BASE}/get`, { params });
  }

  /**
   * Get all school profiles
   */
  getAllSchools(): Observable<SchoolProfileResponse[]> {
    return this.http.get<SchoolProfileResponse[]>(`${this.API_BASE}/getAll`);
  }

  /**
   * Get school by code
   */
  getSchoolByCode(schoolCode: string): Observable<SchoolProfileResponse> {
    const params = new HttpParams().set('schoolCode', schoolCode);
    return this.http.get<SchoolProfileResponse>(`${this.API_BASE}/getByCode`, { params });
  }

  /**
   * Update school profile
   */
  updateSchool(schoolId: number, request: SchoolProfileRequest): Observable<SchoolProfileResponse> {
    const params = new HttpParams().set('schoolId', schoolId.toString());
    return this.http.put<SchoolProfileResponse>(`${this.API_BASE}/update`, request, { params });
  }

  /**
   * Delete school profile
   */
  deleteSchool(schoolId: number): Observable<string> {
    const params = new HttpParams().set('schoolId', schoolId.toString());
    return this.http.delete<string>(`${this.API_BASE}/delete`, { params });
  }
}
