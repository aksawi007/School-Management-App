import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Staff, StaffRequest, StaffResponse, StaffType } from '../../models/staff';

/**
 * Staff Service
 * Handles all staff management related API calls
 */
@Injectable({
  providedIn: 'root'
})
export class StaffService {
  private readonly API_BASE = '/api/staff';

  constructor(private http: HttpClient) {}

  /**
   * Create a new staff member
   */
  createStaff(request: StaffRequest): Observable<StaffResponse> {
    return this.http.post<StaffResponse>(`${this.API_BASE}`, request);
  }

  /**
   * Get staff by ID
   */
  getStaff(staffId: number): Observable<StaffResponse> {
    return this.http.get<StaffResponse>(`${this.API_BASE}/${staffId}`);
  }

  /**
   * Get all staff by school ID
   */
  getAllStaffBySchool(schoolId: number): Observable<StaffResponse[]> {
    return this.http.get<StaffResponse[]>(`${this.API_BASE}/school/${schoolId}`);
  }

  /**
   * Get staff by type (TEACHING, NON_TEACHING, ADMINISTRATIVE)
   */
  getStaffByType(schoolId: number, staffType: StaffType): Observable<StaffResponse[]> {
    return this.http.get<StaffResponse[]>(`${this.API_BASE}/school/${schoolId}/type/${staffType}`);
  }

  /**
   * Update staff member
   */
  updateStaff(staffId: number, request: StaffRequest): Observable<StaffResponse> {
    return this.http.put<StaffResponse>(`${this.API_BASE}/${staffId}`, request);
  }

  /**
   * Delete staff member
   */
  deleteStaff(staffId: number): Observable<void> {
    return this.http.delete<void>(`${this.API_BASE}/${staffId}`);
  }
}
