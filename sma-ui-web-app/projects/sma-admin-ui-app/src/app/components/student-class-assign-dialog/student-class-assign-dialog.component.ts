import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
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
import { Observable } from 'rxjs';
import { startWith, map } from 'rxjs/operators';

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
  studentSearchControl: FormControl = new FormControl('');
  filteredStudents$: Observable<Student[]> | undefined;

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
      // If section is selected, precompute next roll number based on existing students
      if (this.assignForm.get('sectionId')?.value) {
        this.prefillNextRollNumber(this.assignForm.get('classId')?.value, this.assignForm.get('sectionId')?.value);
      }
    }
  }

  /**
   * Load assigned students for the class/section and compute next roll number
   */
  private prefillNextRollNumber(classId: number, sectionId: number): void {
    if (!this.data.academicYearId || !classId) return;

    this.studentClassSectionService.getStudentsByClassAndSection(
      this.data.academicYearId,
      classId,
      sectionId
    ).subscribe({
      next: (students) => {
        const max = students
          .map(s => s.rollNumber)
          .filter((r): r is string => typeof r === 'string' && r.length > 0)
          .map(r => {
            const m = r.match(/\d+/g);
            return m ? parseInt(m.join('')) : NaN;
          })
          .filter(n => !isNaN(n));

        const next = (max.length > 0 ? Math.max(...max) : 0) + 1;
        this.assignForm.patchValue({ rollNumber: next.toString() });
      },
      error: (err) => {
        console.error('Error computing next roll number:', err);
      }
    });
  }

  loadUnassignedStudents(): void {
    this.loadingStudents = true;
    this.studentService.getAllStudents(this.data.schoolId).subscribe({
      next: (students: Student[]) => {
        // In a real scenario, you'd filter out already assigned students
        // For now, showing all students
        this.students = students.filter((s: Student) => s.status === 'ACTIVE');
        this.initStudentFilter();
        this.loadingStudents = false;
      },
      error: (error: any) => {
        console.error('Error loading students:', error);
        this.snackBar.open('Error loading students', 'Close', { duration: 3000 });
        this.loadingStudents = false;
      }
    });
  }

  private initStudentFilter(): void {
    this.filteredStudents$ = this.studentSearchControl.valueChanges.pipe(
      startWith(this.studentSearchControl.value || ''),
      map(value => typeof value === 'string' ? value : (value ? (value as any).studentName || '' : '')),
      map(name => name ? this._filterStudents(name) : this.students.slice())
    );
  }

  private _filterStudents(name: string): Student[] {
    const filterValue = name.toLowerCase();
    return this.students.filter(student => (
      (student.firstName || '').toLowerCase().includes(filterValue) ||
      (student.lastName || '').toLowerCase().includes(filterValue) ||
      (student.admissionNo || '').toLowerCase().includes(filterValue)
    ));
  }

  displayStudentFn(student?: Student): string {
    return student ? `${student.admissionNo} - ${student.firstName} ${student.lastName}` : '';
  }

  onStudentSelected(student: Student | null): void {
    if (student) {
      this.assignForm.patchValue({ studentId: student.id });
    } else {
      this.assignForm.patchValue({ studentId: null });
    }
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
