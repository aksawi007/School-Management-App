import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { StudentClassSectionService, StudentClassSectionResponse } from 'sma-shared-lib';

@Component({
  selector: 'app-class-students-dialog',
  templateUrl: './class-students-dialog.component.html',
  styleUrls: ['./class-students-dialog.component.scss']
})
export class ClassStudentsDialogComponent implements OnInit {
  students: StudentClassSectionResponse[] = [];
  loading = true;
  displayedColumns: string[] = ['admissionNumber', 'studentName', 'rollNumber', 'sectionName', 'enrollmentDate'];

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: {
      academicYearId: number;
      classId: number;
      sectionId?: number;
      className: string;
      sectionName?: string;
    },
    private dialogRef: MatDialogRef<ClassStudentsDialogComponent>,
    private studentClassSectionService: StudentClassSectionService
  ) {}

  ngOnInit(): void {
    this.loadStudents();
  }

  loadStudents(): void {
    this.loading = true;
    this.studentClassSectionService.getStudentsByClassAndSection(
      this.data.academicYearId,
      this.data.classId,
      this.data.sectionId
    ).subscribe({
      next: (students: StudentClassSectionResponse[]) => {
        this.students = students;
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading students:', error);
        this.loading = false;
      }
    });
  }

  close(): void {
    this.dialogRef.close();
  }
}
