import { Component, OnInit, OnDestroy } from '@angular/core';
import { VoiceCommandService } from '../../services/voice/voice-command.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'lib-voice-button',
  template: `
    <button 
      *ngIf="isSupported"
      mat-fab 
      [color]="isListening ? 'warn' : 'primary'"
      (click)="toggleVoice()"
      class="voice-fab"
      [matTooltip]="isListening ? 'Stop listening' : 'Start voice commands'">
      <mat-icon>{{ isListening ? 'mic' : 'mic_none' }}</mat-icon>
    </button>

    <div *ngIf="lastCommand" class="voice-feedback">
      <mat-card class="command-card">
        <mat-card-content>
          <div class="command-text">
            <mat-icon>hearing</mat-icon>
            <span>{{ lastCommand }}</span>
          </div>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .voice-fab {
      position: fixed;
      bottom: 80px;
      right: 20px;
      z-index: 1000;
      box-shadow: 0 4px 8px rgba(0,0,0,0.3);
    }

    .voice-fab:hover {
      transform: scale(1.1);
      transition: transform 0.2s;
    }

    .voice-feedback {
      position: fixed;
      bottom: 150px;
      right: 20px;
      z-index: 999;
      max-width: 300px;
    }

    .command-card {
      background: #f5f5f5;
      animation: slideIn 0.3s ease-out;
    }

    @keyframes slideIn {
      from {
        transform: translateX(100%);
        opacity: 0;
      }
      to {
        transform: translateX(0);
        opacity: 1;
      }
    }

    .command-text {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
    }

    .command-text mat-icon {
      font-size: 20px;
      width: 20px;
      height: 20px;
      color: #1976d2;
    }
  `]
})
export class VoiceButtonComponent implements OnInit, OnDestroy {
  isListening = false;
  lastCommand = '';
  isSupported = false;
  private subscriptions: Subscription[] = [];
  private commandTimeout: any;

  constructor(private voiceService: VoiceCommandService) {}

  ngOnInit(): void {
    this.isSupported = this.voiceService.isSupported();

    if (this.isSupported) {
      this.subscriptions.push(
        this.voiceService.isListening$.subscribe(listening => {
          this.isListening = listening;
        }),
        
        this.voiceService.commandReceived$.subscribe(command => {
          this.lastCommand = command;
          
          // Clear previous timeout
          if (this.commandTimeout) {
            clearTimeout(this.commandTimeout);
          }
          
          // Hide command after 3 seconds
          this.commandTimeout = setTimeout(() => {
            this.lastCommand = '';
          }, 3000);
        })
      );
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
    if (this.commandTimeout) {
      clearTimeout(this.commandTimeout);
    }
  }

  toggleVoice(): void {
    if (this.isListening) {
      this.voiceService.stopListening();
    } else {
      this.voiceService.startListening();
    }
  }
}
