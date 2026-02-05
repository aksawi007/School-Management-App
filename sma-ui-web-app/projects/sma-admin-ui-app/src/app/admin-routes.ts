import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { SchoolProfileComponent } from './components/school-profile/school-profile.component';
import { AcademicYearComponent } from './components/academic-year/academic-year.component';

export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    component: DashboardComponent,
    children: [
      { path: 'school-profile', component: SchoolProfileComponent },
      { path: 'academic-year', component: AcademicYearComponent },
      { path: '', redirectTo: 'school-profile', pathMatch: 'full' }
    ]
  }
];
