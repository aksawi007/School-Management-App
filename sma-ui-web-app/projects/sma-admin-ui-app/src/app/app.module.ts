import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

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

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { StaffManagementComponent } from './components/staff-management/staff-management.component';
import { StudentManagementComponent } from './components/student-management/student-management.component';
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
    ClassStudentsDialogComponent
  ],
  imports: [
    BrowserModule,
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
    MatTooltipModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
