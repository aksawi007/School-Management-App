import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { 
  DailyClassSessionService, 
  ClassMasterService, 
  SectionMasterService, 
  AcademicYearService,
  DailyClassSession 
} from 'sma-shared-lib';
import { SessionOverrideDialogComponent } from '../session-override-dialog/session-override-dialog.component';
import { AdminCacheService } from '../../services/admin-cache.service';

@Component({
  selector: 'app-daily-schedule-view',
  templateUrl: './daily-schedule-view.component.html',
  styleUrls: ['./daily-schedule-view.component.scss']
})
export class DailyScheduleViewComponent implements OnInit {
  filterForm: FormGroup;
  sessions: DailyClassSession[] = [];
  loading = false;
  schoolId: number;
  
  classes: any[] = [];
  sections: any[] = [];
  academicYears: any[] = [];

  constructor(
    private fb: FormBuilder,
    private sessionService: DailyClassSessionService,
    private classService: ClassMasterService,
    private sectionService: SectionMasterService,
    private academicYearService: AcademicYearService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private adminCache: AdminCacheService
  ) {
    this.schoolId = this.adminCache.getSchoolId();
    this.filterForm = this.fb.group({
      classId: ['', Validators.required],
      sectionId: ['', Validators.required],
      academicYearId: ['', Validators.required],
      scheduleDate: [new Date().toISOString().split('T')[0], Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadMasterData();
  }

  loadMasterData(): void {
    this.classService.getAllClassesBySchool(this.schoolId).subscribe((data: any) => this.classes = data);
    // TODO: API needs getAllSectionsBySchool method
    this.sections = []; // Temporarily empty until API is added
    this.academicYearService.getAllAcademicYears().subscribe((data: any) => this.academicYears = data);
  }

  loadSchedule(): void {
    if (this.filterForm.valid) {
      this.loading = true;
      const { classId, sectionId, academicYearId, scheduleDate } = this.filterForm.value;
      
      this.sessionService.getCompleteSchedule(this.schoolId, academicYearId, classId, sectionId, scheduleDate).subscribe({
        next: (sessions: any) => {
          this.sessions = (sessions as DailyClassSession[]).sort((a, b) => 
            (a.timeSlot?.displayOrder || 0) - (b.timeSlot?.displayOrder || 0)
          );
          this.loading = false;
        },
        error: (error: any) => {
          this.snackBar.open('Failed to load schedule', 'Close', { duration: 3000 });
          this.loading = false;
        }
      });
    }
  }

  overrideSession(session: DailyClassSession): void {
    const dialogRef = this.dialog.open(SessionOverrideDialogComponent, {
      width: '600px',
      data: { session }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadSchedule();
      }
    });
  }

  updateSessionStatus(sessionId: number, status: string): void {
    this.sessionService.updateSessionStatus(this.schoolId, sessionId, status).subscribe({
      next: () => {
        this.snackBar.open('Session status updated', 'Close', { duration: 3000 });
        this.loadSchedule();
      },
      error: (error) => {
        this.snackBar.open('Failed to update status', 'Close', { duration: 3000 });
      }
    });
  }

  getStatusClass(status: string): string {
    return `status-${status.toLowerCase()}`;
  }

  getEffectiveTeacher(session: DailyClassSession): string {
    // For now, return routineMaster teacher or 'Not Assigned'
    return session.routineMaster?.teacher?.staffName || 'Not Assigned';
  }

  getEffectiveSubject(session: DailyClassSession): string {
    return session.routineMaster?.subject?.subjectName || 'No Subject';
  }
}
