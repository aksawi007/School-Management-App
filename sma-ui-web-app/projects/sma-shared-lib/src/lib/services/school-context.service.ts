import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface SchoolContext {
  schoolId: string | null;
  schoolName: string | null;
  schoolCode: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class SchoolContextService {
  private schoolContextSubject = new BehaviorSubject<SchoolContext>({
    schoolId: null,
    schoolName: null,
    schoolCode: null
  });

  public schoolContext$: Observable<SchoolContext> = this.schoolContextSubject.asObservable();

  constructor() { }

  setSchoolContext(context: SchoolContext): void {
    this.schoolContextSubject.next(context);
    // Store in sessionStorage for persistence across micro frontends
    sessionStorage.setItem('schoolContext', JSON.stringify(context));
  }

  getSchoolContext(): SchoolContext {
    const stored = sessionStorage.getItem('schoolContext');
    if (stored) {
      const context = JSON.parse(stored);
      this.schoolContextSubject.next(context);
      return context;
    }
    return this.schoolContextSubject.value;
  }

  clearSchoolContext(): void {
    this.schoolContextSubject.next({
      schoolId: null,
      schoolName: null,
      schoolCode: null
    });
    sessionStorage.removeItem('schoolContext');
  }
}
