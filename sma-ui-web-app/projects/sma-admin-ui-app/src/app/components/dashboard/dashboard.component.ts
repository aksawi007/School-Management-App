import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

interface StatCard {
  title: string;
  value: number;
  icon: string;
  color: string;
}

interface AdminAction {
  title: string;
  description: string;
  icon: string;
  route: string;
  color: string;
  category: string;
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

  adminActions: AdminAction[] = [
    // Core Setup
    {
      title: 'School Profile',
      description: 'Manage school information and details',
      icon: 'business',
      route: '/school-profile',
      color: '#1976d2',
      category: 'core'
    },
    {
      title: 'Academic Year',
      description: 'Configure academic sessions and terms',
      icon: 'calendar_today',
      route: '/academic-year',
      color: '#1976d2',
      category: 'core'
    },
    // Academic Management
    {
      title: 'Manage Classes',
      description: 'Setup classes, sections and divisions',
      icon: 'class',
      route: '/classes',
      color: '#7b1fa2',
      category: 'academic'
    },
    {
      title: 'Manage Subjects',
      description: 'Configure subjects and curriculum',
      icon: 'menu_book',
      route: '/subjects',
      color: '#7b1fa2',
      category: 'academic'
    },
    {
      title: 'Department Management',
      description: 'Manage school departments and HODs',
      icon: 'apartment',
      route: '/departments',
      color: '#7b1fa2',
      category: 'academic'
    },
    {
      title: 'Course Management',
      description: 'Setup courses and syllabus',
      icon: 'book',
      route: '/courses',
      color: '#7b1fa2',
      category: 'academic'
    },
    // User Management
    {
      title: 'Student Management',
      description: 'View and manage student records',
      icon: 'school',
      route: '/students',
      color: '#2e7d32',
      category: 'users'
    },
    {
      title: 'Staff Management',
      description: 'Manage teachers and staff members',
      icon: 'people',
      route: '/staff',
      color: '#2e7d32',
      category: 'users'
    },
    {
      title: 'User Roles',
      description: 'Configure user roles and permissions',
      icon: 'admin_panel_settings',
      route: '/roles',
      color: '#2e7d32',
      category: 'users'
    },
    // Financial
    {
      title: 'Fee Structure',
      description: 'Setup fee categories and amounts',
      icon: 'payments',
      route: '/fee-structure',
      color: '#ed6c02',
      category: 'financial'
    },
    {
      title: 'Fee Collection',
      description: 'Track and manage fee payments',
      icon: 'account_balance',
      route: '/fee-collection',
      color: '#ed6c02',
      category: 'financial'
    },
    // Settings
    {
      title: 'Attendance Settings',
      description: 'Configure attendance policies',
      icon: 'how_to_reg',
      route: '/attendance-settings',
      color: '#d32f2f',
      category: 'settings'
    },
    {
      title: 'Exam Configuration',
      description: 'Setup exam types and grading',
      icon: 'assignment',
      route: '/exam-config',
      color: '#d32f2f',
      category: 'settings'
    },
    {
      title: 'System Settings',
      description: 'General system configuration',
      icon: 'settings',
      route: '/system-settings',
      color: '#d32f2f',
      category: 'settings'
    }
  ];

  constructor(private router: Router) { }

  ngOnInit(): void {
  }

  getCategoryTitle(category: string): string {
    const titles: { [key: string]: string } = {
      'core': 'Core Setup',
      'academic': 'Academic Management',
      'users': 'User Management',
      'financial': 'Financial Management',
      'settings': 'System Settings'
    };
    return titles[category] || category;
  }

  getActionsByCategory(category: string): AdminAction[] {
    return this.adminActions.filter(action => action.category === category);
  }

  navigateToAction(action: AdminAction): void {
    this.router.navigate([action.route]);
  }

  get categories(): string[] {
    return ['core', 'academic', 'users', 'financial', 'settings'];
  }
}
