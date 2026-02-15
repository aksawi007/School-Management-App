import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { FeeCategoryService } from 'sma-shared-lib';
import { FeeCategoryResponse } from 'sma-shared-lib';

@Component({
  selector: 'app-fee-category-list',
  templateUrl: './fee-category-list.component.html',
  styleUrls: ['./fee-category-list.component.scss']
})
export class FeeCategoryListComponent implements OnInit {
  categories: FeeCategoryResponse[] = [];
  filteredCategories: FeeCategoryResponse[] = [];
  selectedType = 'ALL';
  categoryTypes = ['ALL', 'TUITION', 'TRANSPORT', 'LIBRARY', 'EXAM', 'MISCELLANEOUS'];
  displayedColumns: string[] = ['categoryCode', 'categoryName', 'categoryType', 'feeApplicability', 'paymentFrequency', 'isMandatory', 'isRefundable', 'displayOrder', 'isActive', 'actions'];
  loading = true;
  schoolId: number = 0;

  constructor(
    private feeCategoryService: FeeCategoryService,
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
          this.loadCategories();
        }
      }
    });
    
    // Request context from parent
    if (window.parent && window.parent !== window) {
      window.parent.postMessage({ type: 'REQUEST_CONTEXT' }, 'http://localhost:4300');
    }
  }

  loadCategories(): void {
    this.loading = true;
    this.feeCategoryService.getAllActiveFeeCategories(this.schoolId).subscribe({
      next: (categories: any[]) => {
        this.categories = categories.sort((a: any, b: any) => (a.displayOrder || 0) - (b.displayOrder || 0));
        this.applyFilter();
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading fee categories:', error);
        this.snackBar.open('Error loading fee categories', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  applyFilter(): void {
    if (this.selectedType === 'ALL') {
      this.filteredCategories = [...this.categories];
    } else {
      this.filteredCategories = this.categories.filter(c => c.categoryType === this.selectedType);
    }
  }

  onTypeChange(): void {
    this.applyFilter();
  }

  addCategory(): void {
    this.router.navigate(['/admin/fee-category/new']);
  }

  editCategory(category: FeeCategoryResponse): void {
    this.router.navigate(['/admin/fee-category/edit', category.id]);
  }

  toggleStatus(category: FeeCategoryResponse): void {
    const action = category.isActive ? 'deactivate' : 'activate';
    const message = category.isActive 
      ? `Are you sure you want to deactivate "${category.categoryName}"?`
      : `Are you sure you want to activate "${category.categoryName}"?`;

    if (confirm(message)) {
      const serviceCall = category.isActive 
        ? this.feeCategoryService.deactivateFeeCategory(category.id)
        : this.feeCategoryService.activateFeeCategory(category.id);

      serviceCall.subscribe({
        next: () => {
          this.snackBar.open(`Category ${action}d successfully`, 'Close', { duration: 3000 });
          this.loadCategories();
        },
        error: (error: any) => {
          console.error(`Error ${action}ing category:`, error);
          this.snackBar.open(`Error ${action}ing category`, 'Close', { duration: 3000 });
        }
      });
    }
  }

  getStatusColor(isActive: boolean): string {
    return isActive ? 'primary' : 'warn';
  }

  getStatusText(isActive: boolean): string {
    return isActive ? 'Active' : 'Inactive';
  }
}
