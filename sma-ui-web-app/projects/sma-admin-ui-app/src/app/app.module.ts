import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';

// Material imports
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatMenuModule } from '@angular/material/menu';
import { MatChipsModule } from '@angular/material/chips';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatExpansionModule } from '@angular/material/expansion';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { SchoolProfileComponent } from './components/school-profile/school-profile.component';
import { AcademicYearComponent } from './components/academic-year/academic-year.component';
import { ClassListComponent } from './components/class-list/class-list.component';
import { ClassFormComponent } from './components/class-form/class-form.component';
import { SectionListComponent } from './components/section-list/section-list.component';
import { SectionFormComponent } from './components/section-form/section-form.component';
import { SubjectListComponent } from './components/subject-list/subject-list.component';
import { SubjectFormComponent } from './components/subject-form/subject-form.component';
import { DepartmentListComponent } from './components/department-list/department-list.component';
import { DepartmentFormComponent } from './components/department-form/department-form.component';
import { DepartmentStaffDialogComponent } from './components/department-staff-dialog/department-staff-dialog.component';
import { StudentClassManagementComponent } from './components/student-class-management/student-class-management.component';
import { StudentClassAssignDialogComponent } from './components/student-class-assign-dialog/student-class-assign-dialog.component';
import { ClassStudentsDialogComponent } from './components/class-students-dialog/class-students-dialog.component';
import { RoutineBuilderComponent } from './components/routine-builder/routine-builder.component';
import { RoutineEntryDialogComponent } from './components/routine-entry-dialog/routine-entry-dialog.component';
import { RoutineSummaryDialogComponent } from './components/routine-summary-dialog/routine-summary-dialog.component';
import { TimeSlotManagementComponent } from './components/time-slot-management/time-slot-management.component';
import { DailyScheduleViewComponent } from './components/daily-schedule-view/daily-schedule-view.component';
import { SessionOverrideDialogComponent } from './components/session-override-dialog/session-override-dialog.component';
import { AttendanceMarkingComponent } from './components/attendance-marking/attendance-marking.component';
import { TimeSlotDialogComponent } from './components/time-slot-dialog/time-slot-dialog.component';
import { FeeCategoryListComponent } from './components/fee-category-list/fee-category-list.component';
import { FeeCategoryFormComponent } from './components/fee-category-form/fee-category-form.component';
import { FeePlanListComponent } from './components/fee-plan-list/fee-plan-list.component';
import { FeePlanFormComponent } from './components/fee-plan-form/fee-plan-form.component';

import { SmaSharedModule } from 'sma-shared-lib';

@NgModule({
  declarations: [
    AppComponent,
    ClassListComponent,
    ClassFormComponent,
    SectionListComponent,
    SectionFormComponent,
    SubjectListComponent,
    SubjectFormComponent,
    DepartmentListComponent,
    DepartmentFormComponent,
    DepartmentStaffDialogComponent,
    StudentClassManagementComponent,
    StudentClassAssignDialogComponent,
    ClassStudentsDialogComponent,
    RoutineBuilderComponent,
    RoutineEntryDialogComponent,
    RoutineSummaryDialogComponent,
    TimeSlotManagementComponent,
    TimeSlotDialogComponent,
    DailyScheduleViewComponent,
    SessionOverrideDialogComponent,
    AttendanceMarkingComponent,
    FeeCategoryListComponent,
    FeeCategoryFormComponent,
    FeePlanListComponent,
    FeePlanFormComponent
  ],
  imports: [
    BrowserModule,
    CommonModule,
    BrowserAnimationsModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    AppRoutingModule,
    SmaSharedModule,
    // Material modules
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatAutocompleteModule,
    MatDialogModule,
    MatSnackBarModule,
    MatTabsModule,
    MatProgressSpinnerModule,
    MatSidenavModule,
    MatListModule,
    MatGridListModule,
    MatMenuModule,
    MatChipsModule,
    MatCheckboxModule,
    MatTooltipModule,
    MatProgressBarModule,
    MatExpansionModule
  ],
  providers: [DatePipe],
  bootstrap: [AppComponent]
})
export class AppModule { }
