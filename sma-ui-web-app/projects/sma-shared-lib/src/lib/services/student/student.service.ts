import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Student, StudentRequest } from '../../models/student';

@Injectable({
  providedIn: 'root'
})
export class StudentService {
  private baseUrl = '/api/schools';

  constructor(private http: HttpClient) { }

  createStudent(schoolId: string, student: StudentRequest): Observable<Student> {
    return this.http.post<Student>(`${this.baseUrl}/${schoolId}/students`, student);
  }

  getStudent(schoolId: string, studentId: string): Observable<Student> {
    return this.http.get<Student>(`${this.baseUrl}/${schoolId}/students/${studentId}`);
  }

  getAllStudents(schoolId: string): Observable<Student[]> {
    return this.http.get<Student[]>(`${this.baseUrl}/${schoolId}/students`);
  }

  updateStudent(schoolId: string, studentId: string, student: StudentRequest): Observable<Student> {
    return this.http.put<Student>(`${this.baseUrl}/${schoolId}/students/${studentId}`, student);
  }

  deleteStudent(schoolId: string, studentId: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${schoolId}/students/${studentId}`);
  }
}
