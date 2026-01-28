import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { StudentService, GuardianService, EnrollmentService, Student, Guardian, Enrollment, Address } from 'sma-shared-lib';
import { MatSnackBar } from '@angular/material/snack-bar';

interface SchoolContext {
  schoolId: number;
  schoolName: string;
  schoolCode: string;
}

@Component({
  selector: 'app-student-detail',
  templateUrl: './student-detail.component.html',
  styleUrls: ['./student-detail.component.scss']
})
export class StudentDetailComponent implements OnInit {
  student?: Student;
  guardians: Guardian[] = [];
  addresses: Address[] = [];
  enrollments: Enrollment[] = [];
  studentId!: string;
  schoolId = 0;
  selectedSchool: SchoolContext | null = null;
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
    // Listen for school context from parent window
    window.addEventListener('message', (event) => {
      if (event.origin !== 'http://localhost:4300') {
        return;
      }
      
      if (event.data && event.data.type === 'SCHOOL_CONTEXT') {
        console.log('Received school context in student detail:', event.data);
        this.selectedSchool = event.data.school;
        
        if (this.selectedSchool) {
          this.schoolId = this.selectedSchool.schoolId;
          // Load details once we have schoolId
          if (this.studentId) {
            this.loadStudentDetails();
          }
        }
      }
    });
    
    // Request context from parent
    if (window.parent && window.parent !== window) {
      window.parent.postMessage({ type: 'REQUEST_CONTEXT' }, 'http://localhost:4300');
    }
    
    this.studentId = this.route.snapshot.paramMap.get('id')!;
  }

  loadStudentDetails(): void {
    if (!this.schoolId || !this.studentId) return;

    this.studentService.getStudent(this.schoolId, this.studentId).subscribe({
      next: (student) => {
        this.student = student;
        // Extract guardians and addresses from student response
        this.guardians = (student as any).guardians || [];
        this.addresses = (student as any).addresses || [];
        this.loadEnrollments();
      },
      error: (error) => {
        console.error('Error loading student:', error);
        this.snackBar.open('Error loading student details', 'Close', { duration: 3000 });
        this.loading = false;
      }
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
