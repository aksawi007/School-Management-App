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
    return this.http.get<FeeCategoryResponse[]>(`${this.apiUrl}/list`, {
      params: { schoolId: schoolId.toString(), status: 'ACTIVE' }
    });
  }

  listFeeCategories(schoolId: number, status: string = 'ALL', categoryType?: string): Observable<FeeCategoryResponse[]> {
    let params: any = { schoolId: schoolId.toString(), status: status };
    if (categoryType) {
      params.categoryType = categoryType;
    }
    return this.http.get<FeeCategoryResponse[]>(`${this.apiUrl}/list`, { params });
  }

}
