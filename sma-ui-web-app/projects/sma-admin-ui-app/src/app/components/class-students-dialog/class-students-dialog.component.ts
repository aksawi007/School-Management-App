import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { StudentClassSectionService, StudentClassSectionResponse } from 'sma-shared-lib';
import { StudentClassAssignDialogComponent } from '../student-class-assign-dialog/student-class-assign-dialog.component';

@Component({
  selector: 'app-class-students-dialog',
  templateUrl: './class-students-dialog.component.html',
  styleUrls: ['./class-students-dialog.component.scss']
})
export class ClassStudentsDialogComponent implements OnInit {
  students: StudentClassSectionResponse[] = [];
  loading = true;
  displayedColumns: string[] = ['admissionNumber', 'studentName', 'rollNumber', 'sectionName', 'enrollmentDate', 'actions'];

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: {
      academicYearId: number;
      classId: number;
      sectionId?: number;
      className: string;
      sectionName?: string;
      schoolId?: number;
    },
    private dialogRef: MatDialogRef<ClassStudentsDialogComponent>,
    private studentClassSectionService: StudentClassSectionService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
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

  assignStudent(): void {
    const dialogRef = this.dialog.open(StudentClassAssignDialogComponent, {
      width: '600px',
      data: {
        schoolId: this.data.schoolId,
        academicYearId: this.data.academicYearId,
        classId: this.data.classId,
        sectionId: this.data.sectionId,
        classes: [],
        sections: []
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadStudents();
      }
    });
  }

  editStudent(student: StudentClassSectionResponse): void {
    const dialogRef = this.dialog.open(StudentClassAssignDialogComponent, {
      width: '600px',
      data: {
        schoolId: this.data.schoolId,
        academicYearId: this.data.academicYearId,
        classId: this.data.classId,
        sectionId: this.data.sectionId,
        classes: [],
        sections: [],
        student: student,
        isEdit: true
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadStudents();
      }
    });
  }
}
