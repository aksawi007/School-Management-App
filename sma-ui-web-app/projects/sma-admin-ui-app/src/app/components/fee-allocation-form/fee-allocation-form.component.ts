import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { StudentFeeAllocationService, FeeCategoryService, StudentFeeAllocationRequest, FeeCategoryResponse } from 'sma-shared-lib';

@Component({
  selector: 'app-fee-allocation-form',
  templateUrl: './fee-allocation-form.component.html',
  styleUrls: ['./fee-allocation-form.component.scss']
})
export class FeeAllocationFormComponent implements OnInit {
  allocationForm!: FormGroup;
  loading = false;
  schoolId: number = 0;
  academicYearId: string = '';
  allocatedBy: number = 1; // TODO: Get from auth service
  
  feeCategories: FeeCategoryResponse[] = [];
  durationTypes = ['MONTHLY', 'QUARTERLY', 'HALF_YEARLY', 'ANNUAL', 'ONE_TIME'];
  months = ['JANUARY', 'FEBRUARY', 'MARCH', 'APRIL', 'MAY', 'JUNE', 'JULY', 'AUGUST', 'SEPTEMBER', 'OCTOBER', 'NOVEMBER', 'DECEMBER'];
  quarters = ['Q1', 'Q2', 'Q3', 'Q4'];

  constructor(
    private fb: FormBuilder,
    private feeAllocationService: StudentFeeAllocationService,
    private feeCategoryService: FeeCategoryService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.createForm();
  }

  ngOnInit(): void {
    // Listen for school context from parent window (shell app)
    window.addEventListener('message', (event) => {
      if (event.origin !== 'http://localhost:4300') {
        return;
      }
      
      if (event.data && event.data.type === 'SCHOOL_CONTEXT') {
        console.log('Received school context:', event.data);
        const school = event.data.school;
        const academicYear = event.data.academicYear;
        
        if (school && academicYear) {
          this.schoolId = school.schoolId;
          this.academicYearId = academicYear.id;
          this.loadFeeCategories();
        }
      }
    });
    
    // Request context from parent
    if (window.parent && window.parent !== window) {
      window.parent.postMessage({ type: 'REQUEST_CONTEXT' }, 'http://localhost:4300');
    }
  }

  createForm(): void {
    this.allocationForm = this.fb.group({
      studentId: ['', [Validators.required]],
      feeCategoryId: ['', Validators.required],
      feeAmount: ['', [Validators.required, Validators.min(0.01)]],
      durationType: ['MONTHLY', Validators.required],
      applicableMonth: [''],
      quarter: [''],
      dueDate: ['', Validators.required],
      paymentDeadline: [''],
      isMandatory: [true],
      discountAmount: [0, [Validators.min(0)]],
      discountReason: [''],
      remarks: ['', Validators.maxLength(500)]
    });

    // Watch duration type changes to update validators
    this.allocationForm.get('durationType')?.valueChanges.subscribe(type => {
      this.updateDurationValidators(type);
    });
  }

  loadFeeCategories(): void {
    this.feeCategoryService.getAllActiveFeeCategories(this.schoolId).subscribe({
      next: (categories) => {
        this.feeCategories = categories.sort((a, b) => (a.displayOrder || 0) - (b.displayOrder || 0));
      },
      error: (error: any) => {
        console.error('Error loading fee categories:', error);
        this.snackBar.open('Error loading fee categories', 'Close', { duration: 3000 });
      }
    });
  }

  updateDurationValidators(durationType: string): void {
    const monthControl = this.allocationForm.get('applicableMonth');
    const quarterControl = this.allocationForm.get('quarter');

    // Clear validators
    monthControl?.clearValidators();
    quarterControl?.clearValidators();

    // Set validators based on duration type
    if (durationType === 'MONTHLY') {
      monthControl?.setValidators([Validators.required]);
    } else if (durationType === 'QUARTERLY') {
      quarterControl?.setValidators([Validators.required]);
    }

    monthControl?.updateValueAndValidity();
    quarterControl?.updateValueAndValidity();
  }

  onSubmit(): void {
    if (this.allocationForm.invalid) {
      this.snackBar.open('Please fill all required fields correctly', 'Close', { duration: 3000 });
      Object.keys(this.allocationForm.controls).forEach(key => {
        const control = this.allocationForm.get(key);
        if (control?.invalid) {
          control.markAsTouched();
        }
      });
      return;
    }

    this.loading = true;
    const formValue = this.allocationForm.value;

    // Prepare request
    const request: StudentFeeAllocationRequest = {
      studentId: parseInt(formValue.studentId),
      feeCategoryId: formValue.feeCategoryId,
      feeAmount: parseFloat(formValue.feeAmount),
      durationType: formValue.durationType,
      applicableMonth: formValue.durationType === 'MONTHLY' ? formValue.applicableMonth : undefined,
      quarter: formValue.durationType === 'QUARTERLY' ? formValue.quarter : undefined,
      dueDate: formValue.dueDate,
      paymentDeadline: formValue.paymentDeadline || undefined,
      isMandatory: formValue.isMandatory,
      discountAmount: formValue.discountAmount > 0 ? parseFloat(formValue.discountAmount) : undefined,
      discountReason: formValue.discountAmount > 0 ? formValue.discountReason : undefined,
      remarks: formValue.remarks || undefined
    };

    this.feeAllocationService.allocateFeeToStudent(
      this.schoolId,
      this.academicYearId,
      this.allocatedBy,
      request
    ).subscribe({
      next: () => {
        this.snackBar.open('Fee allocated successfully', 'Close', { duration: 3000 });
        this.router.navigate(['/admin/fee-allocations']);
      },
      error: (error: any) => {
        console.error('Error allocating fee:', error);
        this.snackBar.open(
          error.error?.message || 'Error allocating fee',
          'Close',
          { duration: 3000 }
        );
        this.loading = false;
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/admin/fee-allocations']);
  }

  get f() {
    return this.allocationForm.controls;
  }

  showMonthField(): boolean {
    return this.f['durationType'].value === 'MONTHLY';
  }

  showQuarterField(): boolean {
    return this.f['durationType'].value === 'QUARTERLY';
  }

  showDiscountReason(): boolean {
    const discountAmount = this.f['discountAmount'].value;
    return discountAmount && parseFloat(discountAmount) > 0;
  }
}
