import { Injectable, InjectionToken } from '@angular/core';

export interface MicroFrontendConfig {
  url: string;
  port: number;
  host: string;
}

export interface EnvironmentConfig {
  production: boolean;
  shell: {
    url: string;
    port: number;
  };
  microFrontends: {
    student: MicroFrontendConfig;
    staff: MicroFrontendConfig;
    admin: MicroFrontendConfig;
    ai: MicroFrontendConfig;
  };
  api?: {
    baseUrl: string;
    timeout: number;
  };
}

export const ENVIRONMENT_CONFIG = new InjectionToken<EnvironmentConfig>('EnvironmentConfig');

@Injectable({
  providedIn: 'root'
})
export class EnvironmentService {
  private config: EnvironmentConfig | null = null;

  setConfig(config: EnvironmentConfig): void {
    this.config = config;
  }

  getConfig(): EnvironmentConfig {
    if (!this.config) {
      throw new Error('Environment config not set. Call setConfig() first.');
    }
    return this.config;
  }

  getMicroFrontendUrl(app: 'student' | 'staff' | 'admin' | 'ai'): string {
    return this.getConfig().microFrontends[app].url;
  }

  getShellUrl(): string {
    return this.getConfig().shell.url;
  }
}
