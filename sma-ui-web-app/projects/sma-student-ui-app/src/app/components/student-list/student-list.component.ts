import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { StudentService, Student } from 'sma-shared-lib';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-student-list',
  templateUrl: './student-list.component.html',
  styleUrls: ['./student-list.component.scss']
})
export class StudentListComponent implements OnInit {
  students: Student[] = [];
  displayedColumns: string[] = ['studentId', 'fullName', 'admissionNumber', 'studentStatus', 'actions'];
  loading = false;
  schoolId = ''; // TODO: Get from auth service or config

  constructor(
    private studentService: StudentService,
    private router: Router,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.loadStudents();
  }

  loadStudents(): void {
    if (!this.schoolId) {
      this.snackBar.open('Please configure school ID', 'Close', { duration: 3000 });
      return;
    }

    this.loading = true;
    this.studentService.getAllStudents(this.schoolId).subscribe({
      next: (students) => {
        this.students = students;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading students:', error);
        this.snackBar.open('Error loading students', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  viewStudent(student: Student): void {
    this.router.navigate(['/students', student.id]);
  }

  editStudent(student: Student): void {
    this.router.navigate(['/students', student.id, 'edit']);
  }

  createStudent(): void {
    this.router.navigate(['/students/new']);
  }
}
