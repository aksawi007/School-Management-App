import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { RoutineTimeSlotService, RoutineTimeSlot } from 'sma-shared-lib';
import { AdminCacheService } from '../../services/admin-cache.service';
import { TimeSlotDialogComponent } from '../time-slot-dialog/time-slot-dialog.component';

@Component({
  selector: 'app-time-slot-management',
  templateUrl: './time-slot-management.component.html',
  styleUrls: ['./time-slot-management.component.scss']
})
export class TimeSlotManagementComponent implements OnInit {
  timeSlots: RoutineTimeSlot[] = [];
  loading = false;
  schoolId: number;

  constructor(
    private timeSlotService: RoutineTimeSlotService,
    private snackBar: MatSnackBar,
    private adminCache: AdminCacheService,
    private dialog: MatDialog
  ) {
    this.schoolId = this.adminCache.getSchoolId();
  }

  ngOnInit(): void {
    this.loadTimeSlots();
  }

  loadTimeSlots(): void {
    this.loading = true;
    this.timeSlotService.getActiveTimeSlots(this.schoolId).subscribe({
      next: (slots: RoutineTimeSlot[]) => {
        this.timeSlots = slots.sort((a, b) => (a.displayOrder || 0) - (b.displayOrder || 0));
        this.loading = false;
      },
      error: (error: any) => {
        this.snackBar.open('Failed to load time slots', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  openAddDialog(): void {
    const dialogRef = this.dialog.open(TimeSlotDialogComponent, {
      width: '500px',
      data: { schoolId: this.schoolId }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadTimeSlots();
      }
    });
  }

  editSlot(slot: RoutineTimeSlot): void {
    const dialogRef = this.dialog.open(TimeSlotDialogComponent, {
      width: '500px',
      data: { schoolId: this.schoolId, slot: slot }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadTimeSlots();
      }
    });
  }

  deleteSlot(id: number): void {
    if (confirm('Are you sure you want to delete this time slot?')) {
      this.loading = true;
      this.timeSlotService.deleteTimeSlot(this.schoolId, id).subscribe({
        next: () => {
          this.snackBar.open('Time slot deleted successfully', 'Close', { duration: 3000 });
          this.loadTimeSlots();
        },
        error: (error: any) => {
          this.snackBar.open(error.error?.message || 'Failed to delete time slot', 'Close', { duration: 3000 });
          this.loading = false;
        }
      });
    }
  }
}
