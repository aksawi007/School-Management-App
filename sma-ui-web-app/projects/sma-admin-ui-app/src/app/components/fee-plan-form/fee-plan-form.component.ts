import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FeePlanService, FeePlanRequest, FeeCategoryService, FeeCategoryResponse, AcademicYearResponse } from 'sma-shared-lib';
import { AdminCacheService } from '../../services/admin-cache.service';

@Component({
  selector: 'app-fee-plan-form',
  templateUrl: './fee-plan-form.component.html',
  styleUrls: ['./fee-plan-form.component.scss']
})
export class FeePlanFormComponent implements OnInit {
  planForm!: FormGroup;
  isEditMode = false;
  viewMode = false;
  planId?: number;
  loading = false;
  schoolId: number;
  academicYearId?: number;
  academicYears: AcademicYearResponse[] = [];
  feeCategories: FeeCategoryResponse[] = [];
  frequencies = ['MONTHLY', 'QUARTERLY', 'HALF_YEARLY', 'ANNUAL'];
  private isLoadingData = false;

  constructor(
    private fb: FormBuilder,
    private feePlanService: FeePlanService,
    private feeCategoryService: FeeCategoryService,
    private adminCache: AdminCacheService,
    private snackBar: MatSnackBar,
    public dialogRef: MatDialogRef<FeePlanFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { schoolId: number, academicYearId?: number, planId?: number, viewMode?: boolean }
  ) {
    this.schoolId = data.schoolId;
    this.academicYearId = data.academicYearId;
    this.planId = data.planId;
    this.viewMode = data.viewMode || false;
    this.isEditMode = !!this.planId && !this.viewMode;
    this.createForm();
  }

  ngOnInit(): void {
    debugger;
    this.loadAcademicYears();
    this.loadFeeCategories();
    
    // Load plan data for both edit and view modes
    if (this.planId) {
      this.loadFeePlan();
    }
  }

  createForm(): void {
    this.planForm = this.fb.group({
      academicYearId: [this.academicYearId || '', Validators.required],
      categoryId: ['', Validators.required],
      totalAmount: [0, [Validators.required, Validators.min(0)]],
      frequency: ['ANNUAL', Validators.required],
      installmentsCount: [1, [Validators.required, Validators.min(1), Validators.max(12)]],
      installments: this.fb.array([])
    });

    // Watch for changes to installments count (only when not loading data)
    this.planForm.get('installmentsCount')?.valueChanges.subscribe(count => {
      if (!this.isLoadingData) {
        this.updateInstallmentsArray(count);
      }
    });

    // Watch for changes to total amount (only when not loading data)
    this.planForm.get('totalAmount')?.valueChanges.subscribe(() => {
      if (!this.isLoadingData) {
        this.distributeAmount();
      }
    });

    // Disable dropdowns in view mode
    if (this.viewMode) {
      this.planForm.get('academicYearId')?.disable();
      this.planForm.get('categoryId')?.disable();
      this.planForm.get('frequency')?.disable();
    }

    // Initialize with 1 installment for create mode
    if (!this.isEditMode && !this.viewMode) {
      this.updateInstallmentsArray(1);
    }
  }

  get installments(): FormArray {
    return this.planForm.get('installments') as FormArray;
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

  loadFeeCategories(): void {
    this.feeCategoryService.getAllActiveFeeCategories(this.schoolId).subscribe({
      next: (categories) => {
        this.feeCategories = categories;
      },
      error: (error) => {
        console.error('Error loading fee categories:', error);
        this.snackBar.open('Error loading fee categories', 'Close', { duration: 3000 });
      }
    });
  }

  loadFeePlan(): void {
    if (!this.planId) return;

    this.loading = true;
    this.isLoadingData = true;
    this.feePlanService.getFeePlanById(this.planId, true).subscribe({
      next: (plan) => {
        // Clear existing installments first
        this.installments.clear();
        
        // Load installments before patching form values
        if (plan.installments && plan.installments.length > 0) {
          plan.installments.forEach(inst => {
            this.installments.push(this.fb.group({
              installmentNo: [inst.installmentNo, Validators.required],
              installmentName: [inst.installmentName, Validators.required],
              periodStartDate: [inst.periodStartDate, Validators.required],
              periodEndDate: [inst.periodEndDate, Validators.required],
              amountDue: [inst.amountDue, [Validators.required, Validators.min(0)]],
              dueDate: [inst.dueDate, Validators.required]
            }));
          });
        }

        // Now patch the form values
        this.planForm.patchValue({
          academicYearId: plan.academicYearId,
          categoryId: plan.categoryId,
          totalAmount: plan.totalAmount,
          frequency: plan.frequency,
          installmentsCount: plan.installmentsCount
        }, { emitEvent: false });

        this.loading = false;
        this.isLoadingData = false;
      },
      error: (error) => {
        console.error('Error loading fee plan:', error);
        this.snackBar.open('Error loading fee plan', 'Close', { duration: 3000 });
        this.loading = false;
        this.isLoadingData = false;
      }
    });
  }

  updateInstallmentsArray(count: number): void {
    const currentLength = this.installments.length;

    if (count > currentLength) {
      // Add new installments
      for (let i = currentLength; i < count; i++) {
        this.installments.push(this.createInstallmentGroup(i + 1));
      }
    } else if (count < currentLength) {
      // Remove extra installments
      for (let i = currentLength - 1; i >= count; i--) {
        this.installments.removeAt(i);
      }
    }

    this.distributeAmount();
  }

  createInstallmentGroup(installmentNo: number): FormGroup {
    return this.fb.group({
      installmentNo: [installmentNo, Validators.required],
      installmentName: [`Installment ${installmentNo}`, Validators.required],
      periodStartDate: ['', Validators.required],
      periodEndDate: ['', Validators.required],
      amountDue: [0, [Validators.required, Validators.min(0)]],
      dueDate: ['', Validators.required]
    });
  }

  distributeAmount(): void {
    const totalAmount = this.planForm.get('totalAmount')?.value || 0;
    const count = this.installments.length;

    if (count === 0 || totalAmount === 0) return;

    const amountPerInstallment = Math.floor((totalAmount / count) * 100) / 100;
    let remaining = totalAmount - (amountPerInstallment * count);

    this.installments.controls.forEach((control, index) => {
      let amount = amountPerInstallment;
      // Add any remaining cents to the last installment
      if (index === count - 1) {
        amount += remaining;
      }
      control.patchValue({ amountDue: amount });
    });
  }

  onSubmit(): void {
    if (this.planForm.invalid) {
      this.snackBar.open('Please fill all required fields correctly', 'Close', { duration: 3000 });
      Object.keys(this.planForm.controls).forEach(key => {
        const control = this.planForm.get(key);
        if (control?.invalid) {
          control.markAsTouched();
        }
      });
      return;
    }

    this.loading = true;
    const request: FeePlanRequest = {
      academicYearId: this.planForm.value.academicYearId,
      categoryId: this.planForm.value.categoryId,
      totalAmount: this.planForm.value.totalAmount,
      frequency: this.planForm.value.frequency,
      installmentsCount: this.planForm.value.installmentsCount,
      status: 'ACTIVE',
      installments: this.planForm.value.installments
    };

    const serviceCall = this.isEditMode && this.planId
      ? this.feePlanService.updateFeePlan(this.planId, request)
      : this.feePlanService.createFeePlan(this.schoolId, request);

    serviceCall.subscribe({
      next: () => {
        this.snackBar.open(
          `Fee plan ${this.isEditMode ? 'updated' : 'created'} successfully`,
          'Close',
          { duration: 3000 }
        );
        this.dialogRef.close(true);
      },
      error: (error) => {
        console.error('Error saving fee plan:', error);
        this.snackBar.open(
          error.error?.message || 'Error saving fee plan',
          'Close',
          { duration: 3000 }
        );
        this.loading = false;
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
