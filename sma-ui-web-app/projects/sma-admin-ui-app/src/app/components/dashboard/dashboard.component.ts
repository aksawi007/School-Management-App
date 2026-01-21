import { Component, OnInit } from '@angular/core';

interface StatCard {
  title: string;
  value: number;
  icon: string;
  color: string;
}

@Component({
  selector: 'app-dashboard',
  template: `
    <div class="dashboard-container">
      <h1>Admin Dashboard</h1>
      
      <div class="stats-grid">
        <mat-card *ngFor="let stat of stats" [style.border-left]="'4px solid ' + stat.color">
          <mat-card-header>
            <mat-icon [style.color]="stat.color">{{ stat.icon }}</mat-icon>
            <mat-card-title>{{ stat.title }}</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <h2 class="stat-value">{{ stat.value }}</h2>
          </mat-card-content>
        </mat-card>
      </div>

      <div class="dashboard-actions mt-20">
        <mat-card>
          <mat-card-header>
            <mat-card-title>Quick Actions</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <div class="action-buttons">
              <button mat-raised-button color="primary" routerLink="/students">
                <mat-icon>add</mat-icon>
                Add Student
              </button>
              <button mat-raised-button color="primary" routerLink="/staff">
                <mat-icon>person_add</mat-icon>
                Add Staff
              </button>
              <button mat-raised-button color="accent">
                <mat-icon>assessment</mat-icon>
                View Reports
              </button>
            </div>
          </mat-card-content>
        </mat-card>
      </div>

      <div class="recent-activities mt-20">
        <mat-card>
          <mat-card-header>
            <mat-card-title>Recent Activities</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <mat-list>
              <mat-list-item>
                <mat-icon matListItemIcon>check_circle</mat-icon>
                <div matListItemTitle>New student enrolled</div>
                <div matListItemLine>2 hours ago</div>
              </mat-list-item>
              <mat-divider></mat-divider>
              <mat-list-item>
                <mat-icon matListItemIcon>update</mat-icon>
                <div matListItemTitle>Staff information updated</div>
                <div matListItemLine>4 hours ago</div>
              </mat-list-item>
              <mat-divider></mat-divider>
              <mat-list-item>
                <mat-icon matListItemIcon>school</mat-icon>
                <div matListItemTitle>New course added</div>
                <div matListItemLine>1 day ago</div>
              </mat-list-item>
            </mat-list>
          </mat-card-content>
        </mat-card>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-container {
      padding: 20px;
    }

    h1 {
      margin-bottom: 30px;
      color: #3f51b5;
    }

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 20px;
      margin-bottom: 20px;
    }

    .stats-grid mat-card {
      padding: 20px;
    }

    .stats-grid mat-card-header {
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .stats-grid mat-icon {
      font-size: 32px;
      width: 32px;
      height: 32px;
    }

    .stat-value {
      font-size: 36px;
      font-weight: bold;
      margin: 10px 0;
      color: #333;
    }

    .action-buttons {
      display: flex;
      gap: 15px;
      flex-wrap: wrap;
    }

    .action-buttons button {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .mt-20 {
      margin-top: 20px;
    }
  `]
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

  constructor() { }

  ngOnInit(): void {
  }
}
