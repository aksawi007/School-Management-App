import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ClassMasterService, ClassMasterResponse, AcademicYearService, AcademicYearResponse } from 'sma-shared-lib';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-class-list',
  templateUrl: './class-list.component.html',
  styleUrls: ['./class-list.component.scss']
})
export class ClassListComponent implements OnInit {
  classes: ClassMasterResponse[] = [];
  allClasses: ClassMasterResponse[] = [];
  academicYears: AcademicYearResponse[] = [];
  selectedAcademicYearId?: number;
  displayedColumns: string[] = ['classCode', 'className', 'academicYearName', 'displayOrder', 'description', 'actions'];
  loading = true;
  schoolId: number = 0;

  constructor(
    private classMasterService: ClassMasterService,
    private academicYearService: AcademicYearService,
    private router: Router,
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
          this.loadAcademicYears();
          this.loadClasses();
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
        // Auto-select current academic year
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
    this.loading = true;
    this.classMasterService.getAllClassesBySchool(this.schoolId).subscribe({
      next: (classes) => {
        this.allClasses = classes.sort((a, b) => (a.displayOrder || 0) - (b.displayOrder || 0));
        this.filterClassesByAcademicYear();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading classes:', error);
        this.snackBar.open('Error loading classes', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  onAcademicYearChange(): void {
    this.filterClassesByAcademicYear();
  }

  filterClassesByAcademicYear(): void {
    if (this.selectedAcademicYearId) {
      this.classes = this.allClasses.filter(c => c.academicYearId === this.selectedAcademicYearId);
    } else {
      this.classes = [];
    }
  }

  addClass(): void {
    if (!this.selectedAcademicYearId) {
      this.snackBar.open('Please select an academic year first', 'Close', { duration: 3000 });
      return;
    }
    this.router.navigate(['/classes/new'], { queryParams: { academicYearId: this.selectedAcademicYearId } });
  }

  editClass(classId: number): void {
    this.router.navigate(['/classes', classId, 'edit']);
  }

  deleteClass(classId: number, className: string): void {
    if (confirm(`Are you sure you want to delete ${className}?`)) {
      this.classMasterService.deleteClass(classId.toString()).subscribe({
        next: () => {
          this.snackBar.open('Class deleted successfully', 'Close', { duration: 3000 });
          this.loadClasses();
        },
        error: (error) => {
          console.error('Error deleting class:', error);
          this.snackBar.open('Error deleting class', 'Close', { duration: 3000 });
        }
      });
    }
  }

  manageSections(classId: number): void {
    this.router.navigate(['/sections'], { 
      queryParams: { 
        classId: classId,
        academicYearId: this.selectedAcademicYearId 
      } 
    });
  }
}
