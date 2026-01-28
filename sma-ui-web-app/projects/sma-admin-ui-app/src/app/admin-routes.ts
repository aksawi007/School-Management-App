import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { SchoolProfileComponent } from './components/school-profile/school-profile.component';
import { AcademicYearComponent } from './components/academic-year/academic-year.component';
import { StaffManagementComponent } from './components/staff-management/staff-management.component';
import { StudentManagementComponent } from './components/student-management/student-management.component';

export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    component: DashboardComponent,
    children: [
      { path: 'school-profile', component: SchoolProfileComponent },
      { path: 'academic-year', component: AcademicYearComponent },
      { path: 'staff', component: StaffManagementComponent },
      { path: 'students', component: StudentManagementComponent },
      { path: '', redirectTo: 'school-profile', pathMatch: 'full' }
    ]
  }
];
