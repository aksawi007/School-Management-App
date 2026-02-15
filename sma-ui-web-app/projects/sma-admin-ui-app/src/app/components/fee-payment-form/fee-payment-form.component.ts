import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import {
  FeePaymentService,
  FeePlanService,
  StudentService
} from 'sma-shared-lib';
import {
  StudentFeePaymentRequest,
  StudentFeePaymentResponse,
  FeePlanResponse,
  InstallmentResponse,
  Student
} from 'sma-shared-lib';
import { AdminCacheService } from '../../services/admin-cache.service';
import { Observable, of } from 'rxjs';
import { startWith, map, debounceTime, distinctUntilChanged, switchMap, catchError } from 'rxjs/operators';

@Component({
  selector: 'app-fee-payment-form',
  templateUrl: './fee-payment-form.component.html',
  styleUrls: ['./fee-payment-form.component.scss']
})
export class FeePaymentFormComponent implements OnInit {
  paymentForm!: FormGroup;
  academicYears: any[] = [];
  feePlans: FeePlanResponse[] = [];
  installments: InstallmentResponse[] = [];
  students: Student[] = [];
  studentSearchControl: FormControl = new FormControl('');
  filteredStudents$: Observable<Student[]> | undefined;
  loadingStudents = false;
  
  isEditMode = false;
  viewMode = false;
  isLoading = false;
  isLoadingData = false;
  schoolId: number = 1;
  paymentData: StudentFeePaymentResponse | null = null;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<FeePaymentFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private feePaymentService: FeePaymentService,
    private feePlanService: FeePlanService,
    private adminCacheService: AdminCacheService,
    private studentService: StudentService
  ) {
    this.viewMode = data?.viewMode || false;
    this.isEditMode = !!data?.paymentId && !this.viewMode;
  }

  ngOnInit(): void {
    this.schoolId = this.adminCacheService.getSchoolId();
    this.createForm();
    this.loadAcademicYears();
    
    if (!this.isEditMode && !this.viewMode) {
      this.initStudentSearch();
    }
    
    if (this.data?.paymentId) {
      this.loadPaymentData(this.data.paymentId);
    } else {
      // Pre-populate if data provided
      if (this.data?.academicYearId) {
        this.paymentForm.patchValue({ academicYearId: this.data.academicYearId });
        this.onAcademicYearChange();
      }
      if (this.data?.studentId) {
        this.paymentForm.patchValue({ studentId: this.data.studentId });
      }
    }
  }

  createForm(): void {
    this.paymentForm = this.fb.group({
      academicYearId: [null, Validators.required],
      studentId: [null, Validators.required],
      feePlanId: [null, Validators.required],
      feeInstallmentId: [null, Validators.required],
      amountPaid: [0, [Validators.required, Validators.min(0)]],
      discountAmount: [0, Validators.min(0)],
      paidOn: [new Date(), Validators.required],
      paymentRef: [''],
      remarks: ['']
    });

    if (this.viewMode) {
      this.paymentForm.disable();
    }
  }

  loadAcademicYears(): void {
    this.adminCacheService.getAcademicYears().subscribe({
      next: (years) => {
        this.academicYears = years;
        if (!this.isEditMode && !this.viewMode) {
          const currentYear = this.academicYears.find(y => y.currentYear === true);
          if (currentYear && !this.paymentForm.get('academicYearId')?.value) {
            this.paymentForm.patchValue({ academicYearId: currentYear.yearId });
            this.onAcademicYearChange();
          }
        }
      },
      error: (error) => {
        console.error('Error loading academic years:', error);
      }
    });
  }

  /**
   * Initialize server-side student search with debouncing
   */
  private initStudentSearch(): void {
    this.filteredStudents$ = this.studentSearchControl.valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      distinctUntilChanged(),
      map(value => typeof value === 'string' ? value : ''),
      switchMap(searchTerm => {
        if (!searchTerm || searchTerm.length < 2) {
          return of([]);
        }
        this.loadingStudents = true;
        return this.studentService.searchStudents(this.schoolId, { search: searchTerm }).pipe(
          map(students => {
            this.loadingStudents = false;
            return students.filter((s: Student) => s.status === 'ACTIVE');
          }),
          catchError(error => {
            console.error('Error searching students:', error);
            this.loadingStudents = false;
            return of([]);
          })
        );
      })
    );
  }

  displayStudentFn(student?: Student): string {
    return student ? `${student.admissionNo} - ${student.firstName} ${student.lastName}` : '';
  }

  onStudentSelected(student: Student | null): void {
    if (student) {
      this.paymentForm.patchValue({ studentId: student.id });
    } else {
      this.paymentForm.patchValue({ studentId: null });
    }
  }

  loadPaymentData(paymentId: number): void {
    this.isLoadingData = true;
    this.feePaymentService.getPaymentById(paymentId).subscribe({
      next: (payment) => {
        this.paymentData = payment;
        // For view mode, just display the data
        // We don't have all fields in the response to fully populate the form
        this.isLoadingData = false;
      },
      error: (error) => {
        console.error('Error loading payment:', error);
        this.isLoadingData = false;
      }
    });
  }

  onAcademicYearChange(): void {
    const academicYearId = this.paymentForm.get('academicYearId')?.value;
    if (academicYearId) {
      this.loadFeePlans(academicYearId);
    }
    // Reset dependent fields
    this.paymentForm.patchValue({ 
      feePlanId: null, 
      feeInstallmentId: null 
    });
    this.feePlans = [];
    this.installments = [];
  }

  loadFeePlans(academicYearId: number): void {
    this.feePlanService.listFeePlans(this.schoolId, academicYearId).subscribe({
      next: (plans) => {
        this.feePlans = plans;
      },
      error: (error) => {
        console.error('Error loading fee plans:', error);
      }
    });
  }

  onFeePlanChange(): void {
    const feePlanId = this.paymentForm.get('feePlanId')?.value;
    // Reset installment field and clear array
    this.paymentForm.patchValue({ feeInstallmentId: null });
    this.installments = [];
    
    if (feePlanId) {
      this.loadInstallments(feePlanId);
    }
  }

  loadInstallments(feePlanId: number): void {
    this.feePlanService.getFeePlanById(feePlanId, true).subscribe({
      next: (plan) => {
        this.installments = plan.installments || [];
        console.log('Loaded installments:', this.installments);
      },
      error: (error) => {
        console.error('Error loading installments:', error);
      }
    });
  }

  onInstallmentChange(): void {
    const installmentId = this.paymentForm.get('feeInstallmentId')?.value;
    console.log('Selected installment ID:', installmentId);
    console.log('Available installments:', this.installments);
    const installment = this.installments.find(i => i.id === installmentId);
    
    if (installment) {
      console.log('Found installment:', installment);
      // Pre-fill amount with installment's due amount
      this.paymentForm.patchValue({ 
        amountPaid: installment.amountDue 
      });
    }
  }

  onSubmit(): void {
    if (this.paymentForm.invalid || this.viewMode) {
      return;
    }

    this.isLoading = true;
    const formValue = this.paymentForm.value;

    const request: StudentFeePaymentRequest = {
      studentId: formValue.studentId,
      feeInstallmentId: formValue.feeInstallmentId,
      amountPaid: formValue.amountPaid,
      discountAmount: formValue.discountAmount || 0,
      paidOn: this.formatDate(formValue.paidOn),
      paymentRef: formValue.paymentRef,
      remarks: formValue.remarks
    };

    this.feePaymentService.recordPayment(this.schoolId, request).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.dialogRef.close(response);
      },
      error: (error) => {
        console.error('Error recording payment:', error);
        this.isLoading = false;
        alert('Failed to record payment: ' + (error.error?.message || error.message));
      }
    });
  }

  formatDate(date: Date | string): string {
    if (typeof date === 'string') {
      return date;
    }
    return date.toISOString();
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  get title(): string {
    if (this.viewMode) return 'Payment Details';
    if (this.isEditMode) return 'Edit Payment';
    return 'Register Payment';
  }
}
