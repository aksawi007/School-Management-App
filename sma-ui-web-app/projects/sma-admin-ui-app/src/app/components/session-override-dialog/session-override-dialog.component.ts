import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { 
  DailyClassSessionService, 
  SubjectMasterService, 
  StaffService,
  DailyClassSession 
} from 'sma-shared-lib';

@Component({
  selector: 'app-session-override-dialog',
  templateUrl: './session-override-dialog.component.html',
  styleUrls: ['./session-override-dialog.component.scss']
})
export class SessionOverrideDialogComponent implements OnInit {
  overrideForm: FormGroup;
  subjects: any[] = [];
  teachers: any[] = [];
  loading = false;
  schoolId = 1; // TODO: Get from context/session

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<SessionOverrideDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { session: DailyClassSession },
    private sessionService: DailyClassSessionService,
    private subjectService: SubjectMasterService,
    private staffService: StaffService,
    private snackBar: MatSnackBar
  ) {
    this.overrideForm = this.fb.group({
      overriddenSubjectId: [''],
      substituteTeacherId: [''],
      remarks: ['']
    });
  }

  ngOnInit(): void {
    this.loadMasterData();
    this.initializeForm();
  }

  loadMasterData(): void {
    this.subjectService.getAllSubjectsBySchool(this.schoolId).subscribe(data => this.subjects = data);
    this.staffService.getAllStaffBySchool(this.schoolId).subscribe(data => this.teachers = data);
  }

  initializeForm(): void {
    const session = this.data.session;
    this.overrideForm.patchValue({
      overriddenSubjectId: session.subjectOverride || '',
      substituteTeacherId: session.teacherOverride || '',
      remarks: session.remarks || ''
    });
  }

  onSubmit(): void {
    if (this.overrideForm.valid) {
      this.loading = true;
      const session = this.data.session;
      const formValue = this.overrideForm.value;
      
      const request = {
        schoolId: this.schoolId,
        academicYearId: session.academicYearId,
        classId: session.classId,
        sectionId: session.sectionId,
        sessionDate: session.sessionDate,
        timeSlotId: session.timeSlotId,
        routineMasterId: session.routineMasterId,
        subjectOverride: formValue.overriddenSubjectId || null,
        teacherOverride: formValue.substituteTeacherId || null,
        actualTeacherId: formValue.substituteTeacherId || null,
        sessionStatus: session.sessionStatus || 'SCHEDULED',
        remarks: formValue.remarks || null
      };

      this.sessionService.createOrUpdateSession(this.schoolId, request).subscribe({
        next: () => {
          this.snackBar.open('Session overridden successfully', 'Close', { duration: 3000 });
          this.dialogRef.close(true);
        },
        error: (error: any) => {
          this.snackBar.open(error.error?.message || 'Failed to override session', 'Close', { duration: 3000 });
          this.loading = false;
        }
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
