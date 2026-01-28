import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AppContextService, SchoolContext } from '../../services/app-context.service';

interface ModuleTile {
  id: string;
  title: string;
  description: string;
  details: string;
  icon: string;
  route: string;
  color: string;
}

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  selectedSchool: SchoolContext | null = null;

  moduleTiles: ModuleTile[] = [
    {
      id: 'admin',
      title: 'Admin Portal',
      description: 'Administrative setup and system configuration',
      details: 'Configure school profile, academic years, system settings, and access administrative tools.',
      icon: 'admin_panel_settings',
      route: '/admin',
      color: '#ff9800'
    },
    {
      id: 'student',
      title: 'Student Management',
      description: 'Manage student enrollments, profiles, and academics',
      details: 'Handle student admissions, track attendance, manage grades, and maintain comprehensive student records.',
      icon: 'school',
      route: '/student',
      color: '#3f51b5'
    },
    {
      id: 'staff',
      title: 'Staff Management',
      description: 'Manage staff members, roles, and assignments',
      details: 'Maintain staff profiles, manage assignments, track attendance, and handle payroll information.',
      icon: 'people',
      route: '/staff',
      color: '#4caf50'
    }
  ];

  constructor(
    private router: Router,
    private appContext: AppContextService
  ) {}

  ngOnInit(): void {
    // Subscribe to selected school
    this.appContext.selectedSchool$.subscribe(school => {
      this.selectedSchool = school;
      if (!school) {
        // If no school selected, redirect to selection
        this.router.navigate(['/']);
      }
    });
  }

  selectModule(module: ModuleTile): void {
    this.router.navigate([module.route]);
  }

  changeSchool(): void {
    this.appContext.clearSchool();
    this.router.navigate(['/']);
  }
}
