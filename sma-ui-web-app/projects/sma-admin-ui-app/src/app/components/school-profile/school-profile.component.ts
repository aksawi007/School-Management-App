import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';

interface SchoolProfile {
  id: number;
  schoolName: string;
  schoolCode: string;
  address: string;
  city: string;
  state: string;
  country: string;
  pincode: string;
  phone: string;
  email: string;
  website: string;
  principalName: string;
  establishedYear: string;
  affiliationNumber: string;
  board: string;
}

@Component({
  selector: 'app-school-profile',
  template: `
    <div class="school-container">
      <div class="header">
        <h1>School Profile Management</h1>
        <button mat-raised-button color="primary" (click)="openSchoolForm()">
          <mat-icon>add</mat-icon>
          Add School
        </button>
      </div>

      <mat-card *ngIf="!showForm">
        <mat-card-content>
          <table mat-table [dataSource]="schools" class="mat-elevation-z2">
            <ng-container matColumnDef="schoolCode">
              <th mat-header-cell *matHeaderCellDef>Code</th>
              <td mat-cell *matCellDef="let school">{{ school.schoolCode }}</td>
            </ng-container>

            <ng-container matColumnDef="schoolName">
              <th mat-header-cell *matHeaderCellDef>School Name</th>
              <td mat-cell *matCellDef="let school">{{ school.schoolName }}</td>
            </ng-container>

            <ng-container matColumnDef="principalName">
              <th mat-header-cell *matHeaderCellDef>Principal</th>
              <td mat-cell *matCellDef="let school">{{ school.principalName }}</td>
            </ng-container>

            <ng-container matColumnDef="board">
              <th mat-header-cell *matHeaderCellDef>Board</th>
              <td mat-cell *matCellDef="let school">
                <span class="board-badge">{{ school.board }}</span>
              </td>
            </ng-container>

            <ng-container matColumnDef="contact">
              <th mat-header-cell *matHeaderCellDef>Contact</th>
              <td mat-cell *matCellDef="let school">
                <div>{{ school.phone }}</div>
                <div class="email">{{ school.email }}</div>
              </td>
            </ng-container>

            <ng-container matColumnDef="actions">
              <th mat-header-cell *matHeaderCellDef>Actions</th>
              <td mat-cell *matCellDef="let school">
                <button mat-icon-button color="primary" (click)="viewSchool(school)">
                  <mat-icon>visibility</mat-icon>
                </button>
                <button mat-icon-button color="primary" (click)="editSchool(school)">
                  <mat-icon>edit</mat-icon>
                </button>
                <button mat-icon-button color="warn" (click)="deleteSchool(school)">
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
          <mat-card-title>{{ isEditMode ? 'Edit' : 'Add' }} School Profile</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <form [formGroup]="schoolForm" (ngSubmit)="saveSchool()">
            <div class="form-grid">
              <mat-form-field>
                <mat-label>School Name</mat-label>
                <input matInput formControlName="schoolName" required>
              </mat-form-field>

              <mat-form-field>
                <mat-label>School Code</mat-label>
                <input matInput formControlName="schoolCode" required>
              </mat-form-field>

              <mat-form-field>
                <mat-label>Principal Name</mat-label>
                <input matInput formControlName="principalName" required>
              </mat-form-field>

              <mat-form-field>
                <mat-label>Board</mat-label>
                <mat-select formControlName="board" required>
                  <mat-option value="CBSE">CBSE</mat-option>
                  <mat-option value="ICSE">ICSE</mat-option>
                  <mat-option value="State Board">State Board</mat-option>
                  <mat-option value="IB">IB</mat-option>
                </mat-select>
              </mat-form-field>

              <mat-form-field>
                <mat-label>Affiliation Number</mat-label>
                <input matInput formControlName="affiliationNumber">
              </mat-form-field>

              <mat-form-field>
                <mat-label>Established Year</mat-label>
                <input matInput formControlName="establishedYear" type="number">
              </mat-form-field>

              <mat-form-field class="full-width">
                <mat-label>Address</mat-label>
                <textarea matInput formControlName="address" rows="2"></textarea>
              </mat-form-field>

              <mat-form-field>
                <mat-label>City</mat-label>
                <input matInput formControlName="city" required>
              </mat-form-field>

              <mat-form-field>
                <mat-label>State</mat-label>
                <input matInput formControlName="state" required>
              </mat-form-field>

              <mat-form-field>
                <mat-label>Country</mat-label>
                <input matInput formControlName="country" required>
              </mat-form-field>

              <mat-form-field>
                <mat-label>Pincode</mat-label>
                <input matInput formControlName="pincode" required>
              </mat-form-field>

              <mat-form-field>
                <mat-label>Phone</mat-label>
                <input matInput formControlName="phone" required>
              </mat-form-field>

              <mat-form-field>
                <mat-label>Email</mat-label>
                <input matInput formControlName="email" type="email" required>
              </mat-form-field>

              <mat-form-field>
                <mat-label>Website</mat-label>
                <input matInput formControlName="website">
              </mat-form-field>
            </div>

            <div class="form-actions">
              <button mat-raised-button type="button" (click)="cancelForm()">Cancel</button>
              <button mat-raised-button color="primary" type="submit" [disabled]="!schoolForm.valid">
                {{ isEditMode ? 'Update' : 'Create' }}
              </button>
            </div>
          </form>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .school-container {
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

    .board-badge {
      background-color: #e3f2fd;
      color: #1976d2;
      padding: 4px 12px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 500;
    }

    .email {
      font-size: 12px;
      color: #666;
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

    .form-actions {
      display: flex;
      gap: 10px;
      justify-content: flex-end;
    }
  `]
})
export class SchoolProfileComponent implements OnInit {
  schools: SchoolProfile[] = [];
  displayedColumns: string[] = ['schoolCode', 'schoolName', 'principalName', 'board', 'contact', 'actions'];
  showForm = false;
  isEditMode = false;
  selectedSchool: SchoolProfile | null = null;
  schoolForm: FormGroup;

  constructor(
    private http: HttpClient,
    private fb: FormBuilder,
    private snackBar: MatSnackBar
  ) {
    this.schoolForm = this.fb.group({
      schoolName: ['', Validators.required],
      schoolCode: ['', Validators.required],
      address: ['', Validators.required],
      city: ['', Validators.required],
      state: ['', Validators.required],
      country: ['', Validators.required],
      pincode: ['', Validators.required],
      phone: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      website: [''],
      principalName: ['', Validators.required],
      establishedYear: [''],
      affiliationNumber: [''],
      board: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadSchools();
  }

  loadSchools(): void {
    this.http.get<SchoolProfile[]>('/api/school/profile/getAll').subscribe({
      next: (data) => {
        this.schools = data;
      },
      error: (err) => {
        console.error('Error loading schools:', err);
        this.showMessage('Error loading schools');
      }
    });
  }

  openSchoolForm(): void {
    this.showForm = true;
    this.isEditMode = false;
    this.schoolForm.reset();
  }

  viewSchool(school: SchoolProfile): void {
    console.log('View school:', school);
  }

  editSchool(school: SchoolProfile): void {
    this.showForm = true;
    this.isEditMode = true;
    this.selectedSchool = school;
    this.schoolForm.patchValue(school);
  }

  saveSchool(): void {
    if (this.schoolForm.valid) {
      const formData = this.schoolForm.value;
      
      if (this.isEditMode && this.selectedSchool) {
        this.http.put<SchoolProfile>(`/api/school/profile/update?schoolId=${this.selectedSchool.id}`, formData)
          .subscribe({
            next: () => {
              this.showMessage('School updated successfully');
              this.cancelForm();
              this.loadSchools();
            },
            error: (err) => {
              console.error('Error updating school:', err);
              this.showMessage('Error updating school');
            }
          });
      } else {
        this.http.post<SchoolProfile>('/api/school/profile/create', formData).subscribe({
          next: () => {
            this.showMessage('School created successfully');
            this.cancelForm();
            this.loadSchools();
          },
          error: (err) => {
            console.error('Error creating school:', err);
            this.showMessage('Error creating school');
          }
        });
      }
    }
  }

  deleteSchool(school: SchoolProfile): void {
    if (confirm(`Are you sure you want to delete ${school.schoolName}?`)) {
      this.http.delete(`/api/school/profile/delete?schoolId=${school.id}`).subscribe({
        next: () => {
          this.showMessage('School deleted successfully');
          this.loadSchools();
        },
        error: (err) => {
          console.error('Error deleting school:', err);
          this.showMessage('Error deleting school');
        }
      });
    }
  }

  cancelForm(): void {
    this.showForm = false;
    this.isEditMode = false;
    this.selectedSchool = null;
    this.schoolForm.reset();
  }

  showMessage(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000
    });
  }
}
