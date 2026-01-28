import { Component } from '@angular/core';

@Component({
  selector: 'app-module-fallback',
  template: `
    <div class="fallback-container">
      <mat-icon class="error-icon">error_outline</mat-icon>
      <h2>Module Not Available</h2>
      <p>The requested module could not be loaded. Please ensure the module is running.</p>
      <button mat-raised-button color="primary" routerLink="/dashboard">
        <mat-icon>arrow_back</mat-icon>
        Back to Dashboard
      </button>
    </div>
  `,
  styles: [`
    .fallback-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      min-height: 60vh;
      padding: 40px;
      text-align: center;

      .error-icon {
        font-size: 96px;
        width: 96px;
        height: 96px;
        color: #ff9800;
        margin-bottom: 24px;
      }

      h2 {
        font-size: 28px;
        margin: 0 0 16px 0;
        color: #333;
      }

      p {
        font-size: 16px;
        color: #666;
        margin: 0 0 32px 0;
        max-width: 500px;
      }

      button mat-icon {
        margin-right: 8px;
      }
    }
  `]
})
export class ModuleFallbackComponent {}
