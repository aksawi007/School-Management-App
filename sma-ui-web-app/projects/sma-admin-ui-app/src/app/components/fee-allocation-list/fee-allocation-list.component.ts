import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { StudentFeeAllocationService, StudentFeeAllocationResponse } from 'sma-shared-lib';

@Component({
  selector: 'app-fee-allocation-list',
  templateUrl: './fee-allocation-list.component.html',
  styleUrls: ['./fee-allocation-list.component.scss']
})
export class FeeAllocationListComponent implements OnInit {
  allocations: StudentFeeAllocationResponse[] = [];
  filteredAllocations: StudentFeeAllocationResponse[] = [];
  selectedStatus = 'ALL';
  selectedMonth = 'ALL';
  statuses = ['ALL', 'PENDING', 'PARTIALLY_PAID', 'PAID', 'OVERDUE', 'WAIVED', 'CANCELLED'];
  months = ['ALL', 'JANUARY', 'FEBRUARY', 'MARCH', 'APRIL', 'MAY', 'JUNE', 'JULY', 'AUGUST', 'SEPTEMBER', 'OCTOBER', 'NOVEMBER', 'DECEMBER'];
  displayedColumns: string[] = ['allocationCode', 'studentName', 'categoryName', 'feeAmount', 'durationType', 'month', 'dueDate', 'status', 'pendingAmount', 'actions'];
  loading = true;
  schoolId: number = 0;
  academicYearId: string = '';

  constructor(
    private feeAllocationService: StudentFeeAllocationService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

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
          this.loadOverdueAllocations();
        }
      }
    });
    
    // Request context from parent
    if (window.parent && window.parent !== window) {
      window.parent.postMessage({ type: 'REQUEST_CONTEXT' }, 'http://localhost:4300');
    }
  }

  loadOverdueAllocations(): void {
    this.loading = true;
    this.feeAllocationService.getOverdueFeeAllocations(this.schoolId, this.academicYearId).subscribe({
      next: (allocations: StudentFeeAllocationResponse[]) => {
        this.allocations = allocations;
        this.applyFilter();
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading fee allocations:', error);
        this.snackBar.open('Error loading fee allocations', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  loadAllocationsByMonth(month: string): void {
    this.loading = true;
    this.feeAllocationService.getFeeAllocationsByMonth(this.schoolId, this.academicYearId, month).subscribe({
      next: (allocations: StudentFeeAllocationResponse[]) => {
        this.allocations = allocations;
        this.applyFilter();
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading fee allocations:', error);
        this.snackBar.open('Error loading fee allocations', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  applyFilter(): void {
    let filtered = [...this.allocations];

    if (this.selectedStatus !== 'ALL') {
      filtered = filtered.filter(a => a.allocationStatus === this.selectedStatus);
    }

    if (this.selectedMonth !== 'ALL') {
      filtered = filtered.filter(a => a.applicableMonth === this.selectedMonth);
    }

    this.filteredAllocations = filtered;
  }

  onStatusChange(): void {
    this.applyFilter();
  }

  onMonthChange(): void {
    if (this.selectedMonth !== 'ALL') {
      this.loadAllocationsByMonth(this.selectedMonth);
    } else {
      this.loadOverdueAllocations();
    }
  }

  addAllocation(): void {
    this.router.navigate(['/admin/fee-allocation/new']);
  }

  viewAllocation(allocation: StudentFeeAllocationResponse): void {
    this.router.navigate(['/admin/fee-allocation/view', allocation.id]);
  }

  updatePayment(allocation: StudentFeeAllocationResponse): void {
    const paidAmount = prompt('Enter payment amount:', '0');
    if (paidAmount && !isNaN(parseFloat(paidAmount))) {
      this.feeAllocationService.updateAllocationStatus(
        allocation.id,
        'PARTIALLY_PAID',
        parseFloat(paidAmount)
      ).subscribe({
        next: () => {
          this.snackBar.open('Payment updated successfully', 'Close', { duration: 3000 });
          this.loadOverdueAllocations();
        },
        error: (error: any) => {
          console.error('Error updating payment:', error);
          this.snackBar.open('Error updating payment', 'Close', { duration: 3000 });
        }
      });
    }
  }

  cancelAllocation(allocation: StudentFeeAllocationResponse): void {
    const reason = prompt('Enter cancellation reason:');
    if (reason) {
      this.feeAllocationService.cancelFeeAllocation(allocation.id, reason).subscribe({
        next: () => {
          this.snackBar.open('Allocation cancelled successfully', 'Close', { duration: 3000 });
          this.loadOverdueAllocations();
        },
        error: (error: any) => {
          console.error('Error cancelling allocation:', error);
          this.snackBar.open(error.error?.message || 'Error cancelling allocation', 'Close', { duration: 3000 });
        }
      });
    }
  }

  getStatusColor(status: string): string {
    const colors: any = {
      'PENDING': 'warn',
      'PARTIALLY_PAID': 'accent',
      'PAID': 'primary',
      'OVERDUE': 'warn',
      'WAIVED': 'primary',
      'CANCELLED': ''
    };
    return colors[status] || '';
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(amount);
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('en-IN');
  }
}
