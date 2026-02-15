import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StudentFeePaymentRequest, StudentFeePaymentResponse } from '../models/fee/fee.model';

@Injectable({
  providedIn: 'root'
})
export class FeePaymentService {
  private apiUrl = '/api/fee-payment';

  constructor(private http: HttpClient) {}

  /**
   * Record a new fee payment
   */
  recordPayment(schoolId: number, request: StudentFeePaymentRequest): Observable<StudentFeePaymentResponse> {
    const params = new HttpParams().set('schoolId', schoolId.toString());
    return this.http.post<StudentFeePaymentResponse>(`${this.apiUrl}/record`, request, { params });
  }

  /**
   * Get payment by ID
   */
  getPaymentById(paymentId: number): Observable<StudentFeePaymentResponse> {
    return this.http.get<StudentFeePaymentResponse>(`${this.apiUrl}/get/${paymentId}`);
  }

  /**
   * List payments with filters
   */
  listPayments(schoolId: number, studentId?: number, installmentId?: number): Observable<StudentFeePaymentResponse[]> {
    let params = new HttpParams().set('schoolId', schoolId.toString());
    
    if (studentId !== undefined && studentId !== null) {
      params = params.set('studentId', studentId.toString());
    }
    
    if (installmentId !== undefined && installmentId !== null) {
      params = params.set('installmentId', installmentId.toString());
    }
    
    return this.http.get<StudentFeePaymentResponse[]>(`${this.apiUrl}/list`, { params });
  }

  /**
   * Delete payment
   */
  deletePayment(paymentId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${paymentId}`);
  }
}
