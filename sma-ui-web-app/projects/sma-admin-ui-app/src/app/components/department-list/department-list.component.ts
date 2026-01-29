import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DepartmentService, DepartmentResponse } from 'sma-shared-lib';
import { MatSnackBar } from '@angular/material/snack-bar';

interface SchoolContext {
  schoolId: number;
  schoolName: string;
  schoolCode: string;
}

@Component({
  selector: 'app-department-list',
  templateUrl: './department-list.component.html',
  styleUrls: ['./department-list.component.scss']
})
export class DepartmentListComponent implements OnInit {
  departments: DepartmentResponse[] = [];
  displayedColumns: string[] = ['departmentCode', 'departmentName', 'departmentType', 'hodName', 'hodEmail', 'actions'];
  loading = true;
  schoolId: number = 0;
  selectedSchool: SchoolContext | null = null;

  constructor(
    private departmentService: DepartmentService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    // Listen for school context from parent window
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
          this.loadDepartments();
        }
      }
    });
    
    // Request context from parent
    if (window.parent && window.parent !== window) {
      window.parent.postMessage({ type: 'REQUEST_CONTEXT' }, 'http://localhost:4300');
    }
  }

  loadDepartments(): void {
    this.loading = true;
    this.departmentService.getDepartmentsBySchool(this.schoolId).subscribe({
      next: (departments: DepartmentResponse[]) => {
        this.departments = departments;
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading departments:', error);
        this.snackBar.open('Error loading departments', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  addDepartment(): void {
    this.router.navigate(['/departments/new']);
  }

  editDepartment(departmentId: number): void {
    this.router.navigate(['/departments', departmentId, 'edit']);
  }

  deleteDepartment(department: DepartmentResponse): void {
    if (confirm(`Are you sure you want to delete department ${department.departmentName}?`)) {
      this.departmentService.deleteDepartment(department.departmentId).subscribe({
        next: () => {
          this.snackBar.open('Department deleted successfully', 'Close', { duration: 3000 });
          this.loadDepartments();
        },
        error: (error: any) => {
          console.error('Error deleting department:', error);
          this.snackBar.open('Error deleting department', 'Close', { duration: 3000 });
        }
      });
    }
  }
}
