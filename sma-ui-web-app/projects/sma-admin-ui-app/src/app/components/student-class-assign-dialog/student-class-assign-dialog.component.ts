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
  SectionMasterResponse,
  SectionMasterService
} from 'sma-shared-lib';
import { Observable, of } from 'rxjs';
import { startWith, map, debounceTime, distinctUntilChanged, switchMap, catchError } from 'rxjs/operators';

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
  availableSections: SectionMasterResponse[] = [];

  constructor(
    private fb: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: {
      schoolId: number;
      academicYearId: number;
      classId: number;
      sectionId?: number;
      className?: string;
      sectionName?: string;
      classes: ClassMasterResponse[];
      sections: SectionMasterResponse[];
      student?: StudentClassSectionResponse;
      isEdit?: boolean;
      preselectedClassSection?: boolean;
    },
    private dialogRef: MatDialogRef<StudentClassAssignDialogComponent>,
    private studentClassSectionService: StudentClassSectionService,
    private studentService: StudentService,
    private sectionService: SectionMasterService,
    private snackBar: MatSnackBar
  ) {
    this.isEdit = data.isEdit || false;
    this.availableSections = data.sections || [];
    
    // Initialize form with student data if editing, otherwise use passed classId/sectionId
    // Ensure IDs are numbers for proper comparison in mat-select
    const initialClassId = (this.isEdit && data.student) ? Number(data.student.classId) : Number(data.classId);
    const initialSectionId = (this.isEdit && data.student) ? Number(data.student.sectionId) : (data.sectionId ? Number(data.sectionId) : null);
    
    this.assignForm = this.fb.group({
      studentId: [(this.isEdit && data.student) ? data.student.studentId : null, Validators.required],
      classId: [initialClassId, Validators.required],
      sectionId: [initialSectionId, Validators.required],
      rollNumber: [(this.isEdit && data.student) ? data.student.rollNumber : ''],
      enrollmentDate: [(this.isEdit && data.student && data.student.enrollmentDate) ? 
        new Date(data.student.enrollmentDate) : new Date()],
      remarks: [(this.isEdit && data.student) ? data.student.remarks : '']
    });
  }

  ngOnInit(): void {
    console.log('Dialog initialized with data:', {
      isEdit: this.isEdit,
      preselectedClassSection: this.data.preselectedClassSection,
      classId: this.assignForm.get('classId')?.value,
      sectionId: this.assignForm.get('sectionId')?.value,
      availableSections: this.availableSections,
      classes: this.data.classes
    });
    
    // Check types
    console.log('Form classId type:', typeof this.assignForm.get('classId')?.value);
    console.log('Form sectionId type:', typeof this.assignForm.get('sectionId')?.value);
    if (this.data.classes && this.data.classes.length > 0) {
      console.log('First class.id type:', typeof this.data.classes[0].id, 'value:', this.data.classes[0].id);
    }
    if (this.availableSections && this.availableSections.length > 0) {
      console.log('First section.id type:', typeof this.availableSections[0].id, 'value:', this.availableSections[0].id);
    }
    
    if (!this.isEdit) {
      // Initialize server-side search
      this.initStudentSearch();
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

  /**
   * Initialize server-side student search with debouncing
   */
  private initStudentSearch(): void {
    this.filteredStudents$ = this.studentSearchControl.valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      distinctUntilChanged(),
      map(value => typeof value === 'string' ? value : ''),
      switchMap(searchTerm => {
        if (!searchTerm || searchTerm.length < 3) {
          // Don't search if less than 3 characters
          return of([]);
        }
        this.loadingStudents = true;
        return this.studentService.searchStudents(this.data.schoolId, { search: searchTerm }).pipe(
          map(students => {
            this.loadingStudents = false;
            // Filter only active students
            return students.filter((s: Student) => s.status === 'ACTIVE');
          }),
          catchError(error => {
            console.error('Error searching students:', error);
            this.loadingStudents = false;
            return of([]);
          })
        );
      })
    );
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

  onClassChange(): void {
    const classId = this.assignForm.get('classId')?.value;
    if (classId) {
      // Clear section selection
      this.assignForm.patchValue({ sectionId: null });
      
      // Load sections for the selected class
      this.sectionService.getSectionsByClass(this.data.schoolId, classId.toString()).subscribe({
        next: (sections: SectionMasterResponse[]) => {
          this.availableSections = sections.sort((a: SectionMasterResponse, b: SectionMasterResponse) => 
            (a.sectionCode || '').localeCompare(b.sectionCode || ''));
        },
        error: (error: any) => {
          console.error('Error loading sections:', error);
          this.snackBar.open('Error loading sections', 'Close', { duration: 3000 });
          this.availableSections = [];
        }
      });
    } else {
      this.availableSections = [];
      this.assignForm.patchValue({ sectionId: null });
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
