import { Injectable } from '@angular/core';
import { AcademicYearService, AcademicYearResponse } from 'sma-shared-lib';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { tap, shareReplay } from 'rxjs/operators';

/**
 * Academic Year Cache Service
 * Loads academic years once and caches them for reuse across components
 */
@Injectable({
  providedIn: 'root'
})
export class AcademicYearCacheService {
  private academicYears$ = new BehaviorSubject<AcademicYearResponse[]>([]);
  private loaded = false;
  private loadingPromise: Promise<AcademicYearResponse[]> | null = null;

  constructor(private academicYearService: AcademicYearService) {}

  /**
   * Get academic years - loads from cache if available, otherwise fetches from API
   */
  getAcademicYears(): Observable<AcademicYearResponse[]> {
    if (this.loaded) {
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

    this.loadingPromise = new Promise((resolve, reject) => {
      this.academicYearService.getAllAcademicYears().subscribe({
        next: (years: AcademicYearResponse[]) => {
          this.academicYears$.next(years);
          this.loaded = true;
          this.loadingPromise = null;
          resolve(years);
        },
        error: (error: any) => {
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
  refresh(): Observable<AcademicYearResponse[]> {
    this.loaded = false;
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
  isLoaded(): boolean {
    return this.loaded;
  }
}
