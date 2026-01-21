import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';

interface AcademicYear {
  id: number;
  yearName: string;
  startDate: string;
  endDate: string;
  currentYear: boolean;
  description: string;
}

@Component({
  selector: 'app-academic-year',
  template: `
    <div class="academic-container">
      <div class="header">
        <h1>Academic Year Management</h1>
        <button mat-raised-button color="primary" (click)="openYearForm()">
          <mat-icon>add</mat-icon>
          Add Academic Year
        </button>
      </div>

      <mat-card *ngIf="!showForm">
        <mat-card-content>
          <table mat-table [dataSource]="academicYears" class="mat-elevation-z2">
            <ng-container matColumnDef="yearName">
              <th mat-header-cell *matHeaderCellDef>Academic Year</th>
              <td mat-cell *matCellDef="let year">
                <strong>{{ year.yearName }}</strong>
                <mat-chip *ngIf="year.currentYear" class="current-chip">Current</mat-chip>
              </td>
            </ng-container>

            <ng-container matColumnDef="startDate">
              <th mat-header-cell *matHeaderCellDef>Start Date</th>
              <td mat-cell *matCellDef="let year">{{ year.startDate | date:'mediumDate' }}</td>
            </ng-container>

            <ng-container matColumnDef="endDate">
              <th mat-header-cell *matHeaderCellDef>End Date</th>
              <td mat-cell *matCellDef="let year">{{ year.endDate | date:'mediumDate' }}</td>
            </ng-container>

            <ng-container matColumnDef="description">
              <th mat-header-cell *matHeaderCellDef>Description</th>
              <td mat-cell *matCellDef="let year">{{ year.description }}</td>
            </ng-container>

            <ng-container matColumnDef="actions">
              <th mat-header-cell *matHeaderCellDef>Actions</th>
              <td mat-cell *matCellDef="let year">
                <button mat-icon-button color="primary" (click)="editYear(year)">
                  <mat-icon>edit</mat-icon>
                </button>
                <button mat-icon-button color="accent" 
                        (click)="setCurrentYear(year)" 
                        [disabled]="year.currentYear">
                  <mat-icon>check_circle</mat-icon>
                </button>
                <button mat-icon-button color="warn" 
                        (click)="deleteYear(year)"
                        [disabled]="year.currentYear">
                  <mat-icon>delete</mat-icon>
                </button>
              </td>
            </ng-container>

            <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
            <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
          </table>
        </mat-card-content>
      </mat-card>

      <mat-card *ngIf="showForm">
        <mat-card-header>
          <mat-card-title>{{ isEditMode ? 'Edit' : 'Add' }} Academic Year</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <form [formGroup]="yearForm" (ngSubmit)="saveYear()">
            <div class="form-grid">
              <mat-form-field>
                <mat-label>Year Name (e.g., 2024-2025)</mat-label>
                <input matInput formControlName="yearName" required>
              </mat-form-field>

              <mat-form-field>
                <mat-label>Start Date</mat-label>
                <input matInput [matDatepicker]="startPicker" formControlName="startDate" required>
                <mat-datepicker-toggle matSuffix [for]="startPicker"></mat-datepicker-toggle>
                <mat-datepicker #startPicker></mat-datepicker>
              </mat-form-field>

              <mat-form-field>
                <mat-label>End Date</mat-label>
                <input matInput [matDatepicker]="endPicker" formControlName="endDate" required>
                <mat-datepicker-toggle matSuffix [for]="endPicker"></mat-datepicker-toggle>
                <mat-datepicker #endPicker></mat-datepicker>
              </mat-form-field>

              <div class="checkbox-field">
                <mat-checkbox formControlName="currentYear">Set as Current Year</mat-checkbox>
              </div>

              <mat-form-field class="full-width">
                <mat-label>Description</mat-label>
                <textarea matInput formControlName="description" rows="3"></textarea>
              </mat-form-field>
            </div>

            <div class="form-actions">
              <button mat-raised-button type="button" (click)="cancelForm()">Cancel</button>
              <button mat-raised-button color="primary" type="submit" [disabled]="!yearForm.valid">
                {{ isEditMode ? 'Update' : 'Create' }}
              </button>
            </div>
          </form>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .academic-container {
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

    table {
      width: 100%;
    }

    .current-chip {
      margin-left: 10px;
      background-color: #4caf50 !important;
      color: white !important;
      font-size: 10px;
      padding: 4px 8px;
      height: 20px;
      line-height: 20px;
    }

    .form-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 16px;
      margin-bottom: 20px;
    }

    .full-width {
      grid-column: 1 / -1;
    }

    .checkbox-field {
      display: flex;
      align-items: center;
    }

    .form-actions {
      display: flex;
      gap: 10px;
      justify-content: flex-end;
    }
  `]
})
export class AcademicYearComponent implements OnInit {
  academicYears: AcademicYear[] = [];
  displayedColumns: string[] = ['yearName', 'startDate', 'endDate', 'description', 'actions'];
  showForm = false;
  isEditMode = false;
  selectedYear: AcademicYear | null = null;
  yearForm: FormGroup;

  constructor(
    private http: HttpClient,
    private fb: FormBuilder,
    private snackBar: MatSnackBar
  ) {
    this.yearForm = this.fb.group({
      yearName: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      currentYear: [false],
      description: ['']
    });
  }

  ngOnInit(): void {
    this.loadAcademicYears();
  }

  loadAcademicYears(): void {
    this.http.get<AcademicYear[]>('/api/academic-year/getAll').subscribe({
      next: (data) => {
        this.academicYears = data;
      },
      error: (err) => {
        console.error('Error loading academic years:', err);
        this.showMessage('Error loading academic years');
      }
    });
  }

  openYearForm(): void {
    this.showForm = true;
    this.isEditMode = false;
    this.yearForm.reset({ currentYear: false });
  }

  editYear(year: AcademicYear): void {
    this.showForm = true;
    this.isEditMode = true;
    this.selectedYear = year;
    this.yearForm.patchValue({
      ...year,
      startDate: new Date(year.startDate),
      endDate: new Date(year.endDate)
    });
  }

  saveYear(): void {
    if (this.yearForm.valid) {
      const formData = {
        ...this.yearForm.value,
        startDate: this.formatDate(this.yearForm.value.startDate),
        endDate: this.formatDate(this.yearForm.value.endDate)
      };
      
      if (this.isEditMode && this.selectedYear) {
        this.http.put<AcademicYear>(`/api/academic-year/update?yearId=${this.selectedYear.id}`, formData)
          .subscribe({
            next: () => {
              this.showMessage('Academic year updated successfully');
              this.cancelForm();
              this.loadAcademicYears();
            },
            error: (err) => {
              console.error('Error updating academic year:', err);
              this.showMessage('Error updating academic year');
            }
          });
      } else {
        this.http.post<AcademicYear>('/api/academic-year/create', formData).subscribe({
          next: () => {
            this.showMessage('Academic year created successfully');
            this.cancelForm();
            this.loadAcademicYears();
          },
          error: (err) => {
            console.error('Error creating academic year:', err);
            this.showMessage('Error creating academic year');
          }
        });
      }
    }
  }

  setCurrentYear(year: AcademicYear): void {
    if (confirm(`Set ${year.yearName} as the current academic year?`)) {
      const updateData = {
        ...year,
        currentYear: true
      };
      this.http.put(`/api/academic-year/update?yearId=${year.id}`, updateData).subscribe({
        next: () => {
          this.showMessage('Current academic year updated');
          this.loadAcademicYears();
        },
        error: (err) => {
          console.error('Error setting current year:', err);
          this.showMessage('Error setting current year');
        }
      });
    }
  }

  deleteYear(year: AcademicYear): void {
    if (confirm(`Are you sure you want to delete academic year ${year.yearName}?`)) {
      this.http.delete(`/api/academic-year/delete?yearId=${year.id}`).subscribe({
        next: () => {
          this.showMessage('Academic year deleted successfully');
          this.loadAcademicYears();
        },
        error: (err) => {
          console.error('Error deleting academic year:', err);
          this.showMessage('Error deleting academic year');
        }
      });
    }
  }

  cancelForm(): void {
    this.showForm = false;
    this.isEditMode = false;
    this.selectedYear = null;
    this.yearForm.reset({ currentYear: false });
  }

  formatDate(date: Date): string {
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  showMessage(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000
    });
  }
}
