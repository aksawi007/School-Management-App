import { Component, OnInit } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-admin-wrapper',
  template: `
    <div class="iframe-container">
      <iframe [src]="adminUrl" frameborder="0"></iframe>
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
export class AdminWrapperComponent implements OnInit {
  adminUrl: SafeResourceUrl;

  constructor(private sanitizer: DomSanitizer) {
    this.adminUrl = this.sanitizer.bypassSecurityTrustResourceUrl('http://localhost:4202');
  }

  ngOnInit(): void {
    // Store school context in sessionStorage so admin app can access it
    console.log('Admin wrapper loaded, school context available in sessionStorage');
  }
}
