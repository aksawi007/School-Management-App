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
  templateUrl: './academic-year.component.html',
  styleUrls: ['./academic-year.component.scss']
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
