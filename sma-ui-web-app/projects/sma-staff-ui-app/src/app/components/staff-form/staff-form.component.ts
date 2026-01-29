import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { StaffService, StaffRequest, StaffResponse, EmploymentType, StaffStatus, DEPARTMENT_TYPE, INDIAN_STATES } from 'sma-shared-lib';
import { MatSnackBar } from '@angular/material/snack-bar';

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

  staffTypes = Object.values(DEPARTMENT_TYPE);
  employmentTypes = Object.values(EmploymentType);
  staffStatuses = Object.values(StaffStatus);
  genders = ['MALE', 'FEMALE', 'OTHER'];
  bloodGroups = ['A+', 'A-', 'B+', 'B-', 'O+', 'O-', 'AB+', 'AB-'];
  states = INDIAN_STATES;

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
      middleName: [''],
      lastName: ['', Validators.required],
      dateOfBirth: [''],
      gender: [''],
      bloodGroup: [''],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
      addressLine1: [''],
      addressLine2: [''],
      city: [''],
      state: [''],
      postalCode: ['', [Validators.pattern(/^\d{6}$/)]],
      staffType: [DEPARTMENT_TYPE.ACADEMIC, Validators.required],
      designation: [''],
      departmentId: [''],
      qualification: [''],
      specialization: [''],
      experienceYears: [0],
      joiningDate: [''],
      employmentType: [EmploymentType.PERMANENT],
      salary: [0],
      staffStatus: [StaffStatus.ACTIVE],
      photoUrl: [''],
      aadharNumber: ['', [Validators.pattern(/^\d{12}$/)]],
      panNumber: ['', [Validators.pattern(/^[A-Z]{5}[0-9]{4}[A-Z]{1}$/)]],
      bankAccountNumber: [''],
      bankName: [''],
      bankIfscCode: ['', [Validators.pattern(/^[A-Z]{4}0[A-Z0-9]{6}$/)]]
    });
  }

  loadStaff(): void {
    if (!this.staffId) return;

    this.loading = true;
    this.staffService.getStaff(this.staffId).subscribe({
      next: (staff) => {
        this.staffForm.patchValue({
          ...staff,
          dateOfBirth: staff.dateOfBirth ? new Date(staff.dateOfBirth) : null,
          joiningDate: staff.joiningDate ? new Date(staff.joiningDate) : null
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
    
    // Format dates to ISO string
    const staffRequest: StaffRequest = {
      ...formValue,
      dateOfBirth: formValue.dateOfBirth ? new Date(formValue.dateOfBirth).toISOString().split('T')[0] : undefined,
      joiningDate: formValue.joiningDate ? new Date(formValue.joiningDate).toISOString().split('T')[0] : undefined
    };

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
