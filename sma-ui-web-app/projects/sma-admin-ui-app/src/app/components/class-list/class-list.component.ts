import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ClassMasterService, ClassMasterResponse, AcademicYearResponse, StudentClassSectionService, StudentClassSectionResponse } from 'sma-shared-lib';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { ClassStudentsDialogComponent } from '../class-students-dialog/class-students-dialog.component';
import { AdminCacheService } from '../../services/admin-cache.service';

@Component({
  selector: 'app-class-list',
  templateUrl: './class-list.component.html',
  styleUrls: ['./class-list.component.scss']
})
export class ClassListComponent implements OnInit {
  classes: ClassMasterResponse[] = [];
  academicYears: AcademicYearResponse[] = [];
  selectedAcademicYearId?: number;
  displayedColumns: string[] = ['classCode', 'className', 'academicYearName', 'displayOrder', 'description', 'studentCount', 'actions'];
  loading = true;
  schoolId: number = 0;

  constructor(
    private classMasterService: ClassMasterService,
    private adminCache: AdminCacheService,
    private studentClassSectionService: StudentClassSectionService,
    private router: Router,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
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
        }
      }
    });
    
    // Request context from parent
    if (window.parent && window.parent !== window) {
      window.parent.postMessage({ type: 'REQUEST_CONTEXT' }, 'http://localhost:4300');
    }
  }

  loadAcademicYears(): void {
    this.adminCache.getAcademicYears().subscribe({
      next: (years) => {
        this.academicYears = years;
        // Auto-select current academic year
        const currentYear = years.find(y => y.currentYear === true);
        if (currentYear && !this.selectedAcademicYearId) {
          this.selectedAcademicYearId = currentYear.yearId;
          this.loadClassesByAcademicYear();
        }
      },
      error: (error) => {
        console.error('Error loading academic years:', error);
        this.snackBar.open('Error loading academic years', 'Close', { duration: 3000 });
      }
    });
  }

  loadClassesByAcademicYear(): void {
    if (!this.selectedAcademicYearId) {
      this.classes = [];
      this.loading = false;
      return;
    }

    this.loading = true;
    this.classMasterService.getAllClassesByAcademicYear(this.selectedAcademicYearId).subscribe({
      next: (classes) => {
        this.classes = classes.sort((a, b) => (a.displayOrder || 0) - (b.displayOrder || 0));
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
    this.loadClassesByAcademicYear();
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
          this.loadClassesByAcademicYear();
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

  viewStudents(classItem: ClassMasterResponse): void {
    this.dialog.open(ClassStudentsDialogComponent, {
      width: '800px',
      data: {
        academicYearId: this.selectedAcademicYearId,
        classId: classItem.id,
        className: classItem.className,
        schoolId: this.schoolId,
        openedFromClass: true
      }
    });
  }
}
