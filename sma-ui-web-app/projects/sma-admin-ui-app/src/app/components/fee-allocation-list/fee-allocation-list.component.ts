import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { FeeCategoryService, FeeCategoryResponse, AcademicYearResponse } from 'sma-shared-lib';
import { AdminCacheService } from '../../services/admin-cache.service';
import { FeeAllocationFormComponent } from '../fee-allocation-form/fee-allocation-form.component';

@Component({
  selector: 'app-fee-allocation-list',
  templateUrl: './fee-allocation-list.component.html',
  styleUrls: ['./fee-allocation-list.component.scss']
})
export class FeeAllocationListComponent implements OnInit {
  allocations: any[] = []; // TODO: Create proper interface
  filteredAllocations: any[] = [];
  selectedCategory = 'ALL';
  categoryTypes = ['ALL', 'TUITION', 'TRANSPORT', 'LIBRARY', 'EXAM', 'MISCELLANEOUS'];
  displayedColumns: string[] = ['allocationCode', 'academicYear', 'categoryName', 'categoryType', 'feeAmount', 'dueDate', 'isActive', 'actions'];
  loading = true;
  schoolId: number = 0;
  selectedAcademicYearId?: number;
  academicYears: AcademicYearResponse[] = [];

  constructor(
    private feeCategoryService: FeeCategoryService,
    private adminCache: AdminCacheService,
    private dialog: MatDialog,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    // Listen for school context from parent window (shell app)
    window.addEventListener('message', (event) => {
      // Verify origin for security
      if (event.origin !== 'http://localhost:4300') {
        return;
      }
      
      if (event.data && event.data.type === 'SCHOOL_CONTEXT') {
        console.log('Received school context:', event.data);
        const school = event.data.school;
        
        if (school) {
          this.schoolId = school.schoolId;
          this.loadAcademicYears();
        }
      }
    });
    
    // Request context from parent
    if (window.parent && window.parent !== window) {
      window.parent.postMessage({ type: 'REQUEST_CONTEXT' }, 'http://localhost:4300');
    }
  }

  loadAcademicYears(): void {
    this.adminCache.getAcademicYears().subscribe({
      next: (years) => {
        this.academicYears = years;
        // Auto-select current academic year
        const currentYear = years.find(y => y.currentYear === true);
        if (currentYear && !this.selectedAcademicYearId) {
          this.selectedAcademicYearId = currentYear.yearId;
          this.loadAllocations();
        }
      },
      error: (error) => {
        console.error('Error loading academic years:', error);
        this.snackBar.open('Error loading academic years', 'Close', { duration: 3000 });
      }
    });
  }

  onAcademicYearChange(): void {
    this.loadAllocations();
  }

  loadAllocations(): void {
    if (!this.selectedAcademicYearId) {
      this.allocations = [];
      this.loading = false;
      return;
    }

    this.loading = true;
    // TODO: Call academic year fee allocation service
    // For now, show empty list
    setTimeout(() => {
      this.allocations = [];
      this.applyFilter();
      this.loading = false;
    }, 500);
  }

  applyFilter(): void {
    let filtered = [...this.allocations];

    if (this.selectedCategory !== 'ALL') {
      filtered = filtered.filter((a: any) => a.categoryType === this.selectedCategory);
    }

    this.filteredAllocations = filtered;
  }

  onCategoryChange(): void {
    this.applyFilter();
  }

  addAllocation(): void {
    if (!this.selectedAcademicYearId) {
      this.snackBar.open('Please select an academic year first', 'Close', { duration: 3000 });
      return;
    }

    const dialogRef = this.dialog.open(FeeAllocationFormComponent, {
      width: '800px',
      data: { 
        schoolId: this.schoolId,
        academicYearId: this.selectedAcademicYearId
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadAllocations();
      }
    });
  }

  editAllocation(allocation: any): void {
    this.router.navigate(['/admin/fee-allocation', allocation.id, 'edit']);
  }

  toggleStatus(allocation: any): void {
    const newStatus = !allocation.isActive;
    const action = newStatus ? 'activate' : 'deactivate';
    
    if (confirm(`Are you sure you want to ${action} this fee allocation?`)) {
      // TODO: Call service to update status
      this.snackBar.open(`Fee allocation ${action}d successfully`, 'Close', { duration: 3000 });
      allocation.isActive = newStatus;
    }
  }

  getStatusColor(isActive: boolean): string {
    return isActive ? 'primary' : '';
  }

  getStatusText(isActive: boolean): string {
    return isActive ? 'ACTIVE' : 'INACTIVE';
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(amount);
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleDateString('en-IN');
  }
}
