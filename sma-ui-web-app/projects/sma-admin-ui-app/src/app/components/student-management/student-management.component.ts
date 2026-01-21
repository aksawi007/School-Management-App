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
  templateUrl: './student-management.component.html',
  styleUrls: ['./student-management.component.scss']
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
