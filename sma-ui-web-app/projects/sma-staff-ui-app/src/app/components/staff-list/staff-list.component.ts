import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { StaffService, StaffResponse, StaffType } from 'sma-shared-lib';
import { MatSnackBar } from '@angular/material/snack-bar';

interface SchoolContext {
  schoolId: number;
  schoolName: string;
  schoolCode: string;
}

@Component({
  selector: 'app-staff-list',
  templateUrl: './staff-list.component.html',
  styleUrls: ['./staff-list.component.scss']
})
export class StaffListComponent implements OnInit {
  staffMembers: StaffResponse[] = [];
  displayedColumns: string[] = ['employeeCode', 'fullName', 'email', 'phoneNumber', 'staffType', 'designation', 'actions'];
  loading = true;
  selectedSchool: SchoolContext | null = null;
  schoolId = 0;

  constructor(
    private staffService: StaffService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    // Listen for school context from parent window
    window.addEventListener('message', (event) => {
      if (event.origin !== 'http://localhost:4300') {
        return;
      }

      if (event.data && event.data.type === 'SCHOOL_CONTEXT') {
        console.log('Received school context:', event.data);
        this.selectedSchool = event.data.school;
        
        if (this.selectedSchool) {
          this.schoolId = this.selectedSchool.schoolId;
          this.loadStaff();
        }
      }
    });

    // Request context from parent
    if (window.parent && window.parent !== window) {
      window.parent.postMessage({ type: 'REQUEST_CONTEXT' }, 'http://localhost:4300');
    }
  }

  loadStaff(): void {
    if (!this.schoolId) return;

    this.loading = true;
    this.staffService.getAllStaffBySchool(this.schoolId).subscribe({
      next: (staff) => {
        this.staffMembers = staff;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading staff:', error);
        this.snackBar.open('Error loading staff members', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  addStaff(): void {
    this.router.navigate(['/staff/new']);
  }

  viewStaff(staffId: number): void {
    this.router.navigate(['/staff', staffId]);
  }

  editStaff(staffId: number): void {
    this.router.navigate(['/staff', staffId, 'edit']);
  }

  deleteStaff(staffId: number): void {
    if (confirm('Are you sure you want to delete this staff member?')) {
      this.staffService.deleteStaff(staffId).subscribe({
        next: () => {
          this.snackBar.open('Staff member deleted successfully', 'Close', { duration: 3000 });
          this.loadStaff();
        },
        error: (error) => {
          console.error('Error deleting staff:', error);
          this.snackBar.open('Error deleting staff member', 'Close', { duration: 3000 });
        }
      });
    }
  }

  getFullName(staff: StaffResponse): string {
    return `${staff.firstName} ${staff.lastName}`;
  }
}
