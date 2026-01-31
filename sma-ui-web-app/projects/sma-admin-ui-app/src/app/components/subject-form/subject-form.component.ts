import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SubjectMasterService, SubjectMasterRequest, ClassMasterResponse, DepartmentService, DepartmentResponse } from 'sma-shared-lib';
import { AdminCacheService } from '../../services/admin-cache.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-subject-form',
  templateUrl: './subject-form.component.html',
  styleUrls: ['./subject-form.component.scss']
})
export class SubjectFormComponent implements OnInit {
  subjectForm!: FormGroup;
  isEditMode = false;
  subjectId?: string;
  loading = false;
  schoolId: number = 0;
  classes: ClassMasterResponse[] = [];
  departments: DepartmentResponse[] = [];
  subjectTypes = ['CORE', 'ELECTIVE', 'OPTIONAL', 'EXTRA_CURRICULAR'];

  constructor(
    private fb: FormBuilder,
    private subjectMasterService: SubjectMasterService,
    private adminCache: AdminCacheService,
    private departmentService: DepartmentService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar
  ) {
    this.createForm();
  }

  ngOnInit(): void {
    this.subjectId = this.route.snapshot.paramMap.get('id') || undefined;
    this.isEditMode = !!this.subjectId;

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
          this.subjectForm.patchValue({ schoolId: this.schoolId });
          
          // Load classes and departments for the school
          this.loadClasses();
          this.loadDepartments();
          
          if (this.isEditMode && this.subjectId) {
            this.loadSubject();
          }
        }
      }
    });
    
    // Request context from parent
    if (window.parent && window.parent !== window) {
      window.parent.postMessage({ type: 'REQUEST_CONTEXT' }, 'http://localhost:4300');
    }
  }

  createForm(): void {
    this.subjectForm = this.fb.group({
      schoolId: [this.schoolId, Validators.required],
      classId: ['', Validators.required],
      departmentId: [''],
      subjectCode: ['', Validators.required],
      subjectName: ['', Validators.required],
      subjectType: ['CORE', Validators.required],
      isMandatory: [true],
      credits: [''],
      maxMarks: [''],
      description: ['']
    });
  }

  loadClasses(): void {
    this.adminCache.getClasses().subscribe({
      next: (classes) => {
        this.classes = classes;
      },
      error: (error) => {
        console.error('Error loading classes:', error);
        this.snackBar.open('Error loading classes', 'Close', { duration: 3000 });
      }
    });
  }

  loadDepartments(): void {
    this.departmentService.getDepartmentsBySchool(this.schoolId).subscribe({
      next: (departments) => {
        this.departments = departments;
      },
      error: (error) => {
        console.error('Error loading departments:', error);
        this.snackBar.open('Error loading departments', 'Close', { duration: 3000 });
      }
    });
  }

  loadSubject(): void {
    if (!this.subjectId) return;

    this.loading = true;
    this.subjectMasterService.getSubject(this.subjectId).subscribe({
      next: (subject) => {
        this.subjectForm.patchValue({
          schoolId: subject.schoolId,
          classId: subject.classId,
          departmentId: subject.departmentId,
          subjectCode: subject.subjectCode,
          subjectName: subject.subjectName,
          subjectType: subject.subjectType,
          isMandatory: subject.isMandatory,
          credits: subject.credits,
          maxMarks: subject.maxMarks,
          description: subject.description
        });
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading subject:', error);
        this.snackBar.open('Error loading subject details', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.subjectForm.invalid) {
      this.snackBar.open('Please fill in all required fields', 'Close', { duration: 3000 });
      return;
    }

    this.loading = true;
    const request: SubjectMasterRequest = this.subjectForm.value;

    if (this.isEditMode && this.subjectId) {
      this.subjectMasterService.updateSubject(this.subjectId, request).subscribe({
        next: () => {
          this.snackBar.open('Subject updated successfully', 'Close', { duration: 3000 });
          this.router.navigate(['/subjects']);
        },
        error: (error) => {
          console.error('Error updating subject:', error);
          this.snackBar.open('Error updating subject', 'Close', { duration: 3000 });
          this.loading = false;
        }
      });
    } else {
      this.subjectMasterService.createSubject(request).subscribe({
        next: () => {
          this.snackBar.open('Subject created successfully', 'Close', { duration: 3000 });
          this.router.navigate(['/subjects']);
        },
        error: (error) => {
          console.error('Error creating subject:', error);
          this.snackBar.open('Error creating subject', 'Close', { duration: 3000 });
          this.loading = false;
        }
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/subjects']);
  }
}
