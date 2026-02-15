import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { AppContextService } from '../../services/app-context.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-staff-wrapper',
  template: `
    <div class="iframe-container">
      <iframe #staffIframe [src]="staffUrl" frameborder="0" (load)="onIframeLoad()"></iframe>
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
export class StaffWrapperComponent implements OnInit, AfterViewInit {
  @ViewChild('staffIframe', { static: false }) iframe!: ElementRef<HTMLIFrameElement>;
  staffUrl: SafeResourceUrl;

  constructor(
    private sanitizer: DomSanitizer,
    private appContext: AppContextService
  ) {
    this.staffUrl = this.sanitizer.bypassSecurityTrustResourceUrl(environment.microFrontends.staff.url);
  }

  ngOnInit(): void {
    // Listen for context requests from child iframe
    window.addEventListener('message', (event) => {
      if (event.origin !== environment.microFrontends.staff.url) {
        return;
      }
      
      if (event.data && event.data.type === 'REQUEST_CONTEXT') {
        console.log('Child app requesting context, sending now...');
        this.sendContextToIframe();
      }
    });
  }

  ngAfterViewInit(): void {
  }

  onIframeLoad(): void {
    // Send initial context when iframe loads (may be too early)
    // Child will request again if it misses this
    this.sendContextToIframe();
  }
  
  private sendContextToIframe(): void {
    const school = this.appContext.getSchool();
    const user = this.appContext.getUser();
    
    if (this.iframe?.nativeElement?.contentWindow) {
      const message = {
        type: 'SCHOOL_CONTEXT',
        school: school,
        user: user
      };
      
      console.log('Sending context to staff iframe:', message);
      this.iframe.nativeElement.contentWindow.postMessage(message, environment.microFrontends.staff.url);
    }
  }
}
