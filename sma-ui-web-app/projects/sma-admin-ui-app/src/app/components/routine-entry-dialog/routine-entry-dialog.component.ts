import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ClassRoutineMasterService, ClassRoutineMasterRequest, StaffSubjectMappingService } from 'sma-shared-lib';

interface TeacherResponse {
  staffId: number;
  employeeCode: string;
  firstName?: string;
  middleName?: string;
  lastName?: string;
  fullName?: string;
  email?: string;
  phone?: string;
  staffType?: string;
  designation?: string;
  roleInDepartment?: string;
  isPrimaryDepartment?: boolean;
  assignmentDate?: string;
  memberSince?: string;
  remarks?: string;
}

@Component({
  selector: 'app-routine-entry-dialog',
  templateUrl: './routine-entry-dialog.component.html',
  styleUrls: ['./routine-entry-dialog.component.scss']
})
export class RoutineEntryDialogComponent implements OnInit {
  routineForm: FormGroup;
  teachers: TeacherResponse[] = [];
  availableTeachers: any[] = [];
  showingAvailableTeachers = false;
  loading = false;
  isEdit = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private routineService: ClassRoutineMasterService,
    private staffSubjectMappingService: StaffSubjectMappingService,
    private dialogRef: MatDialogRef<RoutineEntryDialogComponent>,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.isEdit = !!data.routine;
    
    this.routineForm = this.fb.group({
      subjectId: [data.routine?.subjectId || '', Validators.required],
      teacherId: [data.routine?.teacherId || '', Validators.required],
      remarks: [data.routine?.remarks || '']
    });
  }

  ngOnInit(): void {
    // Listen to subject changes to reload qualified teachers
    // Set this up FIRST so it catches all changes including the initial patchValue
    this.routineForm.get('subjectId')?.valueChanges.subscribe(subjectId => {
      if (subjectId) {
        this.loadQualifiedTeachers(subjectId);
      } else {
        this.teachers = [];
      }
    });

    // If editing, set form values immediately
    if (this.isEdit && this.data.routine) {
      const subjectId = this.data.routine.subjectId || this.data.routine.subject?.id;
      const teacherId = this.data.routine.teacherId || this.data.routine.teacher?.id;
      
      // Set form values - this will trigger valueChanges subscription
      this.routineForm.patchValue({
        subjectId: Number(subjectId),
        teacherId: teacherId ? Number(teacherId) : '',
        remarks: this.data.routine.remarks || ''
      }, { emitEvent: true }); // Ensure valueChanges fires
    }
  }

  loadQualifiedTeachersAndSetForm(subjectId: number): void {
    this.loading = true;
    this.errorMessage = '';
    
    // Find the selected subject to get its departmentId
    // First try to find from data.subjects, if not found check if routine has it
    let selectedSubject = this.data.subjects?.find((s: any) => s.id === subjectId);
    
    // If not found in subjects array, use the subject from routine (it has department info)
    if (!selectedSubject && this.data.routine?.subject) {
      console.log('Using subject from routine object');
      selectedSubject = this.data.routine.subject;
    }
    
    const departmentId = selectedSubject?.departmentId || selectedSubject?.department?.id;
    
    console.log('Selected subject:', selectedSubject);
    console.log('Department ID:', departmentId);
    
    // If departmentId is not available, show error
    if (!departmentId) {
      this.loading = false;
      this.errorMessage = 'Subject is not linked to any department';
      this.snackBar.open(this.errorMessage, 'Close', { duration: 4000 });
      this.teachers = [];
      return;
    }
    
    this.staffSubjectMappingService.getQualifiedTeachersForSubject(
      this.data.schoolId, 
      subjectId, 
      this.data.classId,
      departmentId
    ).subscribe({
      next: (data: TeacherResponse[]) => {
        this.teachers = data;
        this.loading = false;
        
        if (this.teachers.length === 0) {
          this.snackBar.open('No qualified teachers found for this subject', 'Close', { duration: 3000 });
        }
      },
      error: (error: any) => {
        this.loading = false;
        const errorMsg = error?.error?.error || 'Failed to load qualified teachers';
        this.errorMessage = errorMsg;
        console.error('Error loading teachers:', errorMsg);
        
        // Check for specific error about department not linked
        if (errorMsg.toLowerCase().includes('not linked to any department')) {
          this.snackBar.open(errorMsg, 'Close', { duration: 4000 });
        } else {
          this.snackBar.open(errorMsg, 'Close', { duration: 3000 });
        }
        
        this.teachers = [];
      }
    });
  }

  loadQualifiedTeachers(subjectId: number): void {
    this.loading = true;
    this.errorMessage = '';
    
    // Find the selected subject to get its departmentId
    let selectedSubject = this.data.subjects?.find((s: any) => s.id === subjectId);
    
    // If not found in subjects array, try to use the routine's subject (which has full department info)
    if (!selectedSubject && this.data.routine?.subject && this.data.routine.subject.id === subjectId) {
      selectedSubject = this.data.routine.subject;
    }
    
    const departmentId = selectedSubject?.departmentId || selectedSubject?.department?.id;
    
    // If departmentId is not available, show error
    if (!departmentId) {
      this.loading = false;
      this.errorMessage = 'Subject is not linked to any department';
      this.snackBar.open(this.errorMessage, 'Close', { duration: 4000 });
      this.teachers = [];
      return;
    }
    
    this.staffSubjectMappingService.getQualifiedTeachersForSubject(
      this.data.schoolId, 
      subjectId, 
      this.data.classId,
      departmentId
    ).subscribe({
      next: (data: TeacherResponse[]) => {
        this.teachers = data;
        this.loading = false;
        
        if (this.teachers.length === 0) {
          this.snackBar.open('No qualified teachers found for this subject', 'Close', { duration: 3000 });
        }
      },
      error: (error: any) => {
        this.loading = false;
        const errorMsg = error?.error?.error || 'Failed to load qualified teachers';
        this.errorMessage = errorMsg;
        
        // Check for specific error about department not linked
        if (errorMsg.toLowerCase().includes('not linked to any department')) {
          this.snackBar.open(errorMsg, 'Close', { duration: 4000 });
        } else {
          this.snackBar.open(errorMsg, 'Close', { duration: 3000 });
        }
        
        this.teachers = [];
      }
    });
  }

  loadAvailableTeachers(): void {
    this.loading = true;
    this.routineService.getAvailableTeachersByType(
      this.data.schoolId,
      this.data.timeSlotId,
      this.data.academicYearId,
      'ACADEMIC',
      this.data.dayOfWeek
    ).subscribe({
      next: (data: any[]) => {
        this.availableTeachers = data;
        this.loading = false;
        
        if (this.availableTeachers.length === 0) {
          this.snackBar.open('No available academic staff for this time slot', 'Close', { duration: 3000 });
        }
      },
      error: (error: any) => {
        this.loading = false;
        this.snackBar.open('Failed to load available teachers', 'Close', { duration: 3000 });
        this.availableTeachers = [];
      }
    });
  }

  toggleAvailableTeachers(): void {
    this.showingAvailableTeachers = !this.showingAvailableTeachers;
    
    if (this.showingAvailableTeachers && this.availableTeachers.length === 0) {
      this.loadAvailableTeachers();
    }
  }

  onSubmit(): void {
    if (this.routineForm.invalid) {
      return;
    }

    this.loading = true;
    const formValue = this.routineForm.value;

    // Check teacher availability before saving (day-specific)
    this.routineService.checkTeacherAvailability(
      this.data.schoolId,
      formValue.teacherId,
      this.data.timeSlotId,
      this.data.academicYearId,
      this.data.classId,
      this.data.sectionId,
      this.data.dayOfWeek  // Pass day of week for day-specific availability check
    ).subscribe({
      next: (result: { available: boolean; conflictingRoutines: any[] }) => {
        if (!result.available) {
          this.loading = false;
          const conflictClass = result.conflictingRoutines?.[0]?.class?.className || 'another class';
          this.errorMessage = `This teacher is already allocated to ${conflictClass} in the same time slot on ${this.data.dayOfWeek}`;
          this.snackBar.open(this.errorMessage, 'Close', { duration: 5000 });
          return;
        }

        // Teacher is available, proceed with save
        this.saveRoutine(formValue);
      },
      error: (error: any) => {
        this.loading = false;
        this.snackBar.open('Failed to check teacher availability', 'Close', { duration: 3000 });
      }
    });
  }

  private saveRoutine(formValue: any): void {
    const request: ClassRoutineMasterRequest = {
      schoolId: this.data.schoolId,
      academicYearId: this.data.academicYearId,
      classId: this.data.classId,
      sectionId: this.data.sectionId,
      dayOfWeek: this.data.dayOfWeek,
      timeSlotId: this.data.timeSlotId,
      subjectId: formValue.subjectId,
      teacherId: formValue.teacherId,
      remarks: formValue.remarks
    };

    this.routineService.createOrUpdateRoutine(this.data.schoolId, request).subscribe({
      next: (response) => {
        this.snackBar.open(
          this.isEdit ? 'Routine updated successfully' : 'Routine entry created successfully',
          'Close',
          { duration: 3000 }
        );
        this.dialogRef.close(true);
      },
      error: (error) => {
        this.snackBar.open('Failed to save routine entry', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }

  compareIds(id1: any, id2: any): boolean {
    // Compare IDs as numbers to handle type mismatches
    return Number(id1) === Number(id2);
  }
}
