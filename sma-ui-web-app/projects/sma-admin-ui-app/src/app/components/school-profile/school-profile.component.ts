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
  templateUrl: './school-profile.component.html',
  styleUrls: ['./school-profile.component.scss']
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
