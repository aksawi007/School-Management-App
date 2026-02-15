import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FeePaymentService } from 'sma-shared-lib';
import { StudentFeePaymentResponse } from 'sma-shared-lib';
import { FeePaymentFormComponent } from '../fee-payment-form/fee-payment-form.component';
import { AdminCacheService } from '../../services/admin-cache.service';

@Component({
  selector: 'app-fee-payment-list',
  templateUrl: './fee-payment-list.component.html',
  styleUrls: ['./fee-payment-list.component.scss']
})
export class FeePaymentListComponent implements OnInit {
  displayedColumns: string[] = ['id', 'studentName', 'installmentName', 'amountPaid', 'discountAmount', 'paidOn', 'paymentRef', 'actions'];
  dataSource = new MatTableDataSource<StudentFeePaymentResponse>([]);
  
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  academicYears: any[] = [];
  selectedAcademicYear: number | null = null;
  selectedStudentId: number | null = null;
  selectedInstallmentId: number | null = null;
  
  isLoading = false;
  schoolId: number = 1;

  constructor(
    private feePaymentService: FeePaymentService,
    private adminCacheService: AdminCacheService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.schoolId = this.adminCacheService.getSchoolId();
    this.loadAcademicYears();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  loadAcademicYears(): void {
    this.adminCacheService.getAcademicYears().subscribe({
      next: (years) => {
        this.academicYears = years;
        const currentYear = this.academicYears.find(y => y.isCurrentYear);
        if (currentYear) {
          this.selectedAcademicYear = currentYear.id;
          this.loadPayments();
        }
      },
      error: (error) => {
        console.error('Error loading academic years:', error);
      }
    });
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
    const currentYear = this.academicYears.find(y => y.isCurrentYear);
    this.selectedAcademicYear = currentYear ? currentYear.id : null;
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
}
