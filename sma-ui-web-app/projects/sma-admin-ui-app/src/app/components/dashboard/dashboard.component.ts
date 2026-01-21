import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

interface StatCard {
  title: string;
  value: number;
  icon: string;
  color: string;
}

interface ModuleTile {
  title: string;
  description: string;
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
  stats: StatCard[] = [
    {
      title: 'Total Students',
      value: 1250,
      icon: 'school',
      color: '#3f51b5'
    },
    {
      title: 'Total Staff',
      value: 85,
      icon: 'people',
      color: '#4caf50'
    },
    {
      title: 'Active Courses',
      value: 42,
      icon: 'book',
      color: '#ff9800'
    },
    {
      title: 'Pending Enrollments',
      value: 15,
      icon: 'pending_actions',
      color: '#f44336'
    }
  ];

  moduleTiles: ModuleTile[] = [
    {
      title: 'Student Management',
      description: 'Manage student enrollments, profiles, and academics',
      icon: 'school',
      route: '/student-module',
      color: '#3f51b5'
    },
    {
      title: 'Staff Management',
      description: 'Manage staff members, roles, and assignments',
      icon: 'people',
      route: '/staff-module',
      color: '#4caf50'
    }
  ];

  constructor(private router: Router) { }

  ngOnInit(): void {
  }

  navigateToModule(module: ModuleTile): void {
    this.router.navigate([module.route]);
  }
}
