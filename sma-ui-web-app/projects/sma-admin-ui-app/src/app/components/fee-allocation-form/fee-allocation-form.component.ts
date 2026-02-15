import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FeeCategoryService, FeeCategoryResponse, AcademicYearResponse } from 'sma-shared-lib';
import { AdminCacheService } from '../../services/admin-cache.service';

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
  academicYearName: string = '';
  allocatedBy: number = 1; // TODO: Get from auth service
  
  feeCategories: FeeCategoryResponse[] = [];
  academicYears: AcademicYearResponse[] = [];

  constructor(
    private fb: FormBuilder,
    private feeCategoryService: FeeCategoryService,
    private adminCache: AdminCacheService,
    private snackBar: MatSnackBar,
    public dialogRef: MatDialogRef<FeeAllocationFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { schoolId: number, academicYearId: number }
  ) {
    this.createForm();
  }

  ngOnInit(): void {
    if (this.data) {
      this.schoolId = this.data.schoolId;
      this.loadFeeCategories();
    }
  }

  createForm(): void {
    this.allocationForm = this.fb.group({
      feeCategoryId: ['', Validators.required],
      feeAmount: ['', [Validators.required, Validators.min(0.01)]],
      dueDate: [''],
      remarks: ['', Validators.maxLength(500)]
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

    // TODO: Call academic year fee allocation service
    // For now, just show success message
    setTimeout(() => {
      this.snackBar.open('Academic Year Fee Allocation created successfully', 'Close', { duration: 3000 });
      this.dialogRef.close(true);
      this.loading = false;
    }, 1000);
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  get f() {
    return this.allocationForm.controls;
  }
}

