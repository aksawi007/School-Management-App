import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface SchoolContext {
  schoolId: number;
  schoolName: string;
  schoolCode: string;
}

export interface UserContext {
  userId?: number;
  username?: string;
  role?: string;
  email?: string;
}

/**
 * Application Context Service
 * Manages shared state across all micro frontends
 */
@Injectable({
  providedIn: 'root'
})
export class AppContextService {
  private selectedSchoolSubject = new BehaviorSubject<SchoolContext | null>(null);
  private userContextSubject = new BehaviorSubject<UserContext | null>(null);

  selectedSchool$: Observable<SchoolContext | null> = this.selectedSchoolSubject.asObservable();
  userContext$: Observable<UserContext | null> = this.userContextSubject.asObservable();

  constructor() {
    // Load from sessionStorage if available
    this.loadFromStorage();
  }

  /**
   * Set the selected school context
   */
  setSchool(school: SchoolContext): void {
    this.selectedSchoolSubject.next(school);
    sessionStorage.setItem('selectedSchool', JSON.stringify(school));
  }

  /**
   * Get the current selected school
   */
  getSchool(): SchoolContext | null {
    return this.selectedSchoolSubject.value;
  }

  /**
   * Clear selected school
   */
  clearSchool(): void {
    this.selectedSchoolSubject.next(null);
    sessionStorage.removeItem('selectedSchool');
  }

  /**
   * Set user context
   */
  setUser(user: UserContext): void {
    this.userContextSubject.next(user);
    sessionStorage.setItem('userContext', JSON.stringify(user));
  }

  /**
   * Get current user context
   */
  getUser(): UserContext | null {
    return this.userContextSubject.value;
  }

  /**
   * Clear user context
   */
  clearUser(): void {
    this.userContextSubject.next(null);
    sessionStorage.removeItem('userContext');
  }

  /**
   * Load context from session storage
   */
  private loadFromStorage(): void {
    const schoolData = sessionStorage.getItem('selectedSchool');
    if (schoolData) {
      try {
        this.selectedSchoolSubject.next(JSON.parse(schoolData));
      } catch (e) {
        console.error('Error loading school context:', e);
      }
    }

    const userData = sessionStorage.getItem('userContext');
    if (userData) {
      try {
        this.userContextSubject.next(JSON.parse(userData));
      } catch (e) {
        console.error('Error loading user context:', e);
      }
    }
  }

  /**
   * Clear all context
   */
  clearAll(): void {
    this.clearSchool();
    this.clearUser();
  }
}
