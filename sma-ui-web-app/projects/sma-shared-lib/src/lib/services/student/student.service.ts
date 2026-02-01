import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Student, StudentRequest, Guardian, Address } from '../../models/student';

@Injectable({
  providedIn: 'root'
})
export class StudentService {
  private baseUrl = '/api/schools';

  constructor(private http: HttpClient) { }

  createStudent(schoolId: number, student: StudentRequest): Observable<Student> {
    return this.http.post<Student>(`${this.baseUrl}/${schoolId}/students`, student);
  }

  getStudent(schoolId: number, studentId: string): Observable<Student> {
    return this.http.get<Student>(`${this.baseUrl}/${schoolId}/students/${studentId}`);
  }

  getAllStudents(schoolId: number): Observable<Student[]> {
    // Backend returns paginated response, extract content array
    return this.http.get<any>(`${this.baseUrl}/${schoolId}/students`).pipe(
      map(response => response.content || response)
    );
  }

  /**
   * Search students with optional query params. If backend supports query parameters
   * this will forward them; otherwise backend should ignore unknown params.
   */
  searchStudents(schoolId: number, params?: { [key: string]: any }): Observable<Student[]> {
    const options = params ? { params } : {};
    return this.http.get<any>(`${this.baseUrl}/${schoolId}/students`, options).pipe(
      map(response => response.content || response)
    );
  }

  /**
   * Fetch students with pagination. Returns the raw paginated response from backend
   * which includes `content`, `totalElements`, `totalPages`, etc.
   */
  getStudentsPage(schoolId: number, page = 0, size = 20, sortBy = 'admissionNo', sortDir = 'ASC') {
    const params: any = { page: String(page), size: String(size), sortBy, sortDir };
    return this.http.get<any>(`${this.baseUrl}/${schoolId}/students`, { params });
  }

  /**
   * Search students with pagination. Accepts `params` (e.g. { search, status }) and pagination options.
   */
  searchStudentsPaged(schoolId: number, params?: { [key: string]: any }, page = 0, size = 20, sortBy = 'admissionNo', sortDir = 'ASC') {
    const httpParams: any = { page: String(page), size: String(size), sortBy, sortDir };
    if (params) {
      Object.keys(params).forEach(k => {
        if (params[k] !== undefined && params[k] !== null && params[k] !== '') {
          httpParams[k] = params[k];
        }
      });
    }
    return this.http.get<any>(`${this.baseUrl}/${schoolId}/students`, { params: httpParams });
  }

  updateStudent(schoolId: number, studentId: string, student: StudentRequest): Observable<Student> {
    return this.http.put<Student>(`${this.baseUrl}/${schoolId}/students/${studentId}`, student);
  }

  deleteStudent(schoolId: number, studentId: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${schoolId}/students/${studentId}`);
  }

  getGuardians(schoolId: number, studentId: string): Observable<Guardian[]> {
    return this.http.get<Guardian[]>(`${this.baseUrl}/${schoolId}/students/${studentId}/guardians`);
  }

  getAddresses(schoolId: number, studentId: string): Observable<Address[]> {
    return this.http.get<Address[]>(`${this.baseUrl}/${schoolId}/students/${studentId}/addresses`);
  }
}
