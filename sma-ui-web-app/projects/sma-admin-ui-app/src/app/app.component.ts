import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <mat-toolbar color="primary">
      <button mat-icon-button (click)="drawer.toggle()">
        <mat-icon>menu</mat-icon>
      </button>
      <span>School Management Admin Portal</span>
      <span class="spacer"></span>
      <button mat-icon-button [matMenuTriggerFor]="menu">
        <mat-icon>account_circle</mat-icon>
      </button>
      <mat-menu #menu="matMenu">
        <button mat-menu-item>
          <mat-icon>person</mat-icon>
          <span>Profile</span>
        </button>
        <button mat-menu-item>
          <mat-icon>settings</mat-icon>
          <span>Settings</span>
        </button>
        <button mat-menu-item>
          <mat-icon>logout</mat-icon>
          <span>Logout</span>
        </button>
      </mat-menu>
    </mat-toolbar>

    <mat-drawer-container class="drawer-container">
      <mat-drawer #drawer mode="side" opened class="drawer">
        <mat-nav-list>
          <a mat-list-item routerLink="/dashboard" routerLinkActive="active">
            <mat-icon>dashboard</mat-icon>
            <span>Dashboard</span>
          </a>
          <a mat-list-item routerLink="/school-profile" routerLinkActive="active">
            <mat-icon>business</mat-icon>
            <span>School Profile</span>
          </a>
          <a mat-list-item routerLink="/academic-year" routerLinkActive="active">
            <mat-icon>calendar_today</mat-icon>
            <span>Academic Year</span>
          </a>
          <a mat-list-item routerLink="/students" routerLinkActive="active">
            <mat-icon>school</mat-icon>
            <span>Students</span>
          </a>
          <a mat-list-item routerLink="/staff" routerLinkActive="active">
            <mat-icon>people</mat-icon>
            <span>Staff</span>
          </a>
        </mat-nav-list>
      </mat-drawer>
      <mat-drawer-content class="main-content">
        <router-outlet></router-outlet>
      </mat-drawer-content>
    </mat-drawer-container>
  `,
  styles: [`
    .spacer {
      flex: 1 1 auto;
    }

    .drawer-container {
      height: calc(100vh - 64px);
    }

    .drawer {
      width: 250px;
    }

    .main-content {
      padding: 20px;
    }

    mat-nav-list a {
      display: flex;
      align-items: center;
      gap: 16px;
    }

    mat-nav-list a.active {
      background-color: rgba(63, 81, 181, 0.1);
    }
  `]
})
export class AppComponent {
  title = 'sma-admin-ui-app';
}
