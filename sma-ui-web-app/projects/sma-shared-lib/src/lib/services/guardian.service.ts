import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Guardian, GuardianRequest } from '../models/guardian.model';

@Injectable({
  providedIn: 'root'
})
export class GuardianService {
  private baseUrl = '/api/schools';

  constructor(private http: HttpClient) { }

  addGuardian(schoolId: string, studentId: string, guardian: GuardianRequest): Observable<Guardian> {
    return this.http.post<Guardian>(
      `${this.baseUrl}/${schoolId}/students/${studentId}/guardians`,
      guardian
    );
  }

  getGuardians(schoolId: string, studentId: string): Observable<Guardian[]> {
    return this.http.get<Guardian[]>(
      `${this.baseUrl}/${schoolId}/students/${studentId}/guardians`
    );
  }

  deleteGuardian(schoolId: string, studentId: string, guardianId: string): Observable<void> {
    return this.http.delete<void>(
      `${this.baseUrl}/${schoolId}/students/${studentId}/guardians/${guardianId}`
    );
  }
}
