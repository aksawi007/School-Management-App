import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AppContextService, SchoolContext, UserContext } from './services/app-context.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  showHeader = false;
  selectedSchool: SchoolContext | null = null;
  user: UserContext | null = null;

  constructor(
    private router: Router,
    private appContext: AppContextService
  ) {}

  ngOnInit(): void {
    // Subscribe to route changes to show/hide header
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      this.showHeader = event.url !== '/';
    });

    // Subscribe to context changes
    this.appContext.selectedSchool$.subscribe(school => {
      this.selectedSchool = school;
    });

    this.appContext.userContext$.subscribe(user => {
      this.user = user;
    });

    // TODO: Load user context from authentication service
    // For now, set a mock user
    this.appContext.setUser({
      userId: 1,
      username: 'admin',
      role: 'ADMIN',
      email: 'admin@school.com'
    });
  }

  goHome(): void {
    if (this.selectedSchool) {
      this.router.navigate(['/dashboard']);
    } else {
      this.router.navigate(['/']);
    }
  }

  logout(): void {
    this.appContext.clearAll();
    this.router.navigate(['/']);
  }
}
