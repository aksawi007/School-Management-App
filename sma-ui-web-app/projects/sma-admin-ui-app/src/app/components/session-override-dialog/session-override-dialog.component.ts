import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { 
  DailyClassSessionService, 
  SubjectMasterService, 
  StaffService
} from 'sma-shared-lib';
import { AdminCacheService } from '../../services/admin-cache.service';

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
  schoolId: number;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<SessionOverrideDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { session: any },
    private sessionService: DailyClassSessionService,
    private subjectService: SubjectMasterService,
    private staffService: StaffService,
    private snackBar: MatSnackBar,
    private adminCache: AdminCacheService
  ) {
    this.schoolId = this.adminCache.getSchoolId();
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
    this.subjectService.getAllSubjectsBySchool(this.schoolId).subscribe({
      next: (data: any) => {
        this.subjects = data;
        console.log('Subjects loaded:', data);
      },
      error: (error: any) => {
        console.error('Error loading subjects:', error);
      }
    });
    
    this.staffService.getAllStaffBySchool(this.schoolId).subscribe({
      next: (data: any) => {
        console.log('All staff loaded:', data);
        // Filter for academic staff only (teachers)
        this.teachers = data.filter((staff: any) => staff.staffType === 'ACADEMIC' || !staff.staffType);
        console.log('Teachers filtered:', this.teachers);
      },
      error: (error: any) => {
        console.error('Error loading teachers:', error);
      }
    });
  }

  initializeForm(): void {
    const session = this.data.session;
    // Initialize form with any existing overrides
    this.overrideForm.patchValue({
      overriddenSubjectId: session.subjectOverride || '',
      substituteTeacherId: session.teacherOverride || '',
      remarks: session.remarks || ''
    });
  }

  onSubmit(): void {
    this.loading = true;
    const session = this.data.session;
    const formValue = this.overrideForm.value;
    
    // Build the override request with actual session data from API response
    const academicYearId = session.academicYear?.id || session.academicYearId;
    const classId = session.classMaster?.id || session.classId;
    const sectionId = session.section?.id || session.sectionId;
    const timeSlotId = session.timeSlot?.id || session.timeSlotId;
    const subjectId = formValue.overriddenSubjectId || session.subject?.id;
    const teacherId = formValue.substituteTeacherId || session.teacher?.id;
    
    console.log('Session object:', session);
    console.log('Extracted values:', { academicYearId, classId, sectionId, timeSlotId });
    
    const request = {
      schoolId: this.schoolId,
      academicYearId: academicYearId,
      classId: classId,
      sectionId: sectionId,
      sessionDate: session.scheduleDate || new Date().toISOString().split('T')[0],
      dayOfWeek: session.dayOfWeek,
      timeSlotId: timeSlotId,
      subjectId: subjectId,
      teacherId: teacherId,
      remarks: formValue.remarks || session.remarks || null,
      isActive: true,
      routineMasterId: session.id,
      sessionStatus: 'SCHEDULED'
    };

    console.log('Submitting override request:', request);

    this.sessionService.createOrUpdateSession(this.schoolId, request).subscribe({
      next: () => {
        this.snackBar.open('Session overridden successfully', 'Close', { duration: 3000 });
        this.dialogRef.close(true);
      },
      error: (error: any) => {
        console.error('Error overriding session:', error);
        this.snackBar.open(error.error?.message || 'Failed to override session', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
