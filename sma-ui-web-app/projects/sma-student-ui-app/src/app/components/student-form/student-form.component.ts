import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { StudentService, Student, StudentRequest, GENDER, STUDENT_STATUS } from 'sma-shared-lib';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-student-form',
  templateUrl: './student-form.component.html',
  styleUrls: ['./student-form.component.scss']
})
export class StudentFormComponent implements OnInit {
  studentForm: FormGroup;
  isEditMode = false;
  studentId?: string;
  schoolId = ''; // TODO: Get from auth service or config
  
  genders = Object.values(GENDER);
  statuses = Object.values(STUDENT_STATUS);

  constructor(
    private fb: FormBuilder,
    private studentService: StudentService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.studentForm = this.createForm();
  }

  ngOnInit(): void {
    this.studentId = this.route.snapshot.paramMap.get('id') || undefined;
    this.isEditMode = !!this.studentId && this.route.snapshot.url[this.route.snapshot.url.length - 1].path === 'edit';

    if (this.isEditMode && this.studentId) {
      this.loadStudent();
    }
  }

  createForm(): FormGroup {
    return this.fb.group({
      studentId: ['', Validators.required],
      firstName: ['', Validators.required],
      middleName: [''],
      lastName: ['', Validators.required],
      dateOfBirth: ['', Validators.required],
      gender: ['', Validators.required],
      bloodGroup: [''],
      email: ['', Validators.email],
      phoneNumber: [''],
      addressLine1: ['', Validators.required],
      addressLine2: [''],
      city: ['', Validators.required],
      state: ['', Validators.required],
      postalCode: ['', Validators.required],
      admissionDate: ['', Validators.required],
      admissionNumber: ['', Validators.required],
      studentStatus: [STUDENT_STATUS.ACTIVE, Validators.required],
      aadharNumber: ['']
    });
  }

  loadStudent(): void {
    if (!this.schoolId || !this.studentId) return;

    this.studentService.getStudent(this.schoolId, this.studentId).subscribe({
      next: (student) => {
        this.studentForm.patchValue(student);
      },
      error: (error) => {
        console.error('Error loading student:', error);
        this.snackBar.open('Error loading student details', 'Close', { duration: 3000 });
      }
    });
  }

  onSubmit(): void {
    if (this.studentForm.invalid || !this.schoolId) {
      this.snackBar.open('Please fill all required fields', 'Close', { duration: 3000 });
      return;
    }

    const studentData: StudentRequest = {
      ...this.studentForm.value,
      schoolId: this.schoolId
    };

    const operation = this.isEditMode && this.studentId
      ? this.studentService.updateStudent(this.schoolId, this.studentId, studentData)
      : this.studentService.createStudent(this.schoolId, studentData);

    operation.subscribe({
      next: () => {
        this.snackBar.open(
          `Student ${this.isEditMode ? 'updated' : 'created'} successfully`,
          'Close',
          { duration: 3000 }
        );
        this.router.navigate(['/students']);
      },
      error: (error) => {
        console.error('Error saving student:', error);
        this.snackBar.open('Error saving student', 'Close', { duration: 3000 });
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/students']);
  }
}
