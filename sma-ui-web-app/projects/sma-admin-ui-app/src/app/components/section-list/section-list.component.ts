import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { SectionMasterService, SectionMasterResponse, ClassMasterService, ClassMasterResponse, AcademicYearService, AcademicYearResponse } from 'sma-shared-lib';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-section-list',
  templateUrl: './section-list.component.html',
  styleUrls: ['./section-list.component.scss']
})
export class SectionListComponent implements OnInit {
  sections: SectionMasterResponse[] = [];
  classes: ClassMasterResponse[] = [];
  allClasses: ClassMasterResponse[] = [];
  academicYears: AcademicYearResponse[] = [];
  selectedAcademicYearId?: number;
  selectedClassId?: string;
  displayedColumns: string[] = ['className', 'sectionCode', 'sectionName', 'capacity', 'roomNumber', 'actions'];
  loading = true;
  schoolId: number = 0;

  constructor(
    private sectionMasterService: SectionMasterService,
    private classMasterService: ClassMasterService,
    private academicYearService: AcademicYearService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    // Listen for school context from parent window (shell app)
    window.addEventListener('message', (event) => {
      // Verify origin for security
      if (event.origin !== 'http://localhost:4300') {
        return;
      }
      
      if (event.data && event.data.type === 'SCHOOL_CONTEXT') {
        console.log('Received school context:', event.data);
        const school = event.data.school;
        
        if (school) {
          this.schoolId = school.schoolId;
          
          // Load academic years and classes once we have schoolId
          this.loadAcademicYears();
          this.loadClasses();
          
          // Check if academicYearId and classId are in query params
          this.route.queryParams.subscribe(params => {
            if (params['academicYearId']) {
              this.selectedAcademicYearId = parseInt(params['academicYearId'], 10);
              this.filterClassesByAcademicYear();
            }
            
            if (params['classId']) {
              this.selectedClassId = params['classId'];
              this.loadSections();
            } else {
              // No classId selected, stop loading
              this.loading = false;
            }
          });
        }
      }
    });
    
    // Request context from parent
    if (window.parent && window.parent !== window) {
      window.parent.postMessage({ type: 'REQUEST_CONTEXT' }, 'http://localhost:4300');
    }
  }

  loadAcademicYears(): void {
    this.academicYearService.getAllAcademicYears().subscribe({
      next: (years) => {
        this.academicYears = years;
        // Auto-select current academic year if not already set from query params
        const currentYear = years.find(y => y.currentYear === true);
        if (currentYear && !this.selectedAcademicYearId) {
          this.selectedAcademicYearId = currentYear.yearId;
          this.filterClassesByAcademicYear();
        }
      },
      error: (error) => {
        console.error('Error loading academic years:', error);
        this.snackBar.open('Error loading academic years', 'Close', { duration: 3000 });
      }
    });
  }

  loadClasses(): void {
    this.classMasterService.getAllClassesBySchool(this.schoolId).subscribe({
      next: (classes) => {
        this.allClasses = classes.sort((a, b) => (a.displayOrder || 0) - (b.displayOrder || 0));
        this.filterClassesByAcademicYear();
      },
      error: (error) => {
        console.error('Error loading classes:', error);
      }
    });
  }

  onAcademicYearChange(): void {
    this.selectedClassId = undefined;
    this.sections = [];
    this.filterClassesByAcademicYear();
  }

  filterClassesByAcademicYear(): void {
    if (this.selectedAcademicYearId) {
      this.classes = this.allClasses.filter(c => c.academicYearId === this.selectedAcademicYearId);
    } else {
      this.classes = [];
    }
  }

  onClassChange(): void {
    if (this.selectedClassId) {
      this.router.navigate([], {
        relativeTo: this.route,
        queryParams: { classId: this.selectedClassId },
        queryParamsHandling: 'merge'
      });
      this.loadSections();
    }
  }

  loadSections(): void {
    if (!this.selectedClassId) {
      this.sections = [];
      this.loading = false;
      return;
    }

    this.loading = true;
    this.sectionMasterService.getSectionsByClass(this.schoolId, this.selectedClassId).subscribe({
      next: (sections) => {
        this.sections = sections;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading sections:', error);
        this.snackBar.open('Error loading sections', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  addSection(): void {
    if (!this.selectedAcademicYearId) {
      this.snackBar.open('Please select an academic year first', 'Close', { duration: 3000 });
      return;
    }
    if (!this.selectedClassId) {
      this.snackBar.open('Please select a class first', 'Close', { duration: 3000 });
      return;
    }
    this.router.navigate(['/sections/new'], { 
      queryParams: { 
        classId: this.selectedClassId,
        academicYearId: this.selectedAcademicYearId 
      } 
    });
  }

  editSection(sectionId: number): void {
    this.router.navigate(['/sections', sectionId, 'edit'], { 
      queryParams: { 
        classId: this.selectedClassId,
        academicYearId: this.selectedAcademicYearId 
      } 
    });
  }

  deleteSection(sectionId: number, sectionName: string): void {
    if (confirm(`Are you sure you want to delete ${sectionName}?`)) {
      this.sectionMasterService.deleteSection(sectionId.toString()).subscribe({
        next: () => {
          this.snackBar.open('Section deleted successfully', 'Close', { duration: 3000 });
          this.loadSections();
        },
        error: (error) => {
          console.error('Error deleting section:', error);
          this.snackBar.open('Error deleting section', 'Close', { duration: 3000 });
        }
      });
    }
  }
}
