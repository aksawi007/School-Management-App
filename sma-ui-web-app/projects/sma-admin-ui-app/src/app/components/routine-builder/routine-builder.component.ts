import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { 
  ClassRoutineMasterService, 
  ClassRoutineMaster, 
  RoutineTimeSlotService, 
  RoutineTimeSlot,
  ClassMasterService,
  ClassMasterResponse,
  SectionMasterService,
  SectionMasterResponse,
  SubjectMasterService,
  SubjectMasterResponse,
  AcademicYearService
} from 'sma-shared-lib';
import { RoutineEntryDialogComponent } from '../routine-entry-dialog/routine-entry-dialog.component';
import { AdminCacheService } from '../../services/admin-cache.service';

interface RoutineCell {
  dayOfWeek: string;
  timeSlot: RoutineTimeSlot;
  routine?: ClassRoutineMaster;
}

@Component({
  selector: 'app-routine-builder',
  templateUrl: './routine-builder.component.html',
  styleUrls: ['./routine-builder.component.scss']
})
export class RoutineBuilderComponent implements OnInit {
  schoolId: number;
  selectedAcademicYearId?: number;
  selectedClassId?: number;
  selectedSectionId?: number;
  
  classes: ClassMasterResponse[] = [];
  sections: SectionMasterResponse[] = [];
  timeSlots: RoutineTimeSlot[] = [];
  subjects: SubjectMasterResponse[] = [];
  routineData: ClassRoutineMaster[] = [];
  
  daysOfWeek = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
  routineGrid: RoutineCell[][] = [];
  
  loading = false;
  academicYears: any[] = [];

  constructor(
    private routineService: ClassRoutineMasterService,
    private timeSlotService: RoutineTimeSlotService,
    private classService: ClassMasterService,
    private sectionService: SectionMasterService,
    private subjectService: SubjectMasterService,
    private academicYearService: AcademicYearService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private adminCache: AdminCacheService
  ) {
    this.schoolId = this.adminCache.getSchoolId();
  }

  ngOnInit(): void {
    // Listen for school context from parent window (shell app)
    window.addEventListener('message', (event) => {
      // Verify origin for security
      if (event.origin !== 'http://localhost:4300') {
        return;
      }
      
      if (event.data && event.data.type === 'SCHOOL_CONTEXT') {
        console.log('Received school context:', event.data);
        const school = event.data.school;
        
        if (school) {
          this.schoolId = school.schoolId;
          this.loadAcademicYears();
          this.loadClasses();
          this.loadTimeSlots();
        }
      }
    });
    
    // Request context from parent
    if (window.parent && window.parent !== window) {
      window.parent.postMessage({ type: 'REQUEST_CONTEXT' }, 'http://localhost:4300');
    }
  }

  loadAcademicYears(): void {
    this.adminCache.getAcademicYears().subscribe({
      next: (years) => {
        this.academicYears = years;
        console.log('Academic years loaded:', years);
        // Auto-select current academic year
        const currentYear = years.find(y => y.currentYear === true);
        if (currentYear && !this.selectedAcademicYearId) {
          this.selectedAcademicYearId = currentYear.yearId;
          console.log('Auto-selected current academic year:', currentYear.yearName);
        }
      },
      error: (error) => {
        console.error('Error loading academic years:', error);
        this.snackBar.open('Error loading academic years', 'Close', { duration: 3000 });
      }
    });
  }

  loadClasses(): void {
    this.classService.getAllClassesBySchool(this.schoolId).subscribe({
      next: (data: ClassMasterResponse[]) => this.classes = data,
      error: (error: any) => this.showError('Failed to load classes')
    });
  }

  loadSections(): void {
    if (!this.selectedClassId) return;
    
    this.sectionService.getSectionsByClass(this.schoolId, this.selectedClassId!.toString()).subscribe({
      next: (data) => {
        this.sections = data;
        this.selectedSectionId = undefined;
        this.routineData = [];
        this.buildGrid();
      },
      error: (error) => this.showError('Failed to load sections')
    });
  }

  loadTimeSlots(): void {
    this.timeSlotService.getActiveTimeSlots(this.schoolId).subscribe({
      next: (data) => {
        this.timeSlots = data.filter(slot => slot.slotType === 'TEACHING')
          .sort((a, b) => a.displayOrder - b.displayOrder);
        this.buildGrid();
      },
      error: (error) => this.showError('Failed to load time slots')
    });
  }

  loadSubjects(): void {
    if (!this.selectedClassId) return;
    
    this.subjectService.getSubjectsByClass(this.schoolId, this.selectedClassId!.toString()).subscribe({
      next: (data) => this.subjects = data,
      error: (error) => this.showError('Failed to load subjects')
    });
  }

  loadRoutine(): void {
    if (!this.selectedAcademicYearId || !this.selectedClassId || !this.selectedSectionId) {
      this.snackBar.open('Please select Academic Year, Class, and Section', 'Close', { duration: 3000 });
      return;
    }

    this.loading = true;
    this.routineService.getWeeklyRoutine(
      this.schoolId, 
      this.selectedAcademicYearId, 
      this.selectedClassId, 
      this.selectedSectionId
    ).subscribe({
      next: (data) => {
        this.routineData = data;
        this.buildGrid();
        this.loading = false;
      },
      error: (error) => {
        this.showError('Failed to load routine');
        this.loading = false;
      }
    });
  }

  buildGrid(): void {
    this.routineGrid = [];
    
    for (const day of this.daysOfWeek) {
      const dayRow: RoutineCell[] = [];
      
      for (const timeSlot of this.timeSlots) {
        const routine = this.routineData.find(r => 
          r.dayOfWeek === day && r.timeSlotId === timeSlot.id
        );
        
        dayRow.push({
          dayOfWeek: day,
          timeSlot: timeSlot,
          routine: routine
        });
      }
      
      this.routineGrid.push(dayRow);
    }
  }

  openRoutineDialog(cell: RoutineCell): void {
    if (!this.selectedAcademicYearId || !this.selectedClassId || !this.selectedSectionId) {
      this.snackBar.open('Please select Academic Year, Class, and Section first', 'Close', { duration: 3000 });
      return;
    }

    const dialogRef = this.dialog.open(RoutineEntryDialogComponent, {
      width: '500px',
      data: {
        schoolId: this.schoolId,
        academicYearId: this.selectedAcademicYearId,
        classId: this.selectedClassId,
        sectionId: this.selectedSectionId,
        dayOfWeek: cell.dayOfWeek,
        timeSlotId: cell.timeSlot.id!,
        timeSlotName: cell.timeSlot.slotName,
        routine: cell.routine,
        subjects: this.subjects
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadRoutine();
      }
    });
  }

  deleteRoutine(cell: RoutineCell): void {
    if (!cell.routine?.id) return;

    if (confirm('Are you sure you want to delete this routine entry?')) {
      this.routineService.deleteRoutineEntry(this.schoolId, cell.routine.id).subscribe({
        next: () => {
          this.showSuccess('Routine entry deleted successfully');
          this.loadRoutine();
        },
        error: (error) => this.showError('Failed to delete routine entry')
      });
    }
  }

  onClassChange(): void {
    this.loadSections();
    this.loadSubjects();
  }

  onSectionChange(): void {
    this.loadRoutine();
  }

  showSuccess(message: string): void {
    this.snackBar.open(message, 'Close', { duration: 3000 });
  }

  showError(message: string): void {
    this.snackBar.open(message, 'Close', { duration: 5000 });
  }
}
