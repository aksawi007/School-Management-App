import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { StudentAttendanceService, DailyClassSessionService, AttendanceService, StudentClassSectionService } from 'sma-shared-lib';
import { StudentAttendance, DailyClassSession } from 'sma-shared-lib';
import { AdminCacheService } from '../../services/admin-cache.service';

interface AttendanceRecord {
  studentId: number;
  studentName: string;
  rollNumber: string;
  status: string;
  remarks: string;
}

@Component({
  selector: 'app-attendance-marking',
  templateUrl: './attendance-marking.component.html',
  styleUrls: ['./attendance-marking.component.scss']
})
export class AttendanceMarkingComponent implements OnInit {
  sessionId?: number;
  session?: DailyClassSession;
  attendanceRecords: AttendanceRecord[] = [];
  loading = false;
  saving = false;
  schoolId: number;
  academicYearId?: number;
  classId?: number;
  sectionId?: number;

  attendanceStatuses = ['PRESENT', 'ABSENT', 'LATE', 'EXCUSED', 'SICK_LEAVE'];

  // Quick selection
  selectAll = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private attendanceService: StudentAttendanceService,
    private sessionService: DailyClassSessionService,
    private snackBar: MatSnackBar,
    private adminCache: AdminCacheService,
    private deviceAttendanceService: AttendanceService,
    private studentClassSectionService: StudentClassSectionService
  ) {
    this.schoolId = this.adminCache.getSchoolId();
  }

  // Minimal: send a sample device webhook to backend (placeholder integration)
  sendTestDeviceWebhook(): void {
    const sample = {
      deviceId: 'sim-device-01',
      deviceTxnId: `sim-${Date.now()}`,
      timestamp: new Date().toISOString(),
      items: [] as any[]
    };

    // include up to one student and one staff item for quick testing
    if (this.attendanceRecords.length > 0) {
      sample.items.push({ targetType: 'STUDENT', targetId: this.attendanceRecords[0].studentId, sessionId: this.sessionId, attendanceStatus: this.attendanceRecords[0].status || 'PRESENT' });
    }

    // send via shared attendance service (injected)
    this.deviceAttendanceService.postDeviceWebhook(this.schoolId, sample).subscribe({
      next: (res: any) => {
        this.snackBar.open('Device webhook sent (placeholder)', 'Close', { duration: 3000 });
      },
      error: (err: any) => {
        this.snackBar.open('Failed to send device webhook', 'Close', { duration: 4000 });
      }
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.sessionId = +params['sessionId'];
    });
    
    this.route.queryParams.subscribe(params => {
      console.log('Query params received:', params);
      this.academicYearId = params['academicYearId'] ? +params['academicYearId'] : undefined;
      this.classId = params['classId'] ? +params['classId'] : undefined;
      this.sectionId = params['sectionId'] ? +params['sectionId'] : undefined;
      
      console.log('Parsed IDs:', { academicYearId: this.academicYearId, classId: this.classId, sectionId: this.sectionId });
      
      if (this.sessionId && this.academicYearId && this.classId && this.sectionId) {
        this.loadExistingAttendance();
      }
    });
  }

  loadSession(): void {
    this.loading = true;
    this.sessionService.getSessionById(this.schoolId, this.sessionId!).subscribe({
      next: (session: DailyClassSession) => {
        console.log('Session loaded:', session);
        this.session = session;
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Failed to load session:', error);
        this.snackBar.open('Failed to load session details', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  loadExistingAttendance(): void {
    this.loading = true;
    this.attendanceService.getSessionAttendance(this.schoolId, this.sessionId!).subscribe({
      next: (records: StudentAttendance[]) => {
        console.log('Attendance records:', records);
        if (records.length > 0) {
          // Extract session info from first record
          if (records[0].classSession) {
            this.session = records[0].classSession as any;
            console.log('Session extracted from attendance:', this.session);
          }
          // Load existing attendance
          this.attendanceRecords = records.map(r => ({
            studentId: r.student.id!,
            studentName: r.student.studentName || 'Unknown',
            rollNumber: r.student.rollNumber || 'N/A',
            status: r.attendanceStatus,
            remarks: r.remarks || ''
          }));
        } else {
          // No attendance yet - load student list directly
          console.log('No attendance found, loading student list...');
          this.loadStudentList();
        }
        this.loading = false;
      },
      error: (error: any) => {
        this.snackBar.open('Failed to load attendance data', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  loadStudentList(): void {
    console.log('Loading students for:', { academicYearId: this.academicYearId, classId: this.classId, sectionId: this.sectionId });

    if (!this.academicYearId || !this.classId || !this.sectionId) {
      this.snackBar.open('Class/section information missing', 'Close', { duration: 3000 });
      return;
    }

    const academicYearId = this.academicYearId;
    const classId = this.classId;
    const sectionId = this.sectionId;

    this.studentClassSectionService.getStudentsByClassAndSection(academicYearId, classId, sectionId).subscribe({
      next: (mappings: any[]) => {
        console.log('Received student mappings:', mappings);
        this.attendanceRecords = mappings.map((mapping: any) => ({
          studentId: mapping.studentId,
          studentName: mapping.studentName,
          rollNumber: mapping.rollNumber || 'N/A',
          status: '',
          remarks: ''
        }));
        console.log('Attendance records created:', this.attendanceRecords);
        if (this.attendanceRecords.length === 0) {
          this.snackBar.open('No students found in this class/section', 'Close', { duration: 3000 });
        }
      },
      error: (error: any) => {
        console.error('Error loading students:', error);
        this.snackBar.open('Failed to load student list: ' + (error.error?.message || error.message), 'Close', { duration: 5000 });
      }
    });
  }

  markAllAs(status: string): void {
    this.attendanceRecords.forEach(record => record.status = status);
  }

  onSubmit(): void {
    // Validate all students have status
    const incomplete = this.attendanceRecords.filter(r => !r.status);
    if (incomplete.length > 0) {
      this.snackBar.open(`Please mark attendance for all students (${incomplete.length} pending)`, 'Close', { duration: 3000 });
      return;
    }

    this.saving = true;
    const bulkRequest = {
      sessionId: this.sessionId!,
      markedBy: 1, // TODO: Get from logged in user context
      attendanceList: this.attendanceRecords.map(record => ({
        studentId: record.studentId,
        attendanceStatus: record.status,
        remarks: record.remarks
      }))
    };

    this.attendanceService.markBulkAttendance(this.schoolId, bulkRequest).subscribe({
      next: () => {
        this.snackBar.open('Attendance marked successfully', 'Close', { duration: 3000 });
        this.router.navigate(['/daily-schedule-view']);
      },
      error: (error: any) => {
        this.snackBar.open(error.error?.message || 'Failed to mark attendance', 'Close', { duration: 3000 });
        this.saving = false;
      }
    });
  }

  getStatusClass(status: string): string {
    return `status-${status?.toLowerCase() || 'default'}`;
  }

  goBack(): void {
    this.router.navigate(['/daily-schedule-view']);
  }
}
