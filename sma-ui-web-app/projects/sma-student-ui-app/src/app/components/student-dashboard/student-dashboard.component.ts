import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

interface StudentAction {
  title: string;
  icon: string;
  route: string;
  color: string;
}

@Component({
  selector: 'app-student-dashboard',
  templateUrl: './student-dashboard.component.html',
  styleUrls: ['./student-dashboard.component.scss']
})
export class StudentDashboardComponent implements OnInit {
  sidenavOpen = true;

  studentActions: StudentAction[] = [
    {
      title: 'Student List',
      icon: 'people',
      route: '/students',
      color: '#1976d2'
    }
  ];

  constructor(private router: Router) {}

  ngOnInit(): void {
  }

  navigateToAction(action: StudentAction): void {
    this.router.navigate([action.route]);
  }

  toggleSidenav(): void {
    this.sidenavOpen = !this.sidenavOpen;
  }

  logout(): void {
    this.router.navigate(['/']);
  }
}
