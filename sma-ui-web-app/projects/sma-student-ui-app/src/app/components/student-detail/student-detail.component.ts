import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { StudentService, GuardianService, EnrollmentService, Student, Guardian, Enrollment } from 'sma-shared-lib';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-student-detail',
  templateUrl: './student-detail.component.html',
  styleUrls: ['./student-detail.component.scss']
})
export class StudentDetailComponent implements OnInit {
  student?: Student;
  guardians: Guardian[] = [];
  enrollments: Enrollment[] = [];
  studentId!: string;
  schoolId = ''; // TODO: Get from auth service or config
  loading = true;

  constructor(
    private studentService: StudentService,
    private guardianService: GuardianService,
    private enrollmentService: EnrollmentService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.studentId = this.route.snapshot.paramMap.get('id')!;
    this.loadStudentDetails();
  }

  loadStudentDetails(): void {
    if (!this.schoolId || !this.studentId) return;

    this.studentService.getStudent(this.schoolId, this.studentId).subscribe({
      next: (student) => {
        this.student = student;
        this.loadGuardians();
        this.loadEnrollments();
      },
      error: (error) => {
        console.error('Error loading student:', error);
        this.snackBar.open('Error loading student details', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  loadGuardians(): void {
    this.guardianService.getGuardians(this.schoolId, this.studentId).subscribe({
      next: (guardians) => {
        this.guardians = guardians;
      },
      error: (error) => console.error('Error loading guardians:', error)
    });
  }

  loadEnrollments(): void {
    this.enrollmentService.getEnrollments(this.schoolId, this.studentId).subscribe({
      next: (enrollments) => {
        this.enrollments = enrollments;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading enrollments:', error);
        this.loading = false;
      }
    });
  }

  editStudent(): void {
    this.router.navigate(['/students', this.studentId, 'edit']);
  }

  goBack(): void {
    this.router.navigate(['/students']);
  }
}
