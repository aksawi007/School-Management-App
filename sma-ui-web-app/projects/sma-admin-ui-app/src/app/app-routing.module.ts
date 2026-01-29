import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
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

const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'school-profile', component: SchoolProfileComponent },
  { path: 'academic-year', component: AcademicYearComponent },
  { path: 'staff', component: StaffManagementComponent },
  { path: 'students', component: StudentManagementComponent },
  
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
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
