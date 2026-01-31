import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { StudentAttendanceService, DailyClassSessionService } from 'sma-shared-lib';
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
    private adminCache: AdminCacheService
  ) {
    this.schoolId = this.adminCache.getSchoolId();
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.sessionId = +params['sessionId'];
      if (this.sessionId) {
        this.loadSession();
        this.loadExistingAttendance();
      }
    });
  }

  loadSession(): void {
    // This would ideally call a getSessionById method
    // For now, we'll simulate loading session data
    this.loading = true;
    // In real implementation: this.sessionService.getSessionById(this.sessionId!).subscribe(...)
    this.loading = false;
  }

  loadExistingAttendance(): void {
    this.loading = true;
    this.attendanceService.getSessionAttendance(this.schoolId, this.sessionId!).subscribe({
      next: (records: StudentAttendance[]) => {
        if (records.length > 0) {
          // Load existing attendance
          this.attendanceRecords = records.map(r => ({
            studentId: r.student.id!,
            studentName: r.student.studentName || 'Unknown',
            rollNumber: r.student.rollNumber || 'N/A',
            status: r.attendanceStatus,
            remarks: r.remarks || ''
          }));
        } else {
          // Load student list for this class/section (would need API endpoint)
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
    // In real implementation, this would call an API to get students by class/section
    // For now, creating sample data structure
    this.attendanceRecords = [];
  }

  toggleSelectAll(): void {
    this.selectAll = !this.selectAll;
    const status = this.selectAll ? 'PRESENT' : '';
    this.attendanceRecords.forEach(record => {
      if (!record.status || record.status === 'PRESENT' || !this.selectAll) {
        record.status = status;
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
