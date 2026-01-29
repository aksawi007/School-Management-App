import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DepartmentRequest, DepartmentResponse, DepartmentStaffResponse } from '../../models/master/department.model';

@Injectable({
  providedIn: 'root'
})
export class DepartmentService {
  private apiUrl = '/api/department';

  constructor(private http: HttpClient) {}

  createDepartment(request: DepartmentRequest): Observable<DepartmentResponse> {
    return this.http.post<DepartmentResponse>(`${this.apiUrl}/create`, request);
  }

  getDepartment(departmentId: number): Observable<DepartmentResponse> {
    return this.http.get<DepartmentResponse>(`${this.apiUrl}/get`, {
      params: { departmentId: departmentId.toString() }
    });
  }

  getDepartmentsBySchool(schoolId: number): Observable<DepartmentResponse[]> {
    return this.http.get<DepartmentResponse[]>(`${this.apiUrl}/getBySchool`, {
      params: { schoolId: schoolId.toString() }
    });
  }

  getDepartmentsByType(schoolId: number, departmentType: string): Observable<DepartmentResponse[]> {
    return this.http.get<DepartmentResponse[]>(`${this.apiUrl}/getByType`, {
      params: {
        schoolId: schoolId.toString(),
        departmentType: departmentType
      }
    });
  }

  getAllDepartments(schoolId: number): Observable<DepartmentResponse[]> {
    return this.http.get<DepartmentResponse[]>(`${this.apiUrl}/getBySchool`, {
      params: {
        schoolId: schoolId.toString()
      }
    });
  }

  updateDepartment(departmentId: number, request: DepartmentRequest): Observable<DepartmentResponse> {
    return this.http.put<DepartmentResponse>(`${this.apiUrl}/update`, request, {
      params: { departmentId: departmentId.toString() }
    });
  }

  deleteDepartment(departmentId: number): Observable<string> {
    return this.http.delete<string>(`${this.apiUrl}/delete`, {
      params: { departmentId: departmentId.toString() }
    });
  }

  getDepartmentStaff(departmentId: number): Observable<DepartmentStaffResponse[]> {
    return this.http.get<DepartmentStaffResponse[]>(`${this.apiUrl}/getStaff`, {
      params: { departmentId: departmentId.toString() }
    });
  }
}
