import { Component, OnInit } from '@angular/core';
import { AdminCacheService } from './services/admin-cache.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'sma-admin-ui-app';

  constructor(private adminCache: AdminCacheService) {}

  ngOnInit(): void {
    // Initialize school context from parent shell
    this.adminCache.initializeSchoolContext();
    
    // Preload academic years when school context is available
    this.adminCache.getAcademicYears().subscribe({
      next: () => console.log('Academic years preloaded'),
      error: (error) => console.error('Error preloading academic years:', error)
    });
  }
}
