import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { RoutineTimeSlotService, RoutineTimeSlot, RoutineTimeSlotRequest } from 'sma-shared-lib';
import { AdminCacheService } from '../../services/admin-cache.service';

@Component({
  selector: 'app-time-slot-management',
  templateUrl: './time-slot-management.component.html',
  styleUrls: ['./time-slot-management.component.scss']
})
export class TimeSlotManagementComponent implements OnInit {
  timeSlots: RoutineTimeSlot[] = [];
  timeSlotForm: FormGroup;
  loading = false;
  editMode = false;
  editingSlotId?: number;
  schoolId: number;

  slotTypes = ['TEACHING', 'BREAK', 'LUNCH', 'ASSEMBLY'];

  constructor(
    private fb: FormBuilder,
    private timeSlotService: RoutineTimeSlotService,
    private snackBar: MatSnackBar,
    private adminCache: AdminCacheService
  ) {
    this.schoolId = this.adminCache.getSchoolId();
    this.timeSlotForm = this.fb.group({
      slotName: ['', Validators.required],
      startTime: ['', Validators.required],
      endTime: ['', Validators.required],
      displayOrder: [0, [Validators.required, Validators.min(0)]],
      slotType: ['TEACHING', Validators.required]
    });
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

  onSubmit(): void {
    if (this.timeSlotForm.valid) {
      this.loading = true;
      const formValue = this.timeSlotForm.value;
      
      const request: RoutineTimeSlotRequest = {
        schoolId: this.schoolId,
        slotName: formValue.slotName,
        startTime: formValue.startTime,
        endTime: formValue.endTime,
        displayOrder: formValue.displayOrder,
        slotType: formValue.slotType
      };

      const operation = this.editMode
        ? this.timeSlotService.updateTimeSlot(this.schoolId, this.editingSlotId!, request)
        : this.timeSlotService.createTimeSlot(this.schoolId, request);

      operation.subscribe({
        next: () => {
          this.snackBar.open(`Time slot ${this.editMode ? 'updated' : 'created'} successfully`, 'Close', { duration: 3000 });
          this.loadTimeSlots();
          this.resetForm();
          this.loading = false;
        },
        error: (error: any) => {
          this.snackBar.open(error.error?.message || 'Operation failed', 'Close', { duration: 3000 });
          this.loading = false;
        }
      });
    }
  }

  editSlot(slot: RoutineTimeSlot): void {
    this.editMode = true;
    this.editingSlotId = slot.id;
    this.timeSlotForm.patchValue({
      slotName: slot.slotName,
      startTime: slot.startTime,
      endTime: slot.endTime,
      displayOrder: slot.displayOrder,
      slotType: slot.slotType
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

  resetForm(): void {
    this.timeSlotForm.reset({ displayOrder: 0, slotType: 'TEACHING' });
    this.editMode = false;
    this.editingSlotId = undefined;
  }
}
