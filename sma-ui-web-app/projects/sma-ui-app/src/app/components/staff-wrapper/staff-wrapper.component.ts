import { Component, OnInit } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-staff-wrapper',
  template: `
    <div class="iframe-container">
      <iframe [src]="staffUrl" frameborder="0"></iframe>
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
export class StaffWrapperComponent implements OnInit {
  staffUrl: SafeResourceUrl;

  constructor(private sanitizer: DomSanitizer) {
    this.staffUrl = this.sanitizer.bypassSecurityTrustResourceUrl('http://localhost:4201');
  }

  ngOnInit(): void {
    console.log('Staff wrapper loaded, school context available in sessionStorage');
  }
}
