import { Component } from '@angular/core';

interface ModuleTile {
  title: string;
  description: string;
  details: string;
  icon: string;
  port: number;
  color: string;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  selectedModule: ModuleTile | null = null;

  moduleTiles: ModuleTile[] = [
    {
      title: 'Student Management',
      description: 'Manage student enrollments, profiles, and academics',
      details: 'Handle student admissions, track attendance, manage grades, and maintain comprehensive student records.',
      icon: 'school',
      port: 4200,
      color: '#3f51b5'
    },
    {
      title: 'Staff Management',
      description: 'Manage staff members, roles, and assignments',
      details: 'Maintain staff profiles, manage assignments, track attendance, and handle payroll information.',
      icon: 'people',
      port: 4201,
      color: '#4caf50'
    },
    {
      title: 'Admin Portal',
      description: 'Administrative setup and system configuration',
      details: 'Configure school profile, academic years, system settings, and access administrative tools.',
      icon: 'admin_panel_settings',
      port: 4202,
      color: '#ff9800'
    }
  ];

  selectModule(module: ModuleTile): void {
    this.selectedModule = module;
  }

  goHome(): void {
    this.selectedModule = null;
  }

  getModuleUrl(module: ModuleTile): string {
    return `http://localhost:${module.port}`;
  }
}
