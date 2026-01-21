import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <mat-toolbar color="primary">
      <span>Student Management System</span>
      <span class="spacer"></span>
      <button mat-button routerLink="/students">
        <mat-icon>people</mat-icon>
        Students
      </button>
    </mat-toolbar>
    <div class="container">
      <router-outlet></router-outlet>
    </div>
  `,
  styles: []
})
export class AppComponent {
  title = 'Student Management';
}
