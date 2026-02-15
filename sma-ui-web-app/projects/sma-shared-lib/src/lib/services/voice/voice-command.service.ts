import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { EnvironmentService } from '../environment.service';

interface VoiceCommand {
  route: string;
  patterns: string[];
}

interface VoiceConfig {
  commands: VoiceCommand[];
  settings: {
    language: string;
    confidenceThreshold: number;
  };
}

@Injectable({
  providedIn: 'root'
})
export class VoiceCommandService {
  private recognition: any;
  private isListening = false;
  private config: VoiceConfig | null = null;
  
  public commandReceived$ = new Subject<string>();
  public isListening$ = new Subject<boolean>();
  
  constructor(
    private http: HttpClient,
    private router: Router,
    private snackBar: MatSnackBar,
    private envService: EnvironmentService
  ) {
    this.initializeRecognition();
    this.loadConfig();
  }

  private initializeRecognition(): void {
    const SpeechRecognition = (window as any).SpeechRecognition || 
                              (window as any).webkitSpeechRecognition;
    
    if (!SpeechRecognition) {
      console.error('Speech Recognition not supported');
      return;
    }

    this.recognition = new SpeechRecognition();
    this.recognition.continuous = false;
    this.recognition.interimResults = true;
    this.recognition.lang = 'en-US';

    this.recognition.onresult = (event: any) => {
      for (let i = event.resultIndex; i < event.results.length; i++) {
        if (event.results[i].isFinal) {
          const transcript = event.results[i][0].transcript.trim();
          this.commandReceived$.next(transcript);
          this.processCommand(transcript);
        }
      }
    };

    this.recognition.onerror = (event: any) => {
      console.error('Speech recognition error:', event.error);
      this.isListening = false;
      this.isListening$.next(false);
    };

    this.recognition.onend = () => {
      this.isListening = false;
      this.isListening$.next(false);
    };
  }

  private loadConfig(): void {
    this.http.get<VoiceConfig>('/assets/config/voice-commands.json').subscribe({
      next: (config) => {
        this.config = config;
        console.log('Voice commands loaded:', config.commands.length);
      },
      error: (error) => console.error('Error loading voice config:', error)
    });
  }

  startListening(): void {
    if (!this.recognition) {
      this.snackBar.open('Voice recognition not available', 'Close', { duration: 3000 });
      return;
    }

    if (this.isListening) return;

    this.recognition.start();
    this.isListening = true;
    this.isListening$.next(true);
    this.snackBar.open('🎤 Listening...', '', { duration: 2000 });
  }

  stopListening(): void {
    if (this.recognition && this.isListening) {
      this.recognition.stop();
    }
  }

  private processCommand(text: string): void {
    console.log('Processing:', text);
    
    if (!this.config) {
      this.snackBar.open('Voice commands not loaded', 'Close', { duration: 3000 });
      return;
    }

    const route = this.findRoute(text);
    
    if (route) {
      this.navigate(route, text);
    } else {
      console.log('No match found');
      this.snackBar.open('Command not recognized. Try: "classes", "students", "attendance"', 'Close', { duration: 3000 });
    }
  }

  private findRoute(text: string): string | null {
    const lowerText = text.toLowerCase().trim();
    
    for (const command of this.config!.commands) {
      for (const pattern of command.patterns) {
        if (lowerText === pattern.toLowerCase() || 
            lowerText.includes(pattern.toLowerCase())) {
          console.log('✓ Matched:', pattern, '→', command.route);
          return command.route;
        }
      }
    }
    
    return null;
  }

  private navigate(route: string, originalCommand: string): void {
    // Handle micro-frontend routes (iframe-based)
    if (route.startsWith('/admin/')) {
      const subRoute = route.replace('/admin', '');
      const adminUrl = this.envService.getMicroFrontendUrl('admin');
      
      this.router.navigate(['/admin']).then(() => {
        setTimeout(() => {
          const iframe = document.querySelector(`iframe[src*="${new URL(adminUrl).port}"]`) as HTMLIFrameElement;
          if (iframe?.contentWindow) {
            iframe.contentWindow.postMessage({
              type: 'NAVIGATE',
              route: subRoute
            }, adminUrl);
          }
        }, 500);
      });
      
      this.snackBar.open(`✓ Navigating to ${route}`, 'Close', { duration: 2000 });
    } 
    else if (route.startsWith('/staff/')) {
      const subRoute = route.replace('/staff', '');
      const staffUrl = this.envService.getMicroFrontendUrl('staff');
      
      this.router.navigate(['/staff']).then(() => {
        setTimeout(() => {
          const iframe = document.querySelector(`iframe[src*="${new URL(staffUrl).port}"]`) as HTMLIFrameElement;
          if (iframe?.contentWindow) {
            iframe.contentWindow.postMessage({
              type: 'NAVIGATE',
              route: subRoute
            }, staffUrl);
          }
        }, 500);
      });
      
      this.snackBar.open(`✓ Navigating to ${route}`, 'Close', { duration: 2000 });
    }
    else {
      // Direct shell route
      this.router.navigate([route]);
      this.snackBar.open(`✓ Navigating to ${route}`, 'Close', { duration: 2000 });
    }
  }

  isSupported(): boolean {
    return !!((window as any).SpeechRecognition || (window as any).webkitSpeechRecognition);
  }
}
