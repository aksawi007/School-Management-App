import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SchoolSelectionComponent } from './components/school-selection/school-selection.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { AdminWrapperComponent } from './components/admin-wrapper/admin-wrapper.component';
import { StudentWrapperComponent } from './components/student-wrapper/student-wrapper.component';
import { StaffWrapperComponent } from './components/staff-wrapper/staff-wrapper.component';

const routes: Routes = [
  {
    path: '',
    component: SchoolSelectionComponent
  },
  {
    path: 'dashboard',
    component: DashboardComponent
  },
  {
    path: 'admin',
    component: AdminWrapperComponent
  },
  {
    path: 'student',
    component: StudentWrapperComponent
  },
  {
    path: 'staff',
    component: StaffWrapperComponent
  },
  {
    path: '**',
    redirectTo: ''
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
