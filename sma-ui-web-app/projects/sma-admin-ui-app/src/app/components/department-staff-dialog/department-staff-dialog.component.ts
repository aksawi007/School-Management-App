import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DepartmentService, DepartmentStaffResponse } from 'sma-shared-lib';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-department-staff-dialog',
  templateUrl: './department-staff-dialog.component.html',
  styleUrls: ['./department-staff-dialog.component.scss']
})
export class DepartmentStaffDialogComponent implements OnInit {
  staffList: DepartmentStaffResponse[] = [];
  loading = true;
  displayedColumns: string[] = ['employeeCode', 'fullName', 'staffType', 'designation', 'email', 'phone', 'memberSince', 'isPrimary'];

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { departmentId: number; departmentName: string },
    private dialogRef: MatDialogRef<DepartmentStaffDialogComponent>,
    private departmentService: DepartmentService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadDepartmentStaff();
  }

  loadDepartmentStaff(): void {
    this.loading = true;
    this.departmentService.getDepartmentStaff(this.data.departmentId).subscribe({
      next: (staff: DepartmentStaffResponse[]) => {
        this.staffList = staff;
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading department staff:', error);
        this.snackBar.open('Error loading staff members', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  formatDate(dateString?: string): string {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
  }

  close(): void {
    this.dialogRef.close();
  }
}
