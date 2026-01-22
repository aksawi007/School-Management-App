import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';

// Services
import { SchoolProfileService, AcademicYearService } from './services/school';
import { StaffService } from './services/staff';
import { StudentService, EnrollmentService, GuardianService } from './services/student';
import { SchoolContextService } from './services/school-context.service';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    HttpClientModule
  ],
  exports: [],
  providers: [
    // School Management Services
    SchoolProfileService,
    AcademicYearService,
    
    // Staff Management Services
    StaffService,
    
    // Student Management Services
    StudentService,
    EnrollmentService,
    GuardianService,
    
    // Common Services
    SchoolContextService
  ]
})
export class SmaSharedModule { }
