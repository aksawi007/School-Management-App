import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ClassRoutineMasterService, ClassRoutineMasterRequest, StaffSubjectMappingService, StaffSubjectMappingResponse } from 'sma-shared-lib';

@Component({
  selector: 'app-routine-entry-dialog',
  templateUrl: './routine-entry-dialog.component.html',
  styleUrls: ['./routine-entry-dialog.component.scss']
})
export class RoutineEntryDialogComponent implements OnInit {
  routineForm: FormGroup;
  teachers: StaffSubjectMappingResponse[] = [];
  loading = false;
  isEdit = false;

  constructor(
    private fb: FormBuilder,
    private routineService: ClassRoutineMasterService,
    private staffSubjectMappingService: StaffSubjectMappingService,
    private dialogRef: MatDialogRef<RoutineEntryDialogComponent>,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.isEdit = !!data.routine;
    
    this.routineForm = this.fb.group({
      subjectId: [data.routine?.subjectId || '', Validators.required],
      teacherId: [data.routine?.teacherId || '', Validators.required],
      remarks: [data.routine?.remarks || '']
    });
  }

  ngOnInit(): void {
    // Load qualified teachers when subject is selected
    if (this.data.routine?.subjectId) {
      this.loadQualifiedTeachers(this.data.routine.subjectId);
    }
    
    // Listen to subject changes to reload qualified teachers
    this.routineForm.get('subjectId')?.valueChanges.subscribe(subjectId => {
      if (subjectId) {
        this.loadQualifiedTeachers(subjectId);
      } else {
        this.teachers = [];
      }
    });
  }

  loadQualifiedTeachers(subjectId: number): void {
    this.loading = true;
    this.staffSubjectMappingService.getQualifiedTeachersForSubject(
      this.data.schoolId, 
      subjectId, 
      this.data.classId
    ).subscribe({
      next: (data: StaffSubjectMappingResponse[]) => {
        this.teachers = data;
        this.loading = false;
        
        if (this.teachers.length === 0) {
          this.snackBar.open('No qualified teachers found for this subject', 'Close', { duration: 3000 });
        }
      },
      error: (error: any) => {
        this.snackBar.open('Failed to load qualified teachers', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.routineForm.invalid) {
      return;
    }

    this.loading = true;
    const formValue = this.routineForm.value;

    const request: ClassRoutineMasterRequest = {
      schoolId: this.data.schoolId,
      academicYearId: this.data.academicYearId,
      classId: this.data.classId,
      sectionId: this.data.sectionId,
      dayOfWeek: this.data.dayOfWeek,
      timeSlotId: this.data.timeSlotId,
      subjectId: formValue.subjectId,
      teacherId: formValue.teacherId,
      remarks: formValue.remarks
    };

    this.routineService.createOrUpdateRoutine(this.data.schoolId, request).subscribe({
      next: (response) => {
        this.snackBar.open(
          this.isEdit ? 'Routine updated successfully' : 'Routine entry created successfully',
          'Close',
          { duration: 3000 }
        );
        this.dialogRef.close(true);
      },
      error: (error) => {
        this.snackBar.open('Failed to save routine entry', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
