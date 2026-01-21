import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface Student {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  grade: string;
  enrollmentDate: string;
}

@Component({
  selector: 'app-student-management',
  template: `
    <div class="student-container">
      <div class="header">
        <h1>Student Management</h1>
        <button mat-raised-button color="primary" (click)="addStudent()">
          <mat-icon>add</mat-icon>
          Add Student
        </button>
      </div>

      <mat-card>
        <mat-card-content>
          <mat-form-field class="search-field">
            <mat-label>Search Students</mat-label>
            <input matInput placeholder="Search by name, email, or grade" [(ngModel)]="searchText">
            <mat-icon matPrefix>search</mat-icon>
          </mat-form-field>

          <table mat-table [dataSource]="studentList" class="mat-elevation-z2">
            <ng-container matColumnDef="id">
              <th mat-header-cell *matHeaderCellDef>ID</th>
              <td mat-cell *matCellDef="let student">{{ student.id }}</td>
            </ng-container>

            <ng-container matColumnDef="name">
              <th mat-header-cell *matHeaderCellDef>Name</th>
              <td mat-cell *matCellDef="let student">{{ student.firstName }} {{ student.lastName }}</td>
            </ng-container>

            <ng-container matColumnDef="email">
              <th mat-header-cell *matHeaderCellDef>Email</th>
              <td mat-cell *matCellDef="let student">{{ student.email }}</td>
            </ng-container>

            <ng-container matColumnDef="grade">
              <th mat-header-cell *matHeaderCellDef>Grade</th>
              <td mat-cell *matCellDef="let student">
                <span class="grade-badge">{{ student.grade }}</span>
              </td>
            </ng-container>

            <ng-container matColumnDef="enrollmentDate">
              <th mat-header-cell *matHeaderCellDef>Enrollment Date</th>
              <td mat-cell *matCellDef="let student">{{ student.enrollmentDate }}</td>
            </ng-container>

            <ng-container matColumnDef="actions">
              <th mat-header-cell *matHeaderCellDef>Actions</th>
              <td mat-cell *matCellDef="let student">
                <button mat-icon-button color="primary" (click)="viewStudent(student)">
                  <mat-icon>visibility</mat-icon>
                </button>
                <button mat-icon-button color="primary" (click)="editStudent(student)">
                  <mat-icon>edit</mat-icon>
                </button>
                <button mat-icon-button color="warn" (click)="deleteStudent(student)">
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
    .student-container {
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

    .grade-badge {
      background-color: #e8f5e9;
      color: #2e7d32;
      padding: 4px 12px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 500;
    }
  `]
})
export class StudentManagementComponent implements OnInit {
  studentList: Student[] = [];
  displayedColumns: string[] = ['id', 'name', 'email', 'grade', 'enrollmentDate', 'actions'];
  searchText: string = '';

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
    this.loadStudents();
  }

  loadStudents(): void {
    // Mock data - replace with actual API call
    this.studentList = [
      { id: 1, firstName: 'Alice', lastName: 'Williams', email: 'alice.w@school.com', grade: '10th', enrollmentDate: '2024-09-01' },
      { id: 2, firstName: 'Bob', lastName: 'Brown', email: 'bob.b@school.com', grade: '9th', enrollmentDate: '2024-09-01' },
      { id: 3, firstName: 'Charlie', lastName: 'Davis', email: 'charlie.d@school.com', grade: '11th', enrollmentDate: '2023-09-01' }
    ];
  }

  addStudent(): void {
    console.log('Add student clicked');
  }

  viewStudent(student: Student): void {
    console.log('View student:', student);
  }

  editStudent(student: Student): void {
    console.log('Edit student:', student);
  }

  deleteStudent(student: Student): void {
    console.log('Delete student:', student);
  }
}
