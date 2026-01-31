import { Injectable } from '@angular/core';
import { AcademicYearService, AcademicYearResponse } from 'sma-shared-lib';
import { BehaviorSubject, Observable, of } from 'rxjs';

export interface SchoolDetails {
  schoolId: number;
  schoolName?: string;
  schoolCode?: string;
}

/**
 * Admin Cache Service
 * Central service for managing cached data and session context for admin application
 * - School context from parent shell application
 * - Academic years
 * - Other cached data
 */
@Injectable({
  providedIn: 'root'
})
export class AdminCacheService {
  private schoolDetails$ = new BehaviorSubject<SchoolDetails | null>(null);
  private academicYears$ = new BehaviorSubject<AcademicYearResponse[]>([]);
  private academicYearsLoaded = false;
  private loadingPromise: Promise<AcademicYearResponse[]> | null = null;

  constructor(private academicYearService: AcademicYearService) {
    this.initializeSchoolContext();
  }

  /**
   * Initialize school context by listening to parent application messages
   */
  initializeSchoolContext(): void {
    window.addEventListener('message', (event) => {
      if (event.origin !== 'http://localhost:4300') return;
      
      if (event.data && event.data.type === 'SCHOOL_CONTEXT') {
        const school = event.data.school;
        if (school) {
          const schoolDetails: SchoolDetails = {
            schoolId: school.schoolId,
            schoolName: school.schoolName,
            schoolCode: school.schoolCode
          };
          this.schoolDetails$.next(schoolDetails);
          console.log('School context loaded:', schoolDetails);
          
          // Auto-load academic years when school context is received
          this.loadAcademicYears();
        }
      }
    });
    
    // Request context from parent if running in iframe
    if (window.parent && window.parent !== window) {
      window.parent.postMessage({ type: 'REQUEST_CONTEXT' }, 'http://localhost:4300');
    }
  }

  /**
   * Get school details as observable
   */
  getSchoolDetails(): Observable<SchoolDetails | null> {
    return this.schoolDetails$.asObservable();
  }

  /**
   * Get school details synchronously
   */
  getSchoolDetailsSync(): SchoolDetails | null {
    return this.schoolDetails$.value;
  }

  /**
   * Get school ID synchronously (returns 0 if not loaded)
   */
  getSchoolId(): number {
    return this.schoolDetails$.value?.schoolId || 0;
  }

  /**
   * Check if school context is loaded
   */
  isSchoolContextLoaded(): boolean {
    return this.schoolDetails$.value !== null;
  }

  /**
   * Get academic years - loads from cache if available, otherwise fetches from API
   */
  getAcademicYears(): Observable<AcademicYearResponse[]> {
    if (this.academicYearsLoaded) {
      return of(this.academicYears$.value);
    }
    
    return new Observable(observer => {
      this.loadAcademicYears().then(years => {
        observer.next(years);
        observer.complete();
      }).catch(error => {
        observer.error(error);
      });
    });
  }

  /**
   * Load academic years from API and cache them
   */
  private async loadAcademicYears(): Promise<AcademicYearResponse[]> {
    if (this.loadingPromise) {
      return this.loadingPromise;
    }

    const schoolId = this.getSchoolId();
    if (!schoolId) {
      console.warn('Cannot load academic years: School context not available');
      return Promise.resolve([]);
    }

    this.loadingPromise = new Promise((resolve, reject) => {
      this.academicYearService.getAllAcademicYears().subscribe({
        next: (years: AcademicYearResponse[]) => {
          console.log('Academic years loaded from API:', years);
          this.academicYears$.next(years);
          this.academicYearsLoaded = true;
          this.loadingPromise = null;
          resolve(years);
        },
        error: (error: any) => {
          console.error('Failed to load academic years:', error);
          this.loadingPromise = null;
          reject(error);
        }
      });
    });

    return this.loadingPromise;
  }

  /**
   * Refresh academic years from API
   */
  refreshAcademicYears(): Observable<AcademicYearResponse[]> {
    this.academicYearsLoaded = false;
    this.loadingPromise = null;
    return this.getAcademicYears();
  }

  /**
   * Get cached academic years synchronously (returns empty array if not loaded)
   */
  getCachedAcademicYears(): AcademicYearResponse[] {
    return this.academicYears$.value;
  }

  /**
   * Check if academic years are loaded
   */
  areAcademicYearsLoaded(): boolean {
    return this.academicYearsLoaded;
  }

  /**
   * Get current academic year from cached list
   */
  getCurrentAcademicYear(): AcademicYearResponse | undefined {
    return this.academicYears$.value.find(y => y.currentYear === true);
  }

  /**
   * Clear all cached data
   */
  clearCache(): void {
    this.academicYears$.next([]);
    this.academicYearsLoaded = false;
    this.loadingPromise = null;
  }
}
