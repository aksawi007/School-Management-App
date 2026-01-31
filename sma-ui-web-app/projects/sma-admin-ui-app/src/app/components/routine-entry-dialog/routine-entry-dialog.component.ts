import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ClassRoutineMasterService, ClassRoutineMasterRequest, StaffService, StaffResponse } from 'sma-shared-lib';

@Component({
  selector: 'app-routine-entry-dialog',
  templateUrl: './routine-entry-dialog.component.html',
  styleUrls: ['./routine-entry-dialog.component.scss']
})
export class RoutineEntryDialogComponent implements OnInit {
  routineForm: FormGroup;
  teachers: StaffResponse[] = [];
  loading = false;
  isEdit = false;

  constructor(
    private fb: FormBuilder,
    private routineService: ClassRoutineMasterService,
    private staffService: StaffService,
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
    this.loadTeachers();
  }

  loadTeachers(): void {
    this.staffService.getAllStaffBySchool(this.data.schoolId).subscribe({
      next: (data: StaffResponse[]) => {
        this.teachers = data.filter((s: StaffResponse) => s.staffType === 'TEACHING');
      },
      error: (error: any) => {
        this.snackBar.open('Failed to load teachers', 'Close', { duration: 3000 });
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
