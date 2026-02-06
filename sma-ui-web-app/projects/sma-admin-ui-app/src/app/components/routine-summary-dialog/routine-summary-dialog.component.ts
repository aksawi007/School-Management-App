import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ExportColumn } from 'sma-shared-lib';

interface RoutineSummary {
  subject: string;
  department: string;
  staffName: string;
  totalClasses: number;
  totalHours: number;
}

@Component({
  selector: 'app-routine-summary-dialog',
  templateUrl: './routine-summary-dialog.component.html',
  styleUrls: ['./routine-summary-dialog.component.scss']
})
export class RoutineSummaryDialogComponent {
  summaryData: RoutineSummary[] = [];
  displayedColumns: string[] = ['subject', 'department', 'staffName', 'totalClasses', 'totalHours'];
  
  // Export columns configuration
  exportColumns: ExportColumn[] = [
    { header: 'Subject', field: 'subject' },
    { header: 'Department', field: 'department' },
    { header: 'Staff Name', field: 'staffName' },
    { header: 'Total Classes', field: 'totalClasses' },
    { header: 'Total Hours', field: 'totalHours', transform: (val) => val?.toFixed(2) }
  ];

  constructor(
    public dialogRef: MatDialogRef<RoutineSummaryDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.calculateSummary();
  }

  calculateSummary(): void {
    const routineData = this.data.routineData || [];
    const summaryMap = new Map<string, RoutineSummary>();

    // Process each routine entry
    routineData.forEach((routine: any) => {
      if (!routine || !routine.subject) return;

      const subjectId = routine.subject.id || routine.subjectId;
      const subjectName = routine.subject.subjectName || 'Unknown Subject';
      const departmentName = routine.subject.department?.departmentName || 
                            routine.department?.departmentName || 'N/A';
      const staffName = this.getStaffName(routine.teacher);
      
      // Calculate duration for this slot
      const duration = this.calculateSlotDuration(routine.timeSlot);
      
      // Create unique key: subject + teacher combination
      const key = `${subjectId}_${routine.teacherId || 'TBD'}`;

      if (summaryMap.has(key)) {
        // Update existing entry
        const existing = summaryMap.get(key)!;
        existing.totalClasses += 1;
        existing.totalHours += duration;
      } else {
        // Create new entry
        summaryMap.set(key, {
          subject: subjectName,
          department: departmentName,
          staffName: staffName,
          totalClasses: 1,
          totalHours: duration
        });
      }
    });

    // Convert map to array and sort by total classes (descending)
    this.summaryData = Array.from(summaryMap.values())
      .sort((a, b) => b.totalClasses - a.totalClasses);
  }

  getStaffName(teacher: any): string {
    if (!teacher) return 'TBD';
    
    if (teacher.fullName) return teacher.fullName;
    if (teacher.firstName && teacher.lastName) {
      return `${teacher.firstName} ${teacher.lastName}`;
    }
    if (teacher.firstName) return teacher.firstName;
    if (teacher.lastName) return teacher.lastName;
    
    return 'TBD';
  }

  calculateSlotDuration(timeSlot: any): number {
    if (!timeSlot || !timeSlot.startTime || !timeSlot.endTime) {
      return 1; // Default 1 hour if time information not available
    }

    try {
      const start = this.parseTime(timeSlot.startTime);
      const end = this.parseTime(timeSlot.endTime);
      
      // Calculate duration in hours
      const durationMinutes = (end.hours * 60 + end.minutes) - (start.hours * 60 + start.minutes);
      return durationMinutes / 60; // Convert to hours
    } catch (error) {
      console.error('Error calculating duration:', error);
      return 1; // Default to 1 hour on error
    }
  }

  parseTime(timeString: string): { hours: number; minutes: number } {
    // Handle formats like "09:00:00", "09:00", "9:00 AM"
    const cleanTime = timeString.trim();
    
    // Remove AM/PM if present
    const isPM = cleanTime.toUpperCase().includes('PM');
    const isAM = cleanTime.toUpperCase().includes('AM');
    const timeOnly = cleanTime.replace(/AM|PM/gi, '').trim();
    
    const parts = timeOnly.split(':');
    let hours = parseInt(parts[0], 10);
    const minutes = parts.length > 1 ? parseInt(parts[1], 10) : 0;
    
    // Handle AM/PM
    if (isPM && hours < 12) hours += 12;
    if (isAM && hours === 12) hours = 0;
    
    return { hours, minutes };
  }

  getTotalClasses(): number {
    return this.summaryData.reduce((sum, item) => sum + item.totalClasses, 0);
  }

  getTotalHours(): number {
    return this.summaryData.reduce((sum, item) => sum + item.totalHours, 0);
  }

  close(): void {
    this.dialogRef.close();
  }
}
