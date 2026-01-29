import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { StaffService, StaffRequest, StaffResponse, EmploymentType, StaffStatus, DEPARTMENT_TYPE, INDIAN_STATES, DepartmentService, DepartmentResponse } from 'sma-shared-lib';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ReplaySubject, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

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
export class StaffFormComponent implements OnInit, OnDestroy {
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
  departments: DepartmentResponse[] = [];
  loadingDepartments = false;
  
  // Department search
  departmentSearchCtrl = new FormControl();
  filteredDepartments: ReplaySubject<DepartmentResponse[]> = new ReplaySubject<DepartmentResponse[]>(1);
  protected _onDestroy = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private staffService: StaffService,
    private departmentService: DepartmentService,
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

    // Listen to department search input
    this.departmentSearchCtrl.valueChanges
      .pipe(takeUntil(this._onDestroy))
      .subscribe(() => {
        this.filterDepartments();
      });
  }

  ngOnDestroy(): void {
    this._onDestroy.next();
    this._onDestroy.complete();
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
      departmentIds: [[]],
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

    // Listen for staff type changes to load departments
    this.staffForm.get('staffType')?.valueChanges.subscribe(staffType => {
      if (staffType && this.schoolId) {
        this.loadDepartmentsByType(staffType);
      }
    });
  }

  loadDepartmentsByType(staffType: string): void {
    if (!this.schoolId) return;

    this.loadingDepartments = true;
    this.departments = [];
    this.staffForm.patchValue({ departmentIds: [] });

    this.departmentService.getAllDepartments(this.schoolId).subscribe({
      next: (departments) => {
        this.departments = departments;
        this.filteredDepartments.next(departments.slice());
        this.loadingDepartments = false;
      },
      error: (error) => {
        console.error('Error loading departments:', error);
        this.loadingDepartments = false;
      }
    });
  }

  filterDepartments(): void {
    if (!this.departments) {
      return;
    }
    
    let search = this.departmentSearchCtrl.value;
    if (!search) {
      this.filteredDepartments.next(this.departments.slice());
      return;
    }
    
    search = search.toLowerCase();
    this.filteredDepartments.next(
      this.departments.filter(dept => 
        dept.departmentName.toLowerCase().includes(search) ||
        dept.departmentCode.toLowerCase().includes(search)
      )
    );
  }

  getDepartmentName(departmentId: number): string {
    const dept = this.departments.find(d => d.departmentId === departmentId);
    return dept ? `${dept.departmentCode} - ${dept.departmentName}` : '';
  }

  loadStaff(): void {
    if (!this.staffId) return;

    this.loading = true;
    this.staffService.getStaff(this.staffId).subscribe({
      next: (staff) => {
        // Load departments first if staff type is set
        if (staff.staffType && this.schoolId) {
          this.loadDepartmentsByType(staff.staffType);
        }

        // Use departmentIds array directly from response
        const departmentIds = staff.departmentIds || [];

        this.staffForm.patchValue({
          ...staff,
          departmentIds: departmentIds,
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
    
    // Format dates to ISO string and send departmentIds array
    const departmentIds = formValue.departmentIds || [];
    const staffRequest: StaffRequest = {
      ...formValue,
      departmentIds: departmentIds, // Send as array
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
