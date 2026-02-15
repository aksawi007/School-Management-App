import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FeePaymentService, ClassMasterService, SectionMasterService } from 'sma-shared-lib';
import { StudentFeePaymentResponse, PendingPaymentResponse } from 'sma-shared-lib';
import { FeePaymentFormComponent } from '../fee-payment-form/fee-payment-form.component';
import { AdminCacheService } from '../../services/admin-cache.service';

@Component({
  selector: 'app-fee-payment-list',
  templateUrl: './fee-payment-list.component.html',
  styleUrls: ['./fee-payment-list.component.scss']
})
export class FeePaymentListComponent implements OnInit {
  // Tabs
  selectedTab = 0;
  
  // Payment History
  displayedColumns: string[] = ['id', 'studentName', 'installmentName', 'amountPaid', 'discountAmount', 'paidOn', 'paymentRef', 'actions'];
  dataSource = new MatTableDataSource<StudentFeePaymentResponse>([]);
  
  // Pending Payments
  pendingColumns: string[] = ['studentName', 'admissionNo', 'rollNumber', 'className', 'sectionName', 'feeCategoryName', 'installmentName', 'amountDue', 'dueDate', 'daysPastDue', 'actions'];
  pendingDataSource = new MatTableDataSource<PendingPaymentResponse>([]);
  
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  academicYears: any[] = [];
  classes: any[] = [];
  sections: any[] = [];
  
  selectedAcademicYear: number | null = null;
  selectedStudentId: number | null = null;
  selectedInstallmentId: number | null = null;
  
  // For pending payments
  selectedClassId: number | null = null;
  selectedSectionId: number | null = null;
  
  isLoading = false;
  schoolId: number = 1;

  constructor(
    private feePaymentService: FeePaymentService,
    private adminCacheService: AdminCacheService,
    private classMasterService: ClassMasterService,
    private sectionMasterService: SectionMasterService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.schoolId = this.adminCacheService.getSchoolId();
    this.loadAcademicYears();
    this.loadClasses();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  loadAcademicYears(): void {
    this.adminCacheService.getAcademicYears().subscribe({
      next: (years) => {
        this.academicYears = years;
        const currentYear = this.academicYears.find(y => y.currentYear);
        if (currentYear) {
          this.selectedAcademicYear = currentYear.yearId;
          if (this.selectedTab === 0) {
            this.loadPayments();
          }
        }
      },
      error: (error) => {
        console.error('Error loading academic years:', error);
      }
    });
  }

  loadClasses(): void {
    this.adminCacheService.getClasses().subscribe({
      next: (classes) => {
        this.classes = classes;
      },
      error: (error) => {
        console.error('Error loading classes:', error);
      }
    });
  }

  onClassChange(): void {
    this.sections = [];
    this.selectedSectionId = null;
    
    if (this.selectedClassId) {
      this.sectionMasterService.getSectionsByClass(this.schoolId, this.selectedClassId.toString()).subscribe({
        next: (sections) => {
          this.sections = sections;
        },
        error: (error) => {
          console.error('Error loading sections:', error);
        }
      });
    }
  }

  loadPayments(): void {
    this.isLoading = true;
    this.feePaymentService.listPayments(
      this.schoolId,
      this.selectedStudentId || undefined,
      this.selectedInstallmentId || undefined
    ).subscribe({
      next: (payments) => {
        this.dataSource.data = payments;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading payments:', error);
        this.isLoading = false;
      }
    });
  }

  onSearch(): void {
    this.loadPayments();
  }

  onReset(): void {
    this.selectedStudentId = null;
    this.selectedInstallmentId = null;
    const currentYear = this.academicYears.find(y => y.currentYear);
    this.selectedAcademicYear = currentYear ? currentYear.yearId : null;
    this.loadPayments();
  }

  openPaymentDialog(): void {
    const dialogRef = this.dialog.open(FeePaymentFormComponent, {
      width: '700px',
      data: { 
        academicYearId: this.selectedAcademicYear,
        studentId: this.selectedStudentId
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadPayments();
      }
    });
  }

  viewPayment(payment: StudentFeePaymentResponse): void {
    this.dialog.open(FeePaymentFormComponent, {
      width: '700px',
      data: { 
        paymentId: payment.id,
        viewMode: true
      }
    });
  }

  deletePayment(payment: StudentFeePaymentResponse): void {
    if (confirm(`Are you sure you want to delete payment #${payment.id}?`)) {
      this.feePaymentService.deletePayment(payment.id).subscribe({
        next: () => {
          this.loadPayments();
        },
        error: (error) => {
          console.error('Error deleting payment:', error);
          alert('Failed to delete payment');
        }
      });
    }
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  loadPendingPayments(): void {
    if (!this.selectedAcademicYear || !this.selectedClassId) {
      alert('Please select Academic Year and Class');
      return;
    }

    this.isLoading = true;
    this.feePaymentService.getPendingPayments(
      this.schoolId,
      this.selectedAcademicYear,
      this.selectedClassId,
      this.selectedSectionId || undefined
    ).subscribe({
      next: (pending) => {
        this.pendingDataSource.data = pending;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading pending payments:', error);
        this.isLoading = false;
      }
    });
  }

  registerPaymentForPending(pending: PendingPaymentResponse): void {
    const dialogRef = this.dialog.open(FeePaymentFormComponent, {
      width: '700px',
      data: { 
        academicYearId: this.selectedAcademicYear,
        studentId: pending.studentId,
        prefilledInstallmentId: pending.feeInstallmentId
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadPendingPayments();
      }
    });
  }
}
