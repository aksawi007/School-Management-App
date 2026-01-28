import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AppContextService, SchoolContext } from '../../services/app-context.service';

@Component({
  selector: 'app-school-selection',
  templateUrl: './school-selection.component.html',
  styleUrls: ['./school-selection.component.scss']
})
export class SchoolSelectionComponent implements OnInit {
  schools: SchoolContext[] = [];
  loading = false;
  error = '';

  constructor(
    private http: HttpClient,
    private router: Router,
    private appContext: AppContextService
  ) {}

  ngOnInit(): void {
    this.loadSchools();
  }

  loadSchools(): void {
    this.loading = true;
    this.http.get<any[]>('/api/school/profile/getAll').subscribe({
      next: (data) => {
        this.schools = data.map(school => ({
          schoolId: school.schoolId,
          schoolName: school.schoolName,
          schoolCode: school.schoolCode
        }));
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading schools:', err);
        this.error = 'Failed to load schools. Please try again.';
        this.loading = false;
      }
    });
  }

  selectSchool(school: SchoolContext): void {
    this.appContext.setSchool(school);
    this.router.navigate(['/dashboard']);
  }
}
