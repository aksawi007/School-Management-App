import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import {
  StudentClassSectionService,
  StudentClassSectionResponse,
  AcademicYearResponse,
  ClassMasterService,
  ClassMasterResponse,
  SectionMasterService,
  SectionMasterResponse
} from 'sma-shared-lib';
import { StudentClassAssignDialogComponent } from '../student-class-assign-dialog/student-class-assign-dialog.component';
import { AcademicYearCacheService } from '../../services/academic-year-cache.service';

@Component({
  selector: 'app-student-class-management',
  templateUrl: './student-class-management.component.html',
  styleUrls: ['./student-class-management.component.scss']
})
export class StudentClassManagementComponent implements OnInit {
  students: StudentClassSectionResponse[] = [];
  academicYears: AcademicYearResponse[] = [];
  classes: ClassMasterResponse[] = [];
  sections: SectionMasterResponse[] = [];
  
  selectedAcademicYearId?: number;
  selectedClassId?: number;
  selectedSectionId?: number;
  
  displayedColumns: string[] = ['admissionNumber', 'studentName', 'rollNumber', 'className', 'sectionName', 'enrollmentDate', 'actions'];
  loading = false;
  schoolId: number = 0;

  constructor(
    private studentClassSectionService: StudentClassSectionService,
    private academicYearCache: AcademicYearCacheService,
    private classMasterService: ClassMasterService,
    private sectionService: SectionMasterService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    window.addEventListener('message', (event) => {
      if (event.origin !== 'http://localhost:4300') return;
      
      if (event.data && event.data.type === 'SCHOOL_CONTEXT') {
        const school = event.data.school;
        if (school) {
          this.schoolId = school.schoolId;
          this.loadAcademicYears();
        }
      }
    });
    
    if (window.parent && window.parent !== window) {
      window.parent.postMessage({ type: 'REQUEST_CONTEXT' }, 'http://localhost:4300');
    }
  }

  loadAcademicYears(): void {
    this.academicYearCache.getAcademicYears().subscribe({
      next: (years) => {
        this.academicYears = years;
        const currentYear = years.find(y => y.currentYear === true);
        if (currentYear) {
          this.selectedAcademicYearId = currentYear.yearId;
          this.onAcademicYearChange();
        }
      },
      error: (error) => {
        console.error('Error loading academic years:', error);
        this.snackBar.open('Error loading academic years', 'Close', { duration: 3000 });
      }
    });
  }

  onAcademicYearChange(): void {
    this.classes = [];
    this.sections = [];
    this.students = [];
    this.selectedClassId = undefined;
    this.selectedSectionId = undefined;
    
    if (this.selectedAcademicYearId) {
      this.loadClasses();
    }
  }

  loadClasses(): void {
    if (!this.selectedAcademicYearId) return;
    
    this.classMasterService.getAllClassesByAcademicYear(this.selectedAcademicYearId).subscribe({
      next: (classes) => {
        this.classes = classes.sort((a, b) => (a.displayOrder || 0) - (b.displayOrder || 0));
      },
      error: (error) => {
        console.error('Error loading classes:', error);
        this.snackBar.open('Error loading classes', 'Close', { duration: 3000 });
      }
    });
  }

  onClassChange(): void {
    this.sections = [];
    this.students = [];
    this.selectedSectionId = undefined;
    
    if (this.selectedClassId) {
      this.loadSections();
      this.loadStudents();
    }
  }

  loadSections(): void {
    if (!this.selectedClassId) return;
    
    this.sectionService.getSectionsByClass(this.schoolId, this.selectedClassId.toString()).subscribe({
      next: (sections: SectionMasterResponse[]) => {
        this.sections = sections.sort((a: SectionMasterResponse, b: SectionMasterResponse) => 
          (a.sectionCode || '').localeCompare(b.sectionCode || ''));
      },
      error: (error: any) => {
        console.error('Error loading sections:', error);
        this.snackBar.open('Error loading sections', 'Close', { duration: 3000 });
      }
    });
  }

  onSectionChange(): void {
    this.loadStudents();
  }

  loadStudents(): void {
    if (!this.selectedAcademicYearId || !this.selectedClassId) return;
    
    this.loading = true;
    this.studentClassSectionService.getStudentsByClassAndSection(
      this.selectedAcademicYearId,
      this.selectedClassId,
      this.selectedSectionId
    ).subscribe({
      next: (students) => {
        this.students = students;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading students:', error);
        this.snackBar.open('Error loading students', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  assignStudent(): void {
    if (!this.selectedAcademicYearId || !this.selectedClassId) {
      this.snackBar.open('Please select academic year and class first', 'Close', { duration: 3000 });
      return;
    }

    const dialogRef = this.dialog.open(StudentClassAssignDialogComponent, {
      width: '600px',
      data: {
        schoolId: this.schoolId,
        academicYearId: this.selectedAcademicYearId,
        classId: this.selectedClassId,
        sectionId: this.selectedSectionId,
        classes: this.classes,
        sections: this.sections
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadStudents();
      }
    });
  }

  updateStudent(student: StudentClassSectionResponse): void {
    const dialogRef = this.dialog.open(StudentClassAssignDialogComponent, {
      width: '600px',
      data: {
        schoolId: this.schoolId,
        academicYearId: this.selectedAcademicYearId,
        classId: this.selectedClassId,
        sectionId: this.selectedSectionId,
        classes: this.classes,
        sections: this.sections,
        student: student,
        isEdit: true
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadStudents();
      }
    });
  }

  removeStudent(student: StudentClassSectionResponse): void {
    if (confirm(`Remove ${student.studentName} from ${student.className} - ${student.sectionName}?`)) {
      this.studentClassSectionService.deactivateStudentAssignment(student.mappingId).subscribe({
        next: () => {
          this.snackBar.open('Student removed successfully', 'Close', { duration: 3000 });
          this.loadStudents();
        },
        error: (error) => {
          console.error('Error removing student:', error);
          this.snackBar.open('Error removing student', 'Close', { duration: 3000 });
        }
      });
    }
  }

  formatDate(dateString?: string): string {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
  }
}
