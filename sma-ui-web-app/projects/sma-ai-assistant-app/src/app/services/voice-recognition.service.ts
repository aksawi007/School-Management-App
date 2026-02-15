import { Injectable } from '@angular/core';
import { Subject, Observable, BehaviorSubject } from 'rxjs';

declare global {
  interface Window {
    SpeechRecognition: any;
    webkitSpeechRecognition: any;
  }
}

@Injectable({
  providedIn: 'root'
})
export class VoiceRecognitionService {
  private recognition: any;
  private isListening$ = new BehaviorSubject<boolean>(false);
  private transcript$ = new Subject<string>();
  private error$ = new Subject<string>();
  private interim$ = new Subject<string>();

  constructor() {
    this.initializeRecognition();
  }

  private initializeRecognition(): void {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    
    if (!SpeechRecognition) {
      console.error('Speech Recognition not supported in this browser');
      return;
    }

    this.recognition = new SpeechRecognition();
    this.setupRecognitionHandlers();
  }

  private setupRecognitionHandlers(): void {
    if (!this.recognition) return;

    // Set default options
    this.recognition.continuous = false;
    this.recognition.interimResults = true;
    this.recognition.lang = 'en-US';
    this.recognition.maxAlternatives = 3;

    this.recognition.onstart = () => {
      console.log('Voice recognition started');
      this.isListening$.next(true);
    };

    this.recognition.onresult = (event: any) => {
      let finalTranscript = '';
      let interimTranscript = '';

      for (let i = event.resultIndex; i < event.results.length; i++) {
        const transcript = event.results[i][0].transcript;
        const confidence = event.results[i][0].confidence;

        if (event.results[i].isFinal) {
          finalTranscript += transcript;
          console.log('Final transcript:', transcript, 'Confidence:', confidence);
          this.transcript$.next(transcript.trim());
        } else {
          interimTranscript += transcript;
          this.interim$.next(transcript.trim());
        }
      }
    };

    this.recognition.onerror = (event: any) => {
      console.error('Speech recognition error:', event.error);
      this.error$.next(event.error);
      this.isListening$.next(false);
    };

    this.recognition.onend = () => {
      console.log('Voice recognition ended');
      this.isListening$.next(false);
    };
  }

  startListening(options?: {
    continuous?: boolean;
    interimResults?: boolean;
    language?: string;
  }): void {
    if (!this.recognition) {
      this.error$.next('Speech Recognition not available');
      return;
    }

    if (this.isListening$.value) {
      console.log('Already listening');
      return;
    }

    // Apply options
    if (options) {
      if (options.continuous !== undefined) {
        this.recognition.continuous = options.continuous;
      }
      if (options.interimResults !== undefined) {
        this.recognition.interimResults = options.interimResults;
      }
      if (options.language) {
        this.recognition.lang = options.language;
      }
    }

    try {
      this.recognition.start();
    } catch (error) {
      console.error('Error starting recognition:', error);
      this.error$.next('Failed to start voice recognition');
    }
  }

  stopListening(): void {
    if (this.recognition && this.isListening$.value) {
      this.recognition.stop();
    }
  }

  isListening(): Observable<boolean> {
    return this.isListening$.asObservable();
  }

  getTranscript(): Observable<string> {
    return this.transcript$.asObservable();
  }

  getInterimTranscript(): Observable<string> {
    return this.interim$.asObservable();
  }

  getError(): Observable<string> {
    return this.error$.asObservable();
  }

  isSupported(): boolean {
    return !!(window.SpeechRecognition || window.webkitSpeechRecognition);
  }
}
