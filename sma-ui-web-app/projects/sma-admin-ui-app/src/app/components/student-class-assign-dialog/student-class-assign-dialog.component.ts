import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import {
  StudentClassSectionService,
  StudentClassSectionRequest,
  StudentClassSectionResponse,
  StudentService,
  Student,
  ClassMasterResponse,
  SectionMasterResponse
} from 'sma-shared-lib';

@Component({
  selector: 'app-student-class-assign-dialog',
  templateUrl: './student-class-assign-dialog.component.html',
  styleUrls: ['./student-class-assign-dialog.component.scss']
})
export class StudentClassAssignDialogComponent implements OnInit {
  assignForm: FormGroup;
  students: Student[] = [];
  loadingStudents = false;
  saving = false;
  isEdit = false;

  constructor(
    private fb: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: {
      schoolId: number;
      academicYearId: number;
      classId: number;
      sectionId?: number;
      classes: ClassMasterResponse[];
      sections: SectionMasterResponse[];
      student?: StudentClassSectionResponse;
      isEdit?: boolean;
    },
    private dialogRef: MatDialogRef<StudentClassAssignDialogComponent>,
    private studentClassSectionService: StudentClassSectionService,
    private studentService: StudentService,
    private snackBar: MatSnackBar
  ) {
    this.isEdit = data.isEdit || false;
    
    this.assignForm = this.fb.group({
      studentId: [null, Validators.required],
      classId: [data.classId, Validators.required],
      sectionId: [data.sectionId, Validators.required],
      rollNumber: [''],
      enrollmentDate: [new Date()],
      remarks: ['']
    });

    if (this.isEdit && data.student) {
      this.assignForm.patchValue({
        studentId: data.student.studentId,
        classId: data.student.classId,
        sectionId: data.student.sectionId,
        rollNumber: data.student.rollNumber,
        enrollmentDate: data.student.enrollmentDate ? new Date(data.student.enrollmentDate) : new Date(),
        remarks: data.student.remarks
      });
    }
  }

  ngOnInit(): void {
    if (!this.isEdit) {
      this.loadUnassignedStudents();
    }
  }

  loadUnassignedStudents(): void {
    this.loadingStudents = true;
    this.studentService.getAllStudents(this.data.schoolId).subscribe({
      next: (students: Student[]) => {
        // In a real scenario, you'd filter out already assigned students
        // For now, showing all students
        this.students = students.filter((s: Student) => s.status === 'ACTIVE');
        this.loadingStudents = false;
      },
      error: (error: any) => {
        console.error('Error loading students:', error);
        this.snackBar.open('Error loading students', 'Close', { duration: 3000 });
        this.loadingStudents = false;
      }
    });
  }

  onSubmit(): void {
    if (this.assignForm.invalid) {
      this.assignForm.markAllAsTouched();
      return;
    }

    this.saving = true;
    const formValue = this.assignForm.value;
    
    const request: StudentClassSectionRequest = {
      studentId: formValue.studentId,
      schoolId: this.data.schoolId,
      academicYearId: this.data.academicYearId,
      classId: formValue.classId,
      sectionId: formValue.sectionId,
      rollNumber: formValue.rollNumber || undefined,
      enrollmentDate: formValue.enrollmentDate ? 
        new Date(formValue.enrollmentDate).toISOString().split('T')[0] : undefined,
      remarks: formValue.remarks || undefined
    };

    const operation = this.isEdit && this.data.student ?
      this.studentClassSectionService.updateStudentClassSection(this.data.student.mappingId, request) :
      this.studentClassSectionService.assignStudentToClassSection(request);

    operation.subscribe({
      next: () => {
        this.snackBar.open(
          this.isEdit ? 'Student updated successfully' : 'Student assigned successfully',
          'Close',
          { duration: 3000 }
        );
        this.dialogRef.close(true);
      },
      error: (error) => {
        console.error('Error saving:', error);
        this.snackBar.open(
          error.error?.message || 'Error saving student assignment',
          'Close',
          { duration: 3000 }
        );
        this.saving = false;
      }
    });
  }

  cancel(): void {
    this.dialogRef.close();
  }

  hasError(field: string, error: string): boolean {
    const control = this.assignForm.get(field);
    return !!(control && control.hasError(error) && (control.dirty || control.touched));
  }
}
