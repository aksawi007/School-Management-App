import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { FeePlanService, FeePlanResponse, AcademicYearResponse } from 'sma-shared-lib';
import { AdminCacheService } from '../../services/admin-cache.service';
import { FeePlanFormComponent } from '../fee-plan-form/fee-plan-form.component';

@Component({
  selector: 'app-fee-plan-list',
  templateUrl: './fee-plan-list.component.html',
  styleUrls: ['./fee-plan-list.component.scss']
})
export class FeePlanListComponent implements OnInit {
  feePlans: FeePlanResponse[] = [];
  academicYears: AcademicYearResponse[] = [];
  selectedAcademicYearId?: number;
  selectedStatus = 'ALL';
  statusOptions = ['ALL', 'ACTIVE', 'INACTIVE'];
  displayedColumns: string[] = ['categoryName', 'academicYearName', 'totalAmount', 'frequency', 'installmentsCount', 'status', 'actions'];
  loading = true;
  schoolId: number = 0;

  constructor(
    private feePlanService: FeePlanService,
    private adminCache: AdminCacheService,
    private router: Router,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
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
          this.loadFeePlans();
        }
      },
      error: (error) => {
        console.error('Error loading academic years:', error);
        this.snackBar.open('Error loading academic years', 'Close', { duration: 3000 });
      }
    });
  }

  loadFeePlans(): void {
    if (!this.selectedAcademicYearId) {
      this.feePlans = [];
      this.loading = false;
      return;
    }

    this.loading = true;
    this.feePlanService.listFeePlans(
      this.schoolId, 
      this.selectedAcademicYearId, 
      undefined, 
      this.selectedStatus,
      false
    ).subscribe({
      next: (plans) => {
        this.feePlans = plans;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading fee plans:', error);
        this.snackBar.open('Error loading fee plans', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  onAcademicYearChange(): void {
    this.loadFeePlans();
  }

  onStatusChange(): void {
    this.loadFeePlans();
  }

  openAddDialog(): void {
    const dialogRef = this.dialog.open(FeePlanFormComponent, {
      width: '900px',
      data: { schoolId: this.schoolId, academicYearId: this.selectedAcademicYearId }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadFeePlans();
      }
    });
  }

  editFeePlan(plan: FeePlanResponse): void {
    const dialogRef = this.dialog.open(FeePlanFormComponent, {
      width: '900px',
      data: { schoolId: this.schoolId, planId: plan.id }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadFeePlans();
      }
    });
  }

  viewFeePlan(plan: FeePlanResponse): void {
    debugger;
    this.dialog.open(FeePlanFormComponent, {
      width: '900px',
      data: { schoolId: this.schoolId, planId: plan.id, viewMode: true }
    });
  }

  toggleStatus(plan: FeePlanResponse): void {
    const newStatus = plan.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    
    this.feePlanService.getFeePlanById(plan.id, true).subscribe({
      next: (fullPlan) => {
        const request = {
          academicYearId: fullPlan.academicYearId,
          categoryId: fullPlan.categoryId,
          totalAmount: fullPlan.totalAmount,
          frequency: fullPlan.frequency,
          installmentsCount: fullPlan.installmentsCount,
          status: newStatus,
          installments: fullPlan.installments?.map(inst => ({
            installmentNo: inst.installmentNo,
            installmentName: inst.installmentName,
            periodStartDate: inst.periodStartDate,
            periodEndDate: inst.periodEndDate,
            amountDue: inst.amountDue,
            dueDate: inst.dueDate
          })) || []
        };

        this.feePlanService.updateFeePlan(plan.id, request).subscribe({
          next: () => {
            this.snackBar.open(`Fee plan ${newStatus.toLowerCase()} successfully`, 'Close', { duration: 3000 });
            this.loadFeePlans();
          },
          error: (error) => {
            console.error('Error updating fee plan status:', error);
            this.snackBar.open('Error updating fee plan status', 'Close', { duration: 3000 });
          }
        });
      },
      error: (error) => {
        console.error('Error loading fee plan details:', error);
        this.snackBar.open('Error loading fee plan details', 'Close', { duration: 3000 });
      }
    });
  }

  deleteFeePlan(plan: FeePlanResponse): void {
    if (confirm(`Are you sure you want to delete the fee plan for ${plan.categoryName}?`)) {
      this.feePlanService.deleteFeePlan(plan.id).subscribe({
        next: () => {
          this.snackBar.open('Fee plan deleted successfully', 'Close', { duration: 3000 });
          this.loadFeePlans();
        },
        error: (error) => {
          console.error('Error deleting fee plan:', error);
          this.snackBar.open('Error deleting fee plan', 'Close', { duration: 3000 });
        }
      });
    }
  }

  getStatusColor(status: string): string {
    return status === 'ACTIVE' ? 'primary' : 'warn';
  }

  getStatusText(status: string): string {
    return status === 'ACTIVE' ? 'Active' : 'Inactive';
  }
}
