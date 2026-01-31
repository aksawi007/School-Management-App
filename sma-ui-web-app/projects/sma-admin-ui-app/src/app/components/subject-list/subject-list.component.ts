import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SubjectMasterService, SubjectMasterResponse, ClassMasterService, ClassMasterResponse } from 'sma-shared-lib';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-subject-list',
  templateUrl: './subject-list.component.html',
  styleUrls: ['./subject-list.component.scss']
})
export class SubjectListComponent implements OnInit {
  subjects: SubjectMasterResponse[] = [];
  filteredSubjects: SubjectMasterResponse[] = [];
  classes: ClassMasterResponse[] = [];
  selectedType = 'ALL';
  selectedClassId?: number;
  subjectTypes = ['ALL', 'CORE', 'ELECTIVE', 'OPTIONAL', 'EXTRA_CURRICULAR'];
  displayedColumns: string[] = ['subjectCode', 'subjectName', 'className', 'departmentName', 'subjectType', 'credits', 'maxMarks', 'isMandatory', 'actions'];
  loading = true;
  schoolId: number = 0;

  constructor(
    private subjectMasterService: SubjectMasterService,
    private classMasterService: ClassMasterService,
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
          this.loadClasses();
          this.loadSubjects();
        }
      }
    });
    
    // Request context from parent
    if (window.parent && window.parent !== window) {
      window.parent.postMessage({ type: 'REQUEST_CONTEXT' }, 'http://localhost:4300');
    }
  }

  loadClasses(): void {
    this.classMasterService.getAllClassesBySchool(this.schoolId).subscribe({
      next: (classes) => {
        this.classes = classes.sort((a, b) => (a.displayOrder || 0) - (b.displayOrder || 0));
      },
      error: (error) => {
        console.error('Error loading classes:', error);
      }
    });
  }

  loadSubjects(): void {
    this.loading = true;
    this.subjectMasterService.getAllSubjectsBySchool(this.schoolId).subscribe({
      next: (subjects) => {
        this.subjects = subjects;
        this.applyFilter();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading subjects:', error);
        this.snackBar.open('Error loading subjects', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  onTypeChange(): void {
    this.applyFilter();
  }

  onClassChange(): void {
    this.applyFilter();
  }

  applyFilter(): void {
    let filtered = this.subjects;
    
    // Filter by type
    if (this.selectedType !== 'ALL') {
      filtered = filtered.filter(s => s.subjectType === this.selectedType);
    }
    
    // Filter by class
    if (this.selectedClassId) {
      filtered = filtered.filter(s => s.classId === this.selectedClassId?.toString());
    }
    
    this.filteredSubjects = filtered;
  }

  addSubject(): void {
    this.router.navigate(['/subjects/new']);
  }

  editSubject(subjectId: number): void {
    this.router.navigate(['/subjects', subjectId, 'edit']);
  }

  deleteSubject(subjectId: number, subjectName: string): void {
    if (confirm(`Are you sure you want to delete ${subjectName}?`)) {
      this.subjectMasterService.deleteSubject(subjectId.toString()).subscribe({
        next: () => {
          this.snackBar.open('Subject deleted successfully', 'Close', { duration: 3000 });
          this.loadSubjects();
        },
        error: (error) => {
          console.error('Error deleting subject:', error);
          this.snackBar.open('Error deleting subject', 'Close', { duration: 3000 });
        }
      });
    }
  }
}
