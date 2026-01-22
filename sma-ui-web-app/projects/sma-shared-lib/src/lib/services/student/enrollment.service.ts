import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Enrollment, EnrollmentRequest, PromoteRequest, WithdrawRequest } from '../../models/student';

@Injectable({
  providedIn: 'root'
})
export class EnrollmentService {
  private baseUrl = '/api/schools';

  constructor(private http: HttpClient) { }

  enrollStudent(schoolId: string, studentId: string, enrollment: EnrollmentRequest): Observable<Enrollment> {
    return this.http.post<Enrollment>(
      `${this.baseUrl}/${schoolId}/students/${studentId}/enrollments`,
      enrollment
    );
  }

  getEnrollments(schoolId: string, studentId: string): Observable<Enrollment[]> {
    return this.http.get<Enrollment[]>(
      `${this.baseUrl}/${schoolId}/students/${studentId}/enrollments`
    );
  }

  promoteStudent(schoolId: string, studentId: string, promoteRequest: PromoteRequest): Observable<Enrollment> {
    return this.http.post<Enrollment>(
      `${this.baseUrl}/${schoolId}/students/${studentId}/promote`,
      promoteRequest
    );
  }

  withdrawStudent(schoolId: string, studentId: string, withdrawRequest: WithdrawRequest): Observable<void> {
    return this.http.post<void>(
      `${this.baseUrl}/${schoolId}/students/${studentId}/withdraw`,
      withdrawRequest
    );
  }
}
