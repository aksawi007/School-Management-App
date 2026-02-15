import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FeeCategoryService, FeeCategoryRequest } from 'sma-shared-lib';

@Component({
  selector: 'app-fee-category-form',
  templateUrl: './fee-category-form.component.html',
  styleUrls: ['./fee-category-form.component.scss']
})
export class FeeCategoryFormComponent implements OnInit {
  categoryForm!: FormGroup;
  isEditMode = false;
  categoryId?: string;
  loading = false;
  schoolId: number;
  categoryTypes = ['TUITION', 'TRANSPORT', 'LIBRARY', 'EXAM', 'MISCELLANEOUS'];
  feeApplicabilities = ['ANNUAL', 'MONTHLY'];
  paymentFrequencies = ['ONCE', 'MONTHLY', 'QUARTERLY', 'HALF_YEARLY'];

  constructor(
    private fb: FormBuilder,
    private feeCategoryService: FeeCategoryService,
    private snackBar: MatSnackBar,
    public dialogRef: MatDialogRef<FeeCategoryFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { schoolId: number, categoryId?: string }
  ) {
    this.schoolId = data.schoolId;
    this.categoryId = data.categoryId;
    this.isEditMode = !!this.categoryId;
    this.createForm();
  }

  ngOnInit(): void {
    if (this.isEditMode && this.categoryId) {
      this.loadCategory();
    }
  }

  createForm(): void {
    this.categoryForm = this.fb.group({
      categoryCode: ['', [Validators.required, Validators.maxLength(50)]],
      categoryName: ['', [Validators.required, Validators.maxLength(150)]],
      categoryType: ['TUITION', Validators.required],
      isMandatory: [true],
      isRefundable: [false],
      displayOrder: [1, [Validators.min(1)]],
      description: ['', Validators.maxLength(500)],
      feeApplicability: ['ANNUAL', Validators.required],
      paymentFrequency: ['ONCE', Validators.required]
    });
  }

  loadCategory(): void {
    if (!this.categoryId) return;

    this.loading = true;
    this.feeCategoryService.getFeeCategoryById(this.categoryId).subscribe({
      next: (category) => {
        this.categoryForm.patchValue({
          categoryCode: category.categoryCode,
          categoryName: category.categoryName,
          categoryType: category.categoryType,
          isMandatory: category.isMandatory,
          isRefundable: category.isRefundable,
          displayOrder: category.displayOrder,
          description: category.description,
          feeApplicability: category.feeApplicability || 'ANNUAL',
          paymentFrequency: category.paymentFrequency || 'ONCE'
        });
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading category:', error);
        this.snackBar.open('Error loading category', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.categoryForm.invalid) {
      this.snackBar.open('Please fill all required fields correctly', 'Close', { duration: 3000 });
      return;
    }

    this.loading = true;
    const request: FeeCategoryRequest = this.categoryForm.value;

    const serviceCall = this.isEditMode && this.categoryId
      ? this.feeCategoryService.updateFeeCategory(this.categoryId, request)
      : this.feeCategoryService.createFeeCategory(this.schoolId, request);

    serviceCall.subscribe({
      next: () => {
        this.snackBar.open(
          `Fee category ${this.isEditMode ? 'updated' : 'created'} successfully`,
          'Close',
          { duration: 3000 }
        );
        this.dialogRef.close(true);
      },
      error: (error: any) => {
        console.error('Error saving category:', error);
        this.snackBar.open(
          error.error?.message || 'Error saving fee category',
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

  get f() {
    return this.categoryForm.controls;
  }
}
