import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { VoiceCommandConfig, VoiceCommand } from '../models/voice-command.model';

@Injectable({
  providedIn: 'root'
})
export class CommandConfigService {
  private config$ = new BehaviorSubject<VoiceCommandConfig | null>(null);
  private configPath = '/assets/config/voice-commands.json';

  constructor(private http: HttpClient) {
    this.loadConfig();
  }

  loadConfig(): void {
    this.http.get<VoiceCommandConfig>(this.configPath).pipe(
      tap(config => console.log('Voice commands configuration loaded:', config))
    ).subscribe({
      next: (config) => this.config$.next(config),
      error: (error) => console.error('Error loading voice commands config:', error)
    });
  }

  getConfig(): Observable<VoiceCommandConfig | null> {
    return this.config$.asObservable();
  }

  getConfigValue(): VoiceCommandConfig | null {
    return this.config$.value;
  }

  getAllCommands(): VoiceCommand[] {
    const config = this.config$.value;
    if (!config) return [];

    const allCommands: VoiceCommand[] = [];
    Object.values(config.commands).forEach(category => {
      if (category.enabled) {
        allCommands.push(...category.commands);
      }
    });
    return allCommands;
  }

  getCommandsByCategory(categoryName: string): VoiceCommand[] {
    const config = this.config$.value;
    if (!config || !config.commands[categoryName]) return [];
    
    const category = config.commands[categoryName];
    return category.enabled ? category.commands : [];
  }

  isActionAllowed(action: string): boolean {
    const config = this.config$.value;
    if (!config) return false;

    const { restrictions } = config;
    
    // Check if action is in disallowed list
    if (restrictions.disallowedActions.includes(action.toLowerCase())) {
      return false;
    }

    // Check against disallowed patterns
    for (const pattern of restrictions.disallowedPatterns) {
      const regex = new RegExp(pattern, 'i');
      if (regex.test(action)) {
        return false;
      }
    }

    return true;
  }

  requiresConfirmation(action: string): boolean {
    const config = this.config$.value;
    if (!config) return true;

    return config.restrictions.requireConfirmationFor.includes(action);
  }

  expandSynonyms(text: string): string {
    const config = this.config$.value;
    if (!config || !config.synonyms) return text;

    let expandedText = text.toLowerCase();
    
    Object.entries(config.synonyms).forEach(([key, synonyms]) => {
      synonyms.forEach(synonym => {
        const regex = new RegExp(`\\b${synonym}\\b`, 'gi');
        expandedText = expandedText.replace(regex, key);
      });
    });

    return expandedText;
  }
}
