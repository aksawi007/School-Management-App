import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatCardModule } from '@angular/material/card';

// Services
import { SchoolProfileService, AcademicYearService } from './services/school';
import { StaffService } from './services/staff';
import { StudentService, EnrollmentService, GuardianService } from './services/student';
import { SchoolContextService } from './services/school-context.service';
import { AppContextService } from './services/app-context.service';
import { ExportService } from './services/export/export.service';
import { PrintService } from './services/print/print.service';
import { VoiceCommandService } from './services/voice/voice-command.service';

// Components
import { ExportPrintToolbarComponent } from './components/export-print-toolbar/export-print-toolbar.component';
import { VoiceButtonComponent } from './components/voice-button/voice-button.component';

@NgModule({
  declarations: [
    ExportPrintToolbarComponent,
    VoiceButtonComponent
  ],
  imports: [
    CommonModule,
    HttpClientModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatTooltipModule,
    MatCardModule
  ],
  exports: [
    ExportPrintToolbarComponent,
    VoiceButtonComponent
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
    PrintService,
    VoiceCommandService
  ]
})
export class SmaSharedModule { }
