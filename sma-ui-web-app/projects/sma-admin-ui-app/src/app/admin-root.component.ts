import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-root',
  template: `
    <div class="admin-container">
      <mat-toolbar color="primary">
        <button mat-icon-button (click)="goBack()">
          <mat-icon>arrow_back</mat-icon>
        </button>
        <span>Admin Portal</span>
      </mat-toolbar>
      <div class="admin-content">
        <app-dashboard></app-dashboard>
      </div>
    </div>
  `,
  styles: [`
    .admin-container {
      height: 100%;
      display: flex;
      flex-direction: column;
    }
    .admin-content {
      flex: 1;
      overflow: auto;
    }
  `]
})
export class AdminRootComponent implements OnInit {
  
  constructor(private router: Router) {}

  ngOnInit(): void {
  }

  goBack(): void {
    this.router.navigate(['/dashboard']);
  }
}
