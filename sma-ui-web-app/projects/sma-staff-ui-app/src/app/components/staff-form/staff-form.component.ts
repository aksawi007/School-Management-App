import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { StaffService, StaffRequest, StaffResponse, StaffType } from 'sma-shared-lib';
import { MatSnackBar } from '@angular/material/snack-bar';
import { INDIAN_STATES } from 'sma-shared-lib';

interface SchoolContext {
  schoolId: number;
  schoolName: string;
  schoolCode: string;
}

@Component({
  selector: 'app-staff-form',
  templateUrl: './staff-form.component.html',
  styleUrls: ['./staff-form.component.scss']
})
export class StaffFormComponent implements OnInit {
  staffForm!: FormGroup;
  isEditMode = false;
  staffId?: number;
  loading = false;
  selectedSchool: SchoolContext | null = null;
  schoolId = 0;

  staffTypes = Object.values(StaffType);
  genders = ['MALE', 'FEMALE', 'OTHER'];
  states = INDIAN_STATES;
  statusOptions = ['ACTIVE', 'INACTIVE', 'ON_LEAVE'];

  constructor(
    private fb: FormBuilder,
    private staffService: StaffService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar
  ) {
    this.createForm();
  }

  ngOnInit(): void {
    // Listen for school context from parent window
    window.addEventListener('message', (event) => {
      if (event.origin !== 'http://localhost:4300') {
        return;
      }

      if (event.data && event.data.type === 'SCHOOL_CONTEXT') {
        console.log('Received school context:', event.data);
        this.selectedSchool = event.data.school;
        
        if (this.selectedSchool) {
          this.schoolId = this.selectedSchool.schoolId;
          this.staffForm.patchValue({ schoolId: this.schoolId });
          
          // Load staff data if in edit mode
          if (this.isEditMode && this.staffId) {
            this.loadStaff();
          }
        }
      }
    });

    // Request context from parent
    if (window.parent && window.parent !== window) {
      window.parent.postMessage({ type: 'REQUEST_CONTEXT' }, 'http://localhost:4300');
    }

    // Check if edit mode
    const id = this.route.snapshot.paramMap.get('id');
    if (id && id !== 'new') {
      this.isEditMode = true;
      this.staffId = parseInt(id);
    }
  }

  createForm(): void {
    this.staffForm = this.fb.group({
      schoolId: [0, Validators.required],
      employeeCode: [''],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
      dateOfBirth: [''],
      gender: [''],
      address: [''],
      city: [''],
      state: [''],
      country: ['India'],
      postalCode: [''],
      staffType: [StaffType.TEACHING, Validators.required],
      departmentId: [''],
      designation: [''],
      qualifications: [''],
      joiningDate: [''],
      salary: [''],
      status: ['ACTIVE']
    });
  }

  loadStaff(): void {
    if (!this.staffId) return;

    this.loading = true;
    this.staffService.getStaff(this.staffId).subscribe({
      next: (staff) => {
        this.staffForm.patchValue({
          schoolId: staff.schoolId,
          employeeCode: staff.employeeCode,
          firstName: staff.firstName,
          lastName: staff.lastName,
          email: staff.email,
          phoneNumber: staff.phoneNumber,
          dateOfBirth: staff.dateOfBirth,
          gender: staff.gender,
          address: staff.address,
          city: staff.city,
          state: staff.state,
          country: staff.country,
          postalCode: staff.postalCode,
          staffType: staff.staffType,
          departmentId: staff.departmentId,
          designation: staff.designation,
          qualifications: staff.qualifications,
          joiningDate: staff.joiningDate,
          salary: staff.salary,
          status: staff.status
        });
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading staff:', error);
        this.snackBar.open('Error loading staff details', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.staffForm.invalid) {
      this.snackBar.open('Please fill in all required fields', 'Close', { duration: 3000 });
      return;
    }

    this.loading = true;
    const formValue = this.staffForm.value;
    
    // Remove status field from request as it might not be in StaffRequest
    const { status, ...staffRequest } = formValue;

    if (this.isEditMode && this.staffId) {
      this.staffService.updateStaff(this.staffId, staffRequest).subscribe({
        next: () => {
          this.snackBar.open('Staff member updated successfully', 'Close', { duration: 3000 });
          this.router.navigate(['/staff']);
        },
        error: (error) => {
          console.error('Error updating staff:', error);
          this.snackBar.open('Error updating staff member', 'Close', { duration: 3000 });
          this.loading = false;
        }
      });
    } else {
      this.staffService.createStaff(staffRequest).subscribe({
        next: () => {
          this.snackBar.open('Staff member created successfully', 'Close', { duration: 3000 });
          this.router.navigate(['/staff']);
        },
        error: (error) => {
          console.error('Error creating staff:', error);
          this.snackBar.open('Error creating staff member', 'Close', { duration: 3000 });
          this.loading = false;
        }
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/staff']);
  }
}
