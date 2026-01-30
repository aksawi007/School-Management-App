import { Component, OnInit } from '@angular/core';
import { AcademicYearCacheService } from './services/academic-year-cache.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'sma-admin-ui-app';

  constructor(private academicYearCache: AcademicYearCacheService) {}

  ngOnInit(): void {
    // Preload academic years when app starts
    this.academicYearCache.getAcademicYears().subscribe({
      next: () => console.log('Academic years preloaded'),
      error: (error) => console.error('Error preloading academic years:', error)
    });
  }
}
