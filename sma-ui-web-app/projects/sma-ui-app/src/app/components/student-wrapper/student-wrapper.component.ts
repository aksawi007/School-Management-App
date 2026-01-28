import { Component, OnInit } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-student-wrapper',
  template: `
    <div class="iframe-container">
      <iframe [src]="studentUrl" frameborder="0"></iframe>
    </div>
  `,
  styles: [`
    .iframe-container {
      width: 100%;
      height: 100vh;
      position: relative;
    }
    
    iframe {
      width: 100%;
      height: 100%;
      border: none;
    }
  `]
})
export class StudentWrapperComponent implements OnInit {
  studentUrl: SafeResourceUrl;

  constructor(private sanitizer: DomSanitizer) {
    this.studentUrl = this.sanitizer.bypassSecurityTrustResourceUrl('http://localhost:4200');
  }

  ngOnInit(): void {
    console.log('Student wrapper loaded, school context available in sessionStorage');
  }
}
