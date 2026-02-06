import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { 
  ClassRoutineMasterService, 
  ClassRoutineMaster, 
  RoutineTimeSlotService, 
  RoutineTimeSlot,
  ClassMasterResponse,
  ClassMasterService,
  SectionMasterService,
  SectionMasterResponse,
  SubjectMasterService,
  SubjectMasterResponse,
  AcademicYearService
} from 'sma-shared-lib';
import { RoutineEntryDialogComponent } from '../routine-entry-dialog/routine-entry-dialog.component';
import { RoutineSummaryDialogComponent } from '../routine-summary-dialog/routine-summary-dialog.component';
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
    private adminCache: AdminCacheService,
    private classMasterService: ClassMasterService,
    private sectionService: SectionMasterService,
    private subjectService: SubjectMasterService,
    private academicYearService: AcademicYearService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
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
          this.loadClasses();
        }
      },
      error: (error) => {
        console.error('Error loading academic years:', error);
        this.snackBar.open('Error loading academic years', 'Close', { duration: 3000 });
      }
    });
  }

  onAcademicYearChange(): void {
    this.classes = [];
    this.sections = [];
    this.selectedClassId = undefined;
    this.selectedSectionId = undefined;
    this.routineData = [];
    this.buildGrid();
    
    if (this.selectedAcademicYearId) {
      this.loadClasses();
    }
  }

  loadClasses(): void {
    if (!this.selectedAcademicYearId) return;
    
    this.classMasterService.getAllClassesByAcademicYear(this.selectedAcademicYearId).subscribe({
      next: (data: ClassMasterResponse[]) => {
        this.classes = data.sort((a, b) => (a.displayOrder || 0) - (b.displayOrder || 0));
      },
      error: (error: any) => {
        console.error('Error loading classes:', error);
        this.snackBar.open('Failed to load classes', 'Close', { duration: 3000 });
        this.classes = [];
      }
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
        console.log('Routine data received:', data);
        console.log('Number of routines:', data.length);
        this.routineData = data;
        console.log('TimeSlots available:', this.timeSlots.length);
        this.buildGrid();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading routine:', error);
        const errorMsg = error?.error?.message || error?.message || 'Failed to load routine';
        this.showError(errorMsg);
        this.loading = false;
      }
    });
  }

  buildGrid(): void {
    if (!this.timeSlots || this.timeSlots.length === 0) {
      console.warn('No time slots available to build grid');
      return;
    }
    
    this.routineGrid = [];
    
    for (const day of this.daysOfWeek) {
      const dayRow: RoutineCell[] = [];
      
      for (const timeSlot of this.timeSlots) {
        const routine = this.routineData.find(r => {
          const dayMatch = r.dayOfWeek === day;
          // Check both timeSlotId and timeSlot.id since API returns nested object
          const slotMatch = r.timeSlotId === timeSlot.id || r.timeSlot?.id === timeSlot.id;
          return dayMatch && slotMatch;
        });
        
        dayRow.push({
          dayOfWeek: day,
          timeSlot: timeSlot,
          routine: routine
        });
      }
      
      this.routineGrid.push(dayRow);
    }
    
    console.log('=== Grid Build Complete ===');
    console.log('Final routineGrid:', this.routineGrid);
    
    // Count how many cells have routines
    let cellsWithData = 0;
    this.routineGrid.forEach(row => {
      row.forEach(cell => {
        if (cell.routine) cellsWithData++;
      });
    });
    console.log('Total cells with routine data:', cellsWithData);
  }

  getRoutineCell(dayIndex: number, slotIndex: number): RoutineCell | undefined {
    if (!this.routineGrid || dayIndex < 0 || dayIndex >= this.routineGrid.length) {
      return undefined;
    }
    const dayRow = this.routineGrid[dayIndex];
    if (!dayRow || slotIndex < 0 || slotIndex >= dayRow.length) {
      return undefined;
    }
    return dayRow[slotIndex];
  }

  getTeacherName(teacher: any): string {
    if (!teacher) return '';
    
    // Try different name properties in order of preference
    if (teacher.fullName) return teacher.fullName;
    if (teacher.firstName && teacher.lastName) return `${teacher.firstName} ${teacher.lastName}`;
    if (teacher.firstName) return teacher.firstName;
    if (teacher.lastName) return teacher.lastName;
    if (teacher.employeeCode) return teacher.employeeCode;
    
    return '';
  }

  openRoutineDialog(cell: RoutineCell | undefined): void {
    if (!cell) {
      this.snackBar.open('Unable to open routine dialog', 'Close', { duration: 3000 });
      return;
    }

    if (!this.selectedAcademicYearId || !this.selectedClassId || !this.selectedSectionId) {
      this.snackBar.open('Please select Academic Year, Class, and Section first', 'Close', { duration: 3000 });
      return;
    }

    // Don't add duplicate - just pass the subjects we have
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

  deleteRoutine(cell: RoutineCell | undefined): void {
    if (!cell || !cell.routine?.id) {
      return;
    }

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

  showWeekSummary(): void {
    if (!this.selectedAcademicYearId || !this.selectedClassId || !this.selectedSectionId) {
      this.snackBar.open('Please select Academic Year, Class, and Section first', 'Close', { duration: 3000 });
      return;
    }

    if (!this.routineData || this.routineData.length === 0) {
      this.snackBar.open('No routine data available to summarize', 'Close', { duration: 3000 });
      return;
    }

    // Get academic year name
    const selectedYear = this.academicYears.find(y => y.yearId === this.selectedAcademicYearId);
    const academicYearName = selectedYear?.yearName || 'N/A';

    // Get class and section names from routine data or from the dropdowns
    let className = 'N/A';
    let sectionName = 'N/A';
    
    if (this.routineData && this.routineData.length > 0) {
      // Get from first routine entry - use the string properties that are populated
      const firstRoutine = this.routineData[0];
      className = firstRoutine.className || firstRoutine.classMaster?.className || 'N/A';
      sectionName = firstRoutine.sectionName || firstRoutine.section?.sectionName || 'N/A';
    }
    
    // Fallback to selected dropdowns if not found in routine data
    if (className === 'N/A') {
      const selectedClass = this.classes.find(c => Number(c.id) === this.selectedClassId);
      className = selectedClass?.className || 'N/A';
    }
    if (sectionName === 'N/A') {
      const selectedSection = this.sections.find(s => Number(s.id) === this.selectedSectionId);
      sectionName = selectedSection?.sectionName || 'N/A';
    }

    this.dialog.open(RoutineSummaryDialogComponent, {
      width: '900px',
      maxWidth: '95vw',
      data: {
        routineData: this.routineData,
        academicYearName: academicYearName,
        className: className,
        sectionName: sectionName
      }
    });
  }

  showSuccess(message: string): void {
    this.snackBar.open(message, 'Close', { duration: 3000 });
  }

  showError(message: string): void {
    this.snackBar.open(message, 'Close', { duration: 5000 });
  }
}
