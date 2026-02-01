import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { RoutineTimeSlotService, RoutineTimeSlot, RoutineTimeSlotRequest } from 'sma-shared-lib';

@Component({
  selector: 'app-time-slot-dialog',
  templateUrl: './time-slot-dialog.component.html',
  styleUrls: ['./time-slot-dialog.component.scss']
})
export class TimeSlotDialogComponent implements OnInit {
  timeSlotForm: FormGroup;
  loading = false;
  slotTypes = ['TEACHING', 'BREAK', 'LUNCH', 'ASSEMBLY'];

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<TimeSlotDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { schoolId: number; slot?: RoutineTimeSlot },
    private timeSlotService: RoutineTimeSlotService,
    private snackBar: MatSnackBar
  ) {
    this.timeSlotForm = this.fb.group({
      slotName: ['', Validators.required],
      startTime: ['', Validators.required],
      endTime: ['', Validators.required],
      displayOrder: [0, [Validators.required, Validators.min(0)]],
      slotType: ['TEACHING', Validators.required]
    });
  }

  ngOnInit(): void {
    if (this.data.slot) {
      // Edit mode - populate form with existing slot data
      this.timeSlotForm.patchValue({
        slotName: this.data.slot.slotName,
        startTime: this.data.slot.startTime,
        endTime: this.data.slot.endTime,
        displayOrder: this.data.slot.displayOrder,
        slotType: this.data.slot.slotType
      });
    }
  }

  onSubmit(): void {
    if (this.timeSlotForm.valid) {
      this.loading = true;
      const formValue = this.timeSlotForm.value;

      const request: RoutineTimeSlotRequest = {
        schoolId: this.data.schoolId,
        slotName: formValue.slotName,
        startTime: formValue.startTime,
        endTime: formValue.endTime,
        displayOrder: formValue.displayOrder,
        slotType: formValue.slotType
      };

      const operation = this.data.slot
        ? this.timeSlotService.updateTimeSlot(this.data.schoolId, this.data.slot.id!, request)
        : this.timeSlotService.createTimeSlot(this.data.schoolId, request);

      operation.subscribe({
        next: () => {
          this.snackBar.open(`Time slot ${this.data.slot ? 'updated' : 'created'} successfully`, 'Close', { duration: 3000 });
          this.dialogRef.close(true);
        },
        error: (error: any) => {
          this.snackBar.open(error.error?.message || 'Operation failed', 'Close', { duration: 3000 });
          this.loading = false;
        }
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
