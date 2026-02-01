import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { DatePipe } from '@angular/common';
import { 
  DailyClassSessionService, 
  ClassMasterService, 
  SectionMasterService, 
  AcademicYearService,
  ClassRoutineMaster 
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
  sessions: any[] = [];
  loading = false;
  schoolId: number;
  
  classes: any[] = [];
  sections: any[] = [];
  academicYears: any[] = [];

  constructor(
    private fb: FormBuilder,
    private sessionService: DailyClassSessionService,
    private adminCache: AdminCacheService,
    private sectionService: SectionMasterService,
    private academicYearService: AcademicYearService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private datePipe: DatePipe
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
    this.adminCache.getClasses().subscribe((data: any) => this.classes = data);
    
    // Load academic years and auto-select current year
    this.adminCache.getAcademicYears().subscribe((data: any) => {
      this.academicYears = data;
      // Auto-select current academic year
      const currentYear = data.find((y: any) => y.currentYear === true);
      if (currentYear) {
        this.filterForm.patchValue({ academicYearId: currentYear.yearId });
      }
    });
  }

  loadSections(): void {
    const classId = this.filterForm.get('classId')?.value;
    console.log('loadSections called with classId:', classId);
    
    if (!classId) {
      console.log('No classId, clearing sections');
      this.sections = [];
      return;
    }
    
    console.log('Making API call to getSectionsByClass with schoolId:', this.schoolId, 'classId:', classId);
    this.sectionService.getSectionsByClass(this.schoolId, classId.toString()).subscribe({
      next: (data: any) => {
        console.log('Sections loaded:', data);
        this.sections = data;
        this.filterForm.patchValue({ sectionId: '' });
      },
      error: (error: any) => {
        console.error('Error loading sections:', error);
        this.snackBar.open('Failed to load sections', 'Close', { duration: 3000 });
        this.sections = [];
      }
    });
  }

  onClassChange(): void {
    console.log('onClassChange called');
    this.loadSections();
  }

  formatDateToBackend(date: any): string {
    // Convert from Date object or string to YYYY-MM-DD format
    if (!date) return '';
    try {
      let dateObj: Date;
      
      if (date instanceof Date) {
        dateObj = date;
      } else if (typeof date === 'string') {
        dateObj = new Date(date);
      } else {
        return '';
      }
      
      const formatted = this.datePipe.transform(dateObj, 'yyyy-MM-dd');
      return formatted || '';
    } catch (error) {
      console.error('Error formatting date:', error);
      return '';
    }
  }

  formatDateToDisplay(date: any): string {
    // Convert from any format to DD-MM-YYYY
    if (!date) return '';
    try {
      let dateObj: Date;
      
      if (date instanceof Date) {
        dateObj = date;
      } else if (typeof date === 'string') {
        dateObj = new Date(date);
      } else {
        return '';
      }
      
      const formatted = this.datePipe.transform(dateObj, 'dd-MM-yyyy');
      return formatted || '';
    } catch (error) {
      console.error('Error formatting date:', error);
      return '';
    }
  }

  loadSchedule(): void {
    if (this.filterForm.valid) {
      this.loading = true;
      const { classId, sectionId, academicYearId, scheduleDate } = this.filterForm.value;
      
      // Ensure date is formatted to YYYY-MM-DD
      const formattedDate = this.formatDateToBackend(scheduleDate);
      
      console.log('loadSchedule called with:', { 
        classId, 
        sectionId, 
        academicYearId, 
        originalDate: scheduleDate,
        formattedDate: formattedDate
      });
      
      this.sessionService.getCompleteSchedule(this.schoolId, academicYearId, classId, sectionId, formattedDate).subscribe({
        next: (sessions: any) => {
          console.log('Schedule response:', sessions);
          // Map the API response to the expected format, preserving all necessary data
          this.sessions = (sessions as any[]).map(session => ({
            id: session.id,
            timeSlot: session.timeSlot,
            subject: session.subject,
            teacher: session.teacher,
            remarks: session.remarks,
            sessionStatus: session.sessionStatus || 'SCHEDULED',
            subjectOverride: session.subjectOverride,
            teacherOverride: session.teacherOverride,
            actualTeacher: session.actualTeacher,
            effectiveTeacher: session.effectiveTeacher,
            effectiveSubject: session.effectiveSubject,
            routineMaster: session.routineMaster || {
              teacher: session.teacher,
              subject: session.subject
            },
            // Preserve original API response data for override dialog
            academicYear: session.academicYear,
            classMaster: session.classMaster,
            section: session.section,
            dayOfWeek: session.dayOfWeek,
            scheduleDate: formattedDate
          })).sort((a, b) => 
            (a.timeSlot?.displayOrder || 0) - (b.timeSlot?.displayOrder || 0)
          );
          console.log('Sessions transformed:', this.sessions);
          this.loading = false;
        },
        error: (error: any) => {
          console.error('Error loading schedule:', error);
          this.snackBar.open('Failed to load schedule', 'Close', { duration: 3000 });
          this.loading = false;
        }
      });
    }
  }

  overrideSession(session: any): void {
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

  getEffectiveTeacher(session: any): string {
    // Use effectiveTeacher from API response (includes overrides)
    let teacher = session.effectiveTeacher || session.teacher || session.routineMaster?.teacher;
    if (!teacher) return 'Not Assigned';
    
    return teacher.firstName && teacher.lastName ? 
           `${teacher.firstName} ${teacher.lastName}` : 
           teacher.staffName || 'Not Assigned';
  }

  getEffectiveSubject(session: any): string {
    // Use effectiveSubject from API response (includes overrides)
    const subject = session.effectiveSubject || session.subject || session.routineMaster?.subject;
    return subject?.subjectName || 'No Subject';
  }
}
