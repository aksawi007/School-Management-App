import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ClassMasterService, ClassMasterRequest, AcademicYearResponse } from 'sma-shared-lib';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AdminCacheService } from '../../services/admin-cache.service';

@Component({
  selector: 'app-class-form',
  templateUrl: './class-form.component.html',
  styleUrls: ['./class-form.component.scss']
})
export class ClassFormComponent implements OnInit {
  classForm!: FormGroup;
  isEditMode = false;
  classId?: string;
  loading = false;
  schoolId: number = 0;
  academicYears: AcademicYearResponse[] = [];

  constructor(
    private fb: FormBuilder,
    private classMasterService: ClassMasterService,
    private adminCache: AdminCacheService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar
  ) {
    this.createForm();
  }

  ngOnInit(): void {
    this.classId = this.route.snapshot.paramMap.get('id') || undefined;
    this.isEditMode = !!this.classId;

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
          this.classForm.patchValue({ schoolId: this.schoolId });
          
          this.loadAcademicYears();

          // Pre-select academic year from query params
          const academicYearId = this.route.snapshot.queryParamMap.get('academicYearId');
          if (academicYearId) {
            this.classForm.patchValue({ academicYearId: parseInt(academicYearId, 10) });
          }

          if (this.isEditMode && this.classId) {
            this.loadClass();
          }
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
      },
      error: (error) => {
        console.error('Error loading academic years:', error);
      }
    });
  }

  createForm(): void {
    this.classForm = this.fb.group({
      schoolId: [this.schoolId, Validators.required],
      academicYearId: ['', Validators.required],
      classCode: ['', Validators.required],
      className: ['', Validators.required],
      displayOrder: [''],
      description: ['']
    });
  }

  loadClass(): void {
    if (!this.classId) return;

    this.loading = true;
    this.classMasterService.getClass(this.classId).subscribe({
      next: (classData) => {
        this.classForm.patchValue({
          schoolId: classData.schoolId,
          academicYearId: classData.academicYearId,
          classCode: classData.classCode,
          className: classData.className,
          displayOrder: classData.displayOrder,
          description: classData.description
        });
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading class:', error);
        this.snackBar.open('Error loading class details', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.classForm.invalid) {
      this.snackBar.open('Please fill in all required fields', 'Close', { duration: 3000 });
      return;
    }

    this.loading = true;
    const request: ClassMasterRequest = this.classForm.value;

    if (this.isEditMode && this.classId) {
      this.classMasterService.updateClass(this.classId, request).subscribe({
        next: () => {
          this.snackBar.open('Class updated successfully', 'Close', { duration: 3000 });
          this.router.navigate(['/classes']);
        },
        error: (error) => {
          console.error('Error updating class:', error);
          this.snackBar.open('Error updating class', 'Close', { duration: 3000 });
          this.loading = false;
        }
      });
    } else {
      this.classMasterService.createClass(request).subscribe({
        next: () => {
          this.snackBar.open('Class created successfully', 'Close', { duration: 3000 });
          this.router.navigate(['/classes']);
        },
        error: (error) => {
          console.error('Error creating class:', error);
          this.snackBar.open('Error creating class', 'Close', { duration: 3000 });
          this.loading = false;
        }
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/classes']);
  }
}
