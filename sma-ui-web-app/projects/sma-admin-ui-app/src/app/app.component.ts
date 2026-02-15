import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AdminCacheService } from './services/admin-cache.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'sma-admin-ui-app';

  constructor(
    private adminCache: AdminCacheService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Initialize school context from parent shell
    this.adminCache.initializeSchoolContext();
    
    // Preload academic years when school context is available
    this.adminCache.getAcademicYears().subscribe({
      next: () => console.log('Academic years preloaded'),
      error: (error) => console.error('Error preloading academic years:', error)
    });

    // Listen for navigation commands from shell app (voice commands)
    window.addEventListener('message', (event) => {
      // Security check - only accept from shell app
      // TODO: Get shell URL from environment config
      if (event.origin !== 'http://localhost:4300') {
        return;
      }

      if (event.data && event.data.type === 'NAVIGATE') {
        console.log('Voice command navigation received:', event.data.route);
        this.router.navigate([event.data.route]);
      }
    });
  }
}
