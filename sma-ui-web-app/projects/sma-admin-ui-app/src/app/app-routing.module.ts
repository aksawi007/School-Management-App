import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { StaffManagementComponent } from './components/staff-management/staff-management.component';
import { StudentManagementComponent } from './components/student-management/student-management.component';
import { SchoolProfileComponent } from './components/school-profile/school-profile.component';
import { AcademicYearComponent } from './components/academic-year/academic-year.component';
import { StudentModuleComponent } from './components/student-module/student-module.component';
import { StaffModuleComponent } from './components/staff-module/staff-module.component';

const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'school-profile', component: SchoolProfileComponent },
  { path: 'academic-year', component: AcademicYearComponent },
  { path: 'student-module', component: StudentModuleComponent },
  { path: 'staff-module', component: StaffModuleComponent },
  { path: 'staff', component: StaffManagementComponent },
  { path: 'students', component: StudentManagementComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
