import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
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
  schoolId: number = 0;
  categoryTypes = ['TUITION', 'TRANSPORT', 'LIBRARY', 'EXAM', 'MISCELLANEOUS'];
  feeApplicabilities = ['ANNUAL', 'MONTHLY'];
  paymentFrequencies = ['ONCE', 'MONTHLY', 'QUARTERLY', 'HALF_YEARLY'];

  constructor(
    private fb: FormBuilder,
    private feeCategoryService: FeeCategoryService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar
  ) {
    this.createForm();
  }

  ngOnInit(): void {
    this.categoryId = this.route.snapshot.paramMap.get('id') || undefined;
    this.isEditMode = !!this.categoryId;

    // Listen for school context from parent window (shell app)
    window.addEventListener('message', (event) => {
      if (event.origin !== 'http://localhost:4300') {
        return;
      }
      
      if (event.data && event.data.type === 'SCHOOL_CONTEXT') {
        console.log('Received school context:', event.data);
        const school = event.data.school;
        
        if (school) {
          this.schoolId = school.schoolId;
          
          if (this.isEditMode && this.categoryId) {
            this.loadCategory();
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
        this.router.navigate(['/admin/fee-categories']);
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
    this.router.navigate(['/admin/fee-categories']);
  }

  get f() {
    return this.categoryForm.controls;
  }
}
