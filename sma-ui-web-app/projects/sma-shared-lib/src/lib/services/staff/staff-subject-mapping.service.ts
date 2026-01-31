import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface StaffSubjectMappingRequest {
  schoolId?: number;
  staffId: number;
  subjectId: number;
  proficiencyLevel: 'EXPERT' | 'QUALIFIED' | 'SUBSTITUTE';
  canTeachPrimary?: boolean;
  canTeachSecondary?: boolean;
  canTeachHigherSecondary?: boolean;
  remarks?: string;
}

export interface StaffSubjectMappingResponse {
  id: number;
  schoolId: number;
  staffId: number;
  staffName: string;
  employeeCode: string;
  subjectId: number;
  subjectName: string;
  subjectCode: string;
  proficiencyLevel: string;
  canTeachPrimary: boolean;
  canTeachSecondary: boolean;
  canTeachHigherSecondary: boolean;
  isActive: boolean;
  remarks?: string;
}

@Injectable({
  providedIn: 'root'
})
export class StaffSubjectMappingService {
  private baseUrl = '/api/schools';

  constructor(private http: HttpClient) {}

  /**
   * Create a new staff-subject mapping
   */
  createMapping(schoolId: number, request: StaffSubjectMappingRequest): Observable<StaffSubjectMappingResponse> {
    return this.http.post<StaffSubjectMappingResponse>(
      `${this.baseUrl}/${schoolId}/staff-subjects`,
      request
    );
  }

  /**
   * Update an existing staff-subject mapping
   */
  updateMapping(schoolId: number, mappingId: number, request: StaffSubjectMappingRequest): Observable<StaffSubjectMappingResponse> {
    return this.http.put<StaffSubjectMappingResponse>(
      `${this.baseUrl}/${schoolId}/staff-subjects/${mappingId}`,
      request
    );
  }

  /**
   * Delete (soft delete) a staff-subject mapping
   */
  deleteMapping(schoolId: number, mappingId: number): Observable<void> {
    return this.http.delete<void>(
      `${this.baseUrl}/${schoolId}/staff-subjects/${mappingId}`
    );
  }

  /**
   * Get all subjects a staff member is qualified to teach
   */
  getSubjectsForStaff(schoolId: number, staffId: number): Observable<StaffSubjectMappingResponse[]> {
    return this.http.get<StaffSubjectMappingResponse[]>(
      `${this.baseUrl}/${schoolId}/staff-subjects/staff/${staffId}`
    );
  }

  /**
   * Get all qualified teachers for a specific subject (KEY METHOD for routine builder)
   * @param schoolId School ID
   * @param subjectId Subject ID
   * @param classId Optional class ID to filter by grade level
   * @param departmentId Optional department ID linked to the subject
   */
  getQualifiedTeachersForSubject(schoolId: number, subjectId: number, classId?: number, departmentId?: number): Observable<StaffSubjectMappingResponse[]> {
    let params = new HttpParams().set('subjectId', subjectId.toString());
    if (classId) {
      params = params.set('classId', classId.toString());
    }
    if (departmentId) {
      params = params.set('departmentId', departmentId.toString());
    }
    return this.http.get<StaffSubjectMappingResponse[]>(
      `${this.baseUrl}/${schoolId}/staff-subjects/qualified`,
      { params }
    );
  }

  /**
   * Get all staff-subject mappings for a school (admin view)
   */
  getAllMappingsForSchool(schoolId: number): Observable<StaffSubjectMappingResponse[]> {
    return this.http.get<StaffSubjectMappingResponse[]>(
      `${this.baseUrl}/${schoolId}/staff-subjects`
    );
  }
}
