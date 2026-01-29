import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { DepartmentService, DepartmentResponse, DepartmentRequest, DEPARTMENT_TYPE, StaffService, StaffResponse } from 'sma-shared-lib';

@Component({
  selector: 'app-department-form',
  templateUrl: './department-form.component.html',
  styleUrls: ['./department-form.component.scss']
})
export class DepartmentFormComponent implements OnInit {
  departmentForm: FormGroup;
  loading = false;
  isEditMode = false;
  departmentId: number | null = null;
  schoolId: number | null = null;
  departmentTypes = Object.values(DEPARTMENT_TYPE);
  staffList: StaffResponse[] = [];
  loadingStaff = false;

  constructor(
    private fb: FormBuilder,
    private departmentService: DepartmentService,
    private staffService: StaffService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.departmentForm = this.fb.group({
      departmentCode: ['', [Validators.required, Validators.maxLength(20)]],
      departmentName: ['', [Validators.required, Validators.maxLength(100)]],
      departmentType: ['', Validators.required],
      hodStaffId: [null],
      description: ['', Validators.maxLength(500)]
    });
  }

  ngOnInit(): void {
    // Listen for school context from parent window
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
          
          // Check for edit mode
          this.route.paramMap.subscribe(params => {
            const id = params.get('id');
            if (id) {
              this.isEditMode = true;
              this.departmentId = Number(id);
              this.loadDepartmentIfEditMode();
            }
          });
        }
      }
    });
    
    // Request context from parent
    if (window.parent && window.parent !== window) {
      window.parent.postMessage({ type: 'REQUEST_CONTEXT' }, 'http://localhost:4300');
    }

    // Listen for department type changes
    this.departmentForm.get('departmentType')?.valueChanges.subscribe(type => {
      if (type && this.schoolId) {
        this.loadStaffByType(type);
      }
    });
  }

  loadStaffByType(departmentType: string): void {
    if (!this.schoolId) return;
    
    this.loadingStaff = true;
    this.staffList = [];
    this.departmentForm.patchValue({ hodStaffId: null });
    
    this.staffService.getStaffByType(this.schoolId, departmentType as any).subscribe({
      next: (staff: StaffResponse[]) => {
        this.staffList = staff;
        this.loadingStaff = false;
      },
      error: (error: any) => {
        console.error('Error loading staff:', error);
        this.loadingStaff = false;
      }
    });
  }

  loadDepartmentIfEditMode(): void {
    if (this.isEditMode && this.departmentId && this.schoolId) {
      this.loading = true;
      this.departmentService.getDepartment(this.departmentId).subscribe({
        next: (department: DepartmentResponse) => {
          this.departmentForm.patchValue({
            departmentCode: department.departmentCode,
            departmentName: department.departmentName,
            departmentType: department.departmentType,
            hodStaffId: department.hodStaffId,
            description: department.description
          });
          
          // Load staff list if department type is set
          if (department.departmentType) {
            this.loadStaffByType(department.departmentType);
          }
          
          this.loading = false;
        },
        error: (error: any) => {
          console.error('Error loading department:', error);
          alert('Failed to load department details');
          this.loading = false;
        }
      });
    }
  }

  onSubmit(): void {
    if (this.departmentForm.invalid) {
      this.departmentForm.markAllAsTouched();
      return;
    }

    if (!this.schoolId) {
      alert('School context not available. Please try again.');
      return;
    }

    this.loading = true;
    const formValue = this.departmentForm.value;
    const request: DepartmentRequest = {
      schoolId: this.schoolId!,
      departmentCode: formValue.departmentCode,
      departmentName: formValue.departmentName,
      departmentType: formValue.departmentType,
      hodStaffId: formValue.hodStaffId || undefined,
      description: formValue.description || undefined
    };

    if (this.isEditMode && this.departmentId) {
      this.departmentService.updateDepartment(this.departmentId, request).subscribe({
        next: () => {
          alert('Department updated successfully');
          this.goBack();
        },
        error: (error: any) => {
          console.error('Error updating department:', error);
          alert('Failed to update department: ' + (error.error?.message || error.message));
          this.loading = false;
        }
      });
    } else {
      this.departmentService.createDepartment(request).subscribe({
        next: () => {
          alert('Department created successfully');
          this.goBack();
        },
        error: (error: any) => {
          console.error('Error creating department:', error);
          alert('Failed to create department: ' + (error.error?.message || error.message));
          this.loading = false;
        }
      });
    }
  }

  goBack(): void {
    this.router.navigate(['../'], { relativeTo: this.route });
  }

  hasError(field: string, error: string): boolean {
    const control = this.departmentForm.get(field);
    return !!(control && control.hasError(error) && (control.dirty || control.touched));
  }
}
