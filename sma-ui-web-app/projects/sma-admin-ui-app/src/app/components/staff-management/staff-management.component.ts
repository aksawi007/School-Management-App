import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface Staff {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
  department: string;
}

@Component({
  selector: 'app-staff-management',
  template: `
    <div class="staff-container">
      <div class="header">
        <h1>Staff Management</h1>
        <button mat-raised-button color="primary" (click)="addStaff()">
          <mat-icon>person_add</mat-icon>
          Add Staff
        </button>
      </div>

      <mat-card>
        <mat-card-content>
          <mat-form-field class="search-field">
            <mat-label>Search Staff</mat-label>
            <input matInput placeholder="Search by name, email, or role" [(ngModel)]="searchText">
            <mat-icon matPrefix>search</mat-icon>
          </mat-form-field>

          <table mat-table [dataSource]="staffList" class="mat-elevation-z2">
            <ng-container matColumnDef="id">
              <th mat-header-cell *matHeaderCellDef>ID</th>
              <td mat-cell *matCellDef="let staff">{{ staff.id }}</td>
            </ng-container>

            <ng-container matColumnDef="name">
              <th mat-header-cell *matHeaderCellDef>Name</th>
              <td mat-cell *matCellDef="let staff">{{ staff.firstName }} {{ staff.lastName }}</td>
            </ng-container>

            <ng-container matColumnDef="email">
              <th mat-header-cell *matHeaderCellDef>Email</th>
              <td mat-cell *matCellDef="let staff">{{ staff.email }}</td>
            </ng-container>

            <ng-container matColumnDef="role">
              <th mat-header-cell *matHeaderCellDef>Role</th>
              <td mat-cell *matCellDef="let staff">
                <span class="role-badge">{{ staff.role }}</span>
              </td>
            </ng-container>

            <ng-container matColumnDef="department">
              <th mat-header-cell *matHeaderCellDef>Department</th>
              <td mat-cell *matCellDef="let staff">{{ staff.department }}</td>
            </ng-container>

            <ng-container matColumnDef="actions">
              <th mat-header-cell *matHeaderCellDef>Actions</th>
              <td mat-cell *matCellDef="let staff">
                <button mat-icon-button color="primary" (click)="editStaff(staff)">
                  <mat-icon>edit</mat-icon>
                </button>
                <button mat-icon-button color="warn" (click)="deleteStaff(staff)">
                  <mat-icon>delete</mat-icon>
                </button>
              </td>
            </ng-container>

            <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
            <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
          </table>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .staff-container {
      padding: 20px;
    }

    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
    }

    h1 {
      margin: 0;
      color: #3f51b5;
    }

    .search-field {
      width: 100%;
      margin-bottom: 20px;
    }

    table {
      width: 100%;
    }

    .role-badge {
      background-color: #e3f2fd;
      color: #1976d2;
      padding: 4px 12px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 500;
    }
  `]
})
export class StaffManagementComponent implements OnInit {
  staffList: Staff[] = [];
  displayedColumns: string[] = ['id', 'name', 'email', 'role', 'department', 'actions'];
  searchText: string = '';

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
    this.loadStaff();
  }

  loadStaff(): void {
    // Mock data - replace with actual API call
    this.staffList = [
      { id: 1, firstName: 'John', lastName: 'Doe', email: 'john.doe@school.com', role: 'Teacher', department: 'Mathematics' },
      { id: 2, firstName: 'Jane', lastName: 'Smith', email: 'jane.smith@school.com', role: 'Principal', department: 'Administration' },
      { id: 3, firstName: 'Mike', lastName: 'Johnson', email: 'mike.j@school.com', role: 'Teacher', department: 'Science' }
    ];
  }

  addStaff(): void {
    console.log('Add staff clicked');
  }

  editStaff(staff: Staff): void {
    console.log('Edit staff:', staff);
  }

  deleteStaff(staff: Staff): void {
    console.log('Delete staff:', staff);
  }
}
