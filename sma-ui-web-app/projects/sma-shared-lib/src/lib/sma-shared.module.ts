import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';

// Services
import { SchoolProfileService, AcademicYearService } from './services/school';
import { StaffService } from './services/staff';
import { StudentService, EnrollmentService, GuardianService } from './services/student';
import { SchoolContextService } from './services/school-context.service';
import { AppContextService } from './services/app-context.service';
import { ExportService } from './services/export/export.service';
import { PrintService } from './services/print/print.service';

// Components
import { ExportPrintToolbarComponent } from './components/export-print-toolbar/export-print-toolbar.component';

@NgModule({
  declarations: [
    ExportPrintToolbarComponent
  ],
  imports: [
    CommonModule,
    HttpClientModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatTooltipModule
  ],
  exports: [
    ExportPrintToolbarComponent
  ],
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
    SchoolContextService,
    AppContextService,
    
    // Utility Services
    ExportService,
    PrintService
  ]
})
export class SmaSharedModule { }
