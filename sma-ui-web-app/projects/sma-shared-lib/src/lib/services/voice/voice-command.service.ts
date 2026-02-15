import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Subject, Observable } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { EnvironmentService } from '../environment.service';

export interface ParsedCommand {
  originalText: string;
  action: string;
  target?: string;
  format?: string;
  confidence: number;
}

@Injectable({
  providedIn: 'root'
})
export class VoiceCommandService {
  private recognition: any;
  private isListening = false;
  private commandConfig: any = null;
  
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
          const transcript = event.results[i][0].transcript;
          this.commandReceived$.next(transcript.trim());
          this.processCommand(transcript.trim());
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
    this.http.get('/assets/config/voice-commands.json').subscribe({
      next: (config) => {
        this.commandConfig = config;
        console.log('Voice commands config loaded');
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
    console.log('Processing command:', text);
    const parsed = this.parseCommand(text);
    
    if (!parsed) {
      this.snackBar.open('Command not recognized', 'Close', { duration: 3000 });
      return;
    }

    this.executeCommand(parsed);
  }

  private parseCommand(text: string): ParsedCommand | null {
    if (!this.commandConfig) {
      console.log('Command config not loaded yet');
      return null;
    }

    const lowerText = text.toLowerCase().trim();
    console.log('Parsing text:', lowerText);

    // Quick keyword shortcuts for common navigation
    const shortcuts: { [key: string]: string } = {
      'dashboard': '/dashboard',
      'admin': '/admin',
      'students': '/admin/students',
      'student': '/admin/students',
      'classes': '/admin/classes',
      'class': '/admin/classes',
      'sections': '/admin/sections',
      'section': '/admin/sections',
      'routine': '/admin/routine',
      'schedule': '/admin/routine',
      'timetable': '/admin/routine',
      'attendance': '/admin/attendance',
      'staff': '/staff',
      'teacher': '/staff',
      'teachers': '/staff'
    };

    // Check for single-word shortcuts first
    if (shortcuts[lowerText]) {
      console.log('✓ Matched shortcut:', lowerText);
      return {
        originalText: text,
        action: 'navigate',
        target: shortcuts[lowerText],
        confidence: 0.9
      };
    }

    // Check all command categories
    for (const [category, config] of Object.entries(this.commandConfig.commands)) {
      const categoryConfig = config as any;
      if (!categoryConfig.enabled) continue;

      for (const command of categoryConfig.commands) {
        for (const pattern of command.patterns) {
          const patternLower = pattern.toLowerCase();
          
          // Check for exact match or partial match
          if (lowerText === patternLower || 
              lowerText.includes(patternLower) ||
              this.fuzzyMatch(lowerText, patternLower)) {
            console.log('✓ Matched pattern:', pattern);
            return {
              originalText: text,
              action: command.action,
              target: command.target,
              format: command.format,
              confidence: 0.8
            };
          }
        }
      }
    }

    console.log('✗ No match found for:', lowerText);
    return null;
  }

  private fuzzyMatch(text: string, pattern: string): boolean {
    // Remove common words and check if key words match
    const removeWords = ['to', 'the', 'a', 'an', 'and', 'or', 'please', 'can', 'you'];
    const cleanText = text.split(' ').filter(w => !removeWords.includes(w)).join(' ');
    const cleanPattern = pattern.split(' ').filter(w => !removeWords.includes(w)).join(' ');
    
    // Check if all pattern words are in the text
    const patternWords = cleanPattern.split(' ');
    const textWords = cleanText.split(' ');
    
    return patternWords.every(pw => 
      textWords.some(tw => tw.includes(pw) || pw.includes(tw))
    );
  }

  private executeCommand(command: ParsedCommand): void {
    console.log('Executing command:', command);

    switch (command.action) {
      case 'navigate':
        if (command.target) {
          // Handle micro-frontend routes (iframe-based)
          if (command.target.startsWith('/admin/')) {
            const subRoute = command.target.replace('/admin', '');
            const adminUrl = this.envService.getMicroFrontendUrl('admin');
            this.router.navigate(['/admin']).then(() => {
              // Send navigation message to admin iframe
              setTimeout(() => {
                const adminIframe = document.querySelector(`iframe[src*="${new URL(adminUrl).port}"]`) as HTMLIFrameElement;
                if (adminIframe && adminIframe.contentWindow) {
                  adminIframe.contentWindow.postMessage({
                    type: 'NAVIGATE',
                    route: subRoute
                  }, adminUrl);
                  this.snackBar.open(`✓ Navigating to ${command.target}`, 'Close', { duration: 2000 });
                }
              }, 500);
            });
          } else if (command.target.startsWith('/staff/')) {
            const subRoute = command.target.replace('/staff', '');
            const staffUrl = this.envService.getMicroFrontendUrl('staff');
            this.router.navigate(['/staff']).then(() => {
              setTimeout(() => {
                const staffIframe = document.querySelector(`iframe[src*="${new URL(staffUrl).port}"]`) as HTMLIFrameElement;
                if (staffIframe && staffIframe.contentWindow) {
                  staffIframe.contentWindow.postMessage({
                    type: 'NAVIGATE',
                    route: subRoute
                  }, staffUrl);
                  this.snackBar.open(`✓ Navigating to ${command.target}`, 'Close', { duration: 2000 });
                }
              }, 500);
            });
          } else {
            // Direct navigation for shell routes
            this.router.navigate([command.target]);
            this.snackBar.open(`✓ Navigating to ${command.target}`, 'Close', { duration: 2000 });
          }
        }
        break;

      case 'export':
        this.snackBar.open(`Exporting to ${command.format}...`, 'Close', { duration: 2000 });
        // Emit event that other components can listen to
        window.dispatchEvent(new CustomEvent('voice-export', { 
          detail: { format: command.format } 
        }));
        break;

      case 'print':
        this.snackBar.open('Opening print dialog...', 'Close', { duration: 2000 });
        setTimeout(() => window.print(), 500);
        break;

      case 'refresh':
        this.snackBar.open('Refreshing...', 'Close', { duration: 2000 });
        window.location.reload();
        break;

      case 'showHelp':
        this.showAvailableCommands();
        break;

      default:
        this.snackBar.open('Command recognized but not implemented', 'Close', { duration: 3000 });
    }
  }

  private showAvailableCommands(): void {
    const commands = [
      'Navigation: "go to dashboard", "open students", "show attendance"',
      'Export: "export to excel", "download csv"',
      'Print: "print this page"',
      'Help: "help", "what can you do"'
    ];
    
    console.log('Available commands:', commands);
    this.snackBar.open('Available commands logged to console', 'Close', { duration: 3000 });
  }

  isSupported(): boolean {
    return !!((window as any).SpeechRecognition || (window as any).webkitSpeechRecognition);
  }
}
