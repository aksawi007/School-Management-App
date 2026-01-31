import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SectionMasterService, SectionMasterRequest, ClassMasterService, ClassMasterResponse, AcademicYearResponse } from 'sma-shared-lib';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AdminCacheService } from '../../services/admin-cache.service';

@Component({
  selector: 'app-section-form',
  templateUrl: './section-form.component.html',
  styleUrls: ['./section-form.component.scss']
})
export class SectionFormComponent implements OnInit {
  sectionForm!: FormGroup;
  isEditMode = false;
  sectionId?: string;
  loading = false;
  schoolId: number = 0;
  classes: ClassMasterResponse[] = [];
  allClasses: ClassMasterResponse[] = [];
  academicYears: AcademicYearResponse[] = [];
  selectedAcademicYearId?: number;
  selectedClassId?: string;

  constructor(
    private fb: FormBuilder,
    private sectionMasterService: SectionMasterService,
    private classMasterService: ClassMasterService,
    private adminCache: AdminCacheService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar
  ) {
    this.createForm();
  }

  ngOnInit(): void {
    this.sectionId = this.route.snapshot.paramMap.get('id') || undefined;
    this.isEditMode = !!this.sectionId;
    this.selectedClassId = this.route.snapshot.queryParamMap.get('classId') || undefined;

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
          this.sectionForm.patchValue({ schoolId: this.schoolId });
          
          this.loadAcademicYears();
          this.loadClasses();

          // Pre-select academic year from query params
          const academicYearId = this.route.snapshot.queryParamMap.get('academicYearId');
          if (academicYearId) {
            this.selectedAcademicYearId = parseInt(academicYearId, 10);
            setTimeout(() => this.filterClassesByAcademicYear(), 100);
          }

          if (this.isEditMode && this.sectionId) {
            this.loadSection();
          } else if (this.selectedClassId) {
            this.sectionForm.patchValue({ classId: this.selectedClassId });
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
    this.sectionForm = this.fb.group({
      schoolId: [this.schoolId, Validators.required],
      classId: ['', Validators.required],
      sectionCode: ['', Validators.required],
      sectionName: ['', Validators.required],
      capacity: [''],
      roomNumber: [''],
      description: ['']
    });
  }

  loadAcademicYears(): void {
    this.adminCache.getAcademicYears().subscribe({
      next: (years) => {
        this.academicYears = years;
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
    this.sectionForm.patchValue({ classId: null });
    this.filterClassesByAcademicYear();
  }

  filterClassesByAcademicYear(): void {
    if (this.selectedAcademicYearId) {
      this.classes = this.allClasses.filter(c => c.academicYearId === this.selectedAcademicYearId);
    } else {
      this.classes = [];
    }
  }

  loadSection(): void {
    if (!this.sectionId) return;

    this.loading = true;
    this.sectionMasterService.getSection(this.sectionId).subscribe({
      next: (section) => {
        this.sectionForm.patchValue({
          schoolId: section.schoolId,
          classId: section.classId,
          sectionCode: section.sectionCode,
          sectionName: section.sectionName,
          capacity: section.capacity,
          roomNumber: section.roomNumber,
          description: section.description
        });
        this.selectedClassId = section.classId?.toString();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading section:', error);
        this.snackBar.open('Error loading section details', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.sectionForm.invalid) {
      this.snackBar.open('Please fill in all required fields', 'Close', { duration: 3000 });
      return;
    }

    this.loading = true;
    const request: SectionMasterRequest = this.sectionForm.value;

    if (this.isEditMode && this.sectionId) {
      this.sectionMasterService.updateSection(this.sectionId, request).subscribe({
        next: () => {
          this.snackBar.open('Section updated successfully', 'Close', { duration: 3000 });
          this.router.navigate(['/sections'], { queryParams: { classId: this.selectedClassId } });
        },
        error: (error) => {
          console.error('Error updating section:', error);
          this.snackBar.open('Error updating section', 'Close', { duration: 3000 });
          this.loading = false;
        }
      });
    } else {
      this.sectionMasterService.createSection(request).subscribe({
        next: () => {
          this.snackBar.open('Section created successfully', 'Close', { duration: 3000 });
          this.router.navigate(['/sections'], { queryParams: { classId: this.selectedClassId } });
        },
        error: (error) => {
          console.error('Error creating section:', error);
          this.snackBar.open('Error creating section', 'Close', { duration: 3000 });
          this.loading = false;
        }
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/sections'], { queryParams: { classId: this.selectedClassId } });
  }
}
