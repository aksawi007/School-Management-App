import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { StaffListComponent } from './components/staff-list/staff-list.component';
import { StaffFormComponent } from './components/staff-form/staff-form.component';
import { StaffDetailComponent } from './components/staff-detail/staff-detail.component';

const routes: Routes = [
  { path: '', redirectTo: '/staff', pathMatch: 'full' },
  { path: 'staff', component: StaffListComponent },
  { path: 'staff/new', component: StaffFormComponent },
  { path: 'staff/:id', component: StaffDetailComponent },
  { path: 'staff/:id/edit', component: StaffFormComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
