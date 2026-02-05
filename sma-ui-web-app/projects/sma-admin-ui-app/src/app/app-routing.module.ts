import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
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
import { StudentClassManagementComponent } from './components/student-class-management/student-class-management.component';
import { RoutineBuilderComponent } from './components/routine-builder/routine-builder.component';
import { TimeSlotManagementComponent } from './components/time-slot-management/time-slot-management.component';
import { DailyScheduleViewComponent } from './components/daily-schedule-view/daily-schedule-view.component';
import { AttendanceMarkingComponent } from './components/attendance-marking/attendance-marking.component';

const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'school-profile', component: SchoolProfileComponent },
  { path: 'academic-year', component: AcademicYearComponent },
  
  // Master Data Management Routes
  { path: 'classes', component: ClassListComponent },
  { path: 'classes/new', component: ClassFormComponent },
  { path: 'classes/:id/edit', component: ClassFormComponent },
  
  { path: 'sections', component: SectionListComponent },
  { path: 'sections/new', component: SectionFormComponent },
  { path: 'sections/:id/edit', component: SectionFormComponent },
  
  { path: 'subjects', component: SubjectListComponent },
  { path: 'subjects/new', component: SubjectFormComponent },
  { path: 'subjects/:id/edit', component: SubjectFormComponent },
  
  { path: 'departments', component: DepartmentListComponent },
  { path: 'departments/new', component: DepartmentFormComponent },
  { path: 'departments/:id/edit', component: DepartmentFormComponent },
  
  { path: 'student-class-management', component: StudentClassManagementComponent },
  
  // Routine Management
  { path: 'time-slot-management', component: TimeSlotManagementComponent },
  { path: 'routine-builder', component: RoutineBuilderComponent },
  { path: 'daily-schedule-view', component: DailyScheduleViewComponent },
  { path: 'attendance-marking/:sessionId', component: AttendanceMarkingComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
