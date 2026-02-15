import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FeePlanRequest, FeePlanResponse } from '../models/fee/fee.model';

@Injectable({
  providedIn: 'root'
})
export class FeePlanService {
  private apiUrl = '/api/fee-plan';

  constructor(private http: HttpClient) {}

  createFeePlan(schoolId: number, request: FeePlanRequest): Observable<FeePlanResponse> {
    return this.http.post<FeePlanResponse>(`${this.apiUrl}/create`, request, {
      params: { schoolId: schoolId.toString() }
    });
  }

  updateFeePlan(planId: number, request: FeePlanRequest): Observable<FeePlanResponse> {
    return this.http.put<FeePlanResponse>(`${this.apiUrl}/update/${planId}`, request);
  }

  getFeePlanById(planId: number, includeInstallments: boolean = true): Observable<FeePlanResponse> {
    return this.http.get<FeePlanResponse>(`${this.apiUrl}/get/${planId}`, {
      params: { includeInstallments: includeInstallments.toString() }
    });
  }

  listFeePlans(
    schoolId: number,
    academicYearId?: number,
    categoryId?: number,
    status: string = 'ALL',
    includeInstallments: boolean = false
  ): Observable<FeePlanResponse[]> {
    let params: any = { 
      schoolId: schoolId.toString(), 
      status: status,
      includeInstallments: includeInstallments.toString()
    };
    if (academicYearId) {
      params.academicYearId = academicYearId.toString();
    }
    if (categoryId) {
      params.categoryId = categoryId.toString();
    }
    return this.http.get<FeePlanResponse[]>(`${this.apiUrl}/list`, { params });
  }

  deleteFeePlan(planId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${planId}`);
  }
}
