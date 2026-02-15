export interface VoiceCommand {
  id: string;
  patterns: string[];
  action: string;
  target?: string;
  format?: string;
  filterType?: string;
  filterValue?: string;
  confirmationRequired: boolean;
  confirmationMessage?: string;
  parameterExtraction?: Record<string, string>;
  allowedRoles?: string[];
}

export interface CommandCategory {
  enabled: boolean;
  description: string;
  commands: VoiceCommand[];
}

export interface VoiceCommandConfig {
  version: string;
  lastUpdated: string;
  description: string;
  commands: Record<string, CommandCategory>;
  restrictions: {
    safeActionsOnly: boolean;
    description: string;
    disallowedActions: string[];
    disallowedPatterns: string[];
    requireConfirmationFor: string[];
  };
  settings: {
    language: string;
    continuous: boolean;
    interimResults: boolean;
    maxAlternatives: number;
    confidenceThreshold: number;
    autoStopTimeout: number;
    feedbackEnabled: boolean;
    visualFeedback: boolean;
    audioFeedback: boolean;
  };
  synonyms: Record<string, string[]>;
}

export interface ParsedCommand {
  originalText: string;
  matchedCommand: VoiceCommand | null;
  confidence: number;
  parameters?: Record<string, any>;
  category?: string;
  requiresConfirmation: boolean;
}

export interface CommandExecutionResult {
  success: boolean;
  message: string;
  action: string;
  data?: any;
}

export interface CommandHistoryItem {
  timestamp: Date;
  command: string;
  parsedCommand: ParsedCommand;
  executed: boolean;
  result?: CommandExecutionResult;
}
