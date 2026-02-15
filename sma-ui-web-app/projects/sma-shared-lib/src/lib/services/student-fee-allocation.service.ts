import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StudentFeeAllocationRequest, StudentFeeAllocationResponse } from '../models/fee/fee.model';

@Injectable({
  providedIn: 'root'
})
export class StudentFeeAllocationService {
  private apiUrl = '/api/student-fee-allocation';

  constructor(private http: HttpClient) {}

  allocateFeeToStudent(
    schoolId: number,
    academicYearId: string,
    allocatedBy: number,
    request: StudentFeeAllocationRequest
  ): Observable<StudentFeeAllocationResponse> {
    return this.http.post<StudentFeeAllocationResponse>(`${this.apiUrl}/allocate`, request, {
      params: {
        schoolId: schoolId.toString(),
        academicYearId,
        allocatedBy: allocatedBy.toString()
      }
    });
  }

  getStudentFeeAllocations(studentId: number, academicYearId: string): Observable<StudentFeeAllocationResponse[]> {
    return this.http.get<StudentFeeAllocationResponse[]>(`${this.apiUrl}/student/${studentId}`, {
      params: { academicYearId }
    });
  }

  getPendingFeeAllocations(studentId: number): Observable<StudentFeeAllocationResponse[]> {
    return this.http.get<StudentFeeAllocationResponse[]>(`${this.apiUrl}/student/${studentId}/pending`);
  }

  getOverdueFeeAllocations(schoolId: number, academicYearId: string): Observable<StudentFeeAllocationResponse[]> {
    return this.http.get<StudentFeeAllocationResponse[]>(`${this.apiUrl}/overdue`, {
      params: { schoolId: schoolId.toString(), academicYearId }
    });
  }

  getFeeAllocationsByMonth(
    schoolId: number,
    academicYearId: string,
    month: string
  ): Observable<StudentFeeAllocationResponse[]> {
    return this.http.get<StudentFeeAllocationResponse[]>(`${this.apiUrl}/by-month`, {
      params: { schoolId: schoolId.toString(), academicYearId, month }
    });
  }

  calculateTotalPendingAmount(studentId: number, academicYearId: string): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/student/${studentId}/total-pending`, {
      params: { academicYearId }
    });
  }

  updateAllocationStatus(allocationId: string, status: string, paidAmount?: number): Observable<StudentFeeAllocationResponse> {
    const params: any = { status };
    if (paidAmount !== undefined) {
      params.paidAmount = paidAmount.toString();
    }
    return this.http.put<StudentFeeAllocationResponse>(`${this.apiUrl}/update-status/${allocationId}`, {}, { params });
  }

  cancelFeeAllocation(allocationId: string, reason: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/cancel/${allocationId}`, {
      params: { reason }
    });
  }
}
