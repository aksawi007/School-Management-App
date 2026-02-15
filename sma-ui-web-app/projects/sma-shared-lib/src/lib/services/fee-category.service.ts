import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FeeCategoryRequest, FeeCategoryResponse } from '../models/fee/fee.model';

@Injectable({
  providedIn: 'root'
})
export class FeeCategoryService {
  private apiUrl = '/api/fee-category';

  constructor(private http: HttpClient) {}

  createFeeCategory(schoolId: number, request: FeeCategoryRequest): Observable<FeeCategoryResponse> {
    return this.http.post<FeeCategoryResponse>(`${this.apiUrl}/create`, request, {
      params: { schoolId: schoolId.toString() }
    });
  }

  updateFeeCategory(categoryId: string, request: FeeCategoryRequest): Observable<FeeCategoryResponse> {
    return this.http.put<FeeCategoryResponse>(`${this.apiUrl}/update/${categoryId}`, request);
  }

  getFeeCategoryById(categoryId: string): Observable<FeeCategoryResponse> {
    return this.http.get<FeeCategoryResponse>(`${this.apiUrl}/get/${categoryId}`);
  }

  getAllActiveFeeCategories(schoolId: number): Observable<FeeCategoryResponse[]> {
    return this.http.get<FeeCategoryResponse[]>(`${this.apiUrl}/list/active`, {
      params: { schoolId: schoolId.toString() }
    });
  }

  getFeeCategoriesByType(schoolId: number, categoryType: string): Observable<FeeCategoryResponse[]> {
    return this.http.get<FeeCategoryResponse[]>(`${this.apiUrl}/list/by-type`, {
      params: { schoolId: schoolId.toString(), categoryType }
    });
  }

  deactivateFeeCategory(categoryId: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/deactivate/${categoryId}`, {});
  }

  activateFeeCategory(categoryId: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/activate/${categoryId}`, {});
  }
}
