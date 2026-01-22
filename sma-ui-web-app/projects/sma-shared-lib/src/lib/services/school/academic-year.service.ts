import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AcademicYear, AcademicYearRequest, AcademicYearResponse } from '../../models/school';

/**
 * Academic Year Service
 * Handles all academic year related API calls
 */
@Injectable({
  providedIn: 'root'
})
export class AcademicYearService {
  private readonly API_BASE = '/api/academic-year';

  constructor(private http: HttpClient) {}

  /**
   * Create a new academic year
   */
  createAcademicYear(request: AcademicYearRequest): Observable<AcademicYearResponse> {
    return this.http.post<AcademicYearResponse>(`${this.API_BASE}/create`, request);
  }

  /**
   * Get academic year by ID
   */
  getAcademicYear(yearId: number): Observable<AcademicYearResponse> {
    const params = new HttpParams().set('yearId', yearId.toString());
    return this.http.get<AcademicYearResponse>(`${this.API_BASE}/get`, { params });
  }

  /**
   * Get all academic years
   */
  getAllAcademicYears(): Observable<AcademicYearResponse[]> {
    return this.http.get<AcademicYearResponse[]>(`${this.API_BASE}/getAll`);
  }

  /**
   * Get current academic year
   */
  getCurrentAcademicYear(): Observable<AcademicYearResponse> {
    return this.http.get<AcademicYearResponse>(`${this.API_BASE}/getCurrent`);
  }

  /**
   * Update academic year
   */
  updateAcademicYear(yearId: number, request: AcademicYearRequest): Observable<AcademicYearResponse> {
    const params = new HttpParams().set('yearId', yearId.toString());
    return this.http.put<AcademicYearResponse>(`${this.API_BASE}/update`, request, { params });
  }

  /**
   * Set academic year as current
   */
  setCurrentAcademicYear(yearId: number): Observable<AcademicYearResponse> {
    const params = new HttpParams().set('yearId', yearId.toString());
    return this.http.put<AcademicYearResponse>(`${this.API_BASE}/setCurrent`, null, { params });
  }

  /**
   * Delete academic year
   */
  deleteAcademicYear(yearId: number): Observable<string> {
    const params = new HttpParams().set('yearId', yearId.toString());
    return this.http.delete<string>(`${this.API_BASE}/delete`, { params });
  }
}
