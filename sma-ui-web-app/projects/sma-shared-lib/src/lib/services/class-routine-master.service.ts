import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ClassRoutineMaster, ClassRoutineMasterRequest } from '../models/routine/class-routine-master.model';

@Injectable({
  providedIn: 'root'
})
export class ClassRoutineMasterService {
  constructor(private http: HttpClient) {}

  createOrUpdateRoutine(schoolId: number, request: ClassRoutineMasterRequest): Observable<ClassRoutineMaster> {
    return this.http.post<ClassRoutineMaster>(`/api/schools/${schoolId}/routine/master`, request);
  }

  getWeeklyRoutine(schoolId: number, academicYearId: number, classId: number, sectionId: number): Observable<ClassRoutineMaster[]> {
    const params = new HttpParams()
      .set('academicYearId', academicYearId.toString())
      .set('classId', classId.toString())
      .set('sectionId', sectionId.toString());
    return this.http.get<ClassRoutineMaster[]>(`/api/schools/${schoolId}/routine/master/weekly`, { params });
  }

  getDailyRoutine(schoolId: number, academicYearId: number, classId: number, sectionId: number, dayOfWeek: string): Observable<ClassRoutineMaster[]> {
    const params = new HttpParams()
      .set('academicYearId', academicYearId.toString())
      .set('classId', classId.toString())
      .set('sectionId', sectionId.toString())
      .set('dayOfWeek', dayOfWeek);
    return this.http.get<ClassRoutineMaster[]>(`/api/schools/${schoolId}/routine/master/daily`, { params });
  }

  getTeacherRoutine(schoolId: number, academicYearId: number, teacherId: number, dayOfWeek: string): Observable<ClassRoutineMaster[]> {
    const params = new HttpParams()
      .set('academicYearId', academicYearId.toString())
      .set('teacherId', teacherId.toString())
      .set('dayOfWeek', dayOfWeek);
    return this.http.get<ClassRoutineMaster[]>(`/api/schools/${schoolId}/routine/master/teacher`, { params });
  }

  checkTeacherAvailability(
    schoolId: number,
    teacherId: number,
    timeSlotId: number,
    academicYearId: number,
    classId: number,
    sectionId: number,
    dayOfWeek?: string
  ): Observable<{ available: boolean; conflictingRoutines: ClassRoutineMaster[] }> {
    let params = new HttpParams()
      .set('teacherId', teacherId.toString())
      .set('timeSlotId', timeSlotId.toString())
      .set('academicYearId', academicYearId.toString())
      .set('classId', classId.toString())
      .set('sectionId', sectionId.toString());
    
    if (dayOfWeek) {
      params = params.set('dayOfWeek', dayOfWeek);
    }
    
    return this.http.get<{ available: boolean; conflictingRoutines: ClassRoutineMaster[] }>(
      `/api/schools/${schoolId}/routine/master/check-availability`,
      { params }
    );
  }

  getAvailableTeachers(
    schoolId: number,
    timeSlotId: number,
    academicYearId: number,
    dayOfWeek?: string
  ): Observable<any[]> {
    let params = new HttpParams()
      .set('timeSlotId', timeSlotId.toString())
      .set('academicYearId', academicYearId.toString());
    
    if (dayOfWeek) {
      params = params.set('dayOfWeek', dayOfWeek);
    }
    
    return this.http.get<any[]>(
      `/api/schools/${schoolId}/routine/master/available-teachers`,
      { params }
    );
  }

  deleteRoutineEntry(schoolId: number, routineId: number): Observable<any> {
    return this.http.delete(`/api/schools/${schoolId}/routine/master/${routineId}`);
  }

  getRoutineById(schoolId: number, routineId: number): Observable<ClassRoutineMaster> {
    return this.http.get<ClassRoutineMaster>(`/api/schools/${schoolId}/routine/master/${routineId}`);
  }
}
