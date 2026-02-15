# SMA AI Assistant App

Voice-controlled AI Assistant for School Management Application.

## Features

- 🎤 **Voice Commands**: Hands-free navigation and operations
- 🔒 **Safe Operations Only**: No delete/edit commands via voice
- ⚙️ **Configurable Commands**: Edit JSON files to add/modify commands
- 📊 **Export & Print**: Voice-controlled data export and printing
- 🔍 **Smart Search**: Natural language search queries
- 🎯 **Context Aware**: Understands current page context
- 🔮 **AI Ready**: Architecture supports future AI/ML enhancements

## Technology Stack

- **Voice Recognition**: Web Speech API (Native, open-source)
- **NLP (Future)**: compromise.js / natural.js
- **Command Matching**: Fuzzy matching with fuse.js
- **Framework**: Angular 15+
- **Language**: TypeScript

## Installation

### 1. Install Dependencies

```bash
cd sma-ui-web-app
npm install
```

### 2. Optional NLP Libraries (for future enhancements)

```bash
npm install compromise fuse.js --save
npm install natural --save  # Advanced NLP
```

## Configuration

### Voice Commands Configuration

Edit: `projects/sma-ai-assistant-app/src/assets/config/voice-commands.json`

```json
{
  "commands": {
    "navigation": {
      "enabled": true,
      "commands": [
        {
          "id": "nav_dashboard",
          "patterns": ["go to dashboard", "open dashboard"],
          "action": "navigate",
          "target": "/admin/dashboard"
        }
      ]
    }
  }
}
```

### AI Settings

Edit: `projects/sma-ai-assistant-app/src/assets/config/ai-settings.json`

## Supported Commands

### Navigation
- "Go to dashboard"
- "Open student management"
- "Show attendance"
- "Navigate to routine"

### Viewing
- "Show details"
- "Refresh page"
- "Search for [student name]"

### Export
- "Export to Excel"
- "Download CSV"
- "Save as PDF"

### Print
- "Print this page"
- "Show print preview"

### Filters
- "Show all"
- "Show today's data"
- "Clear filters"

### Help
- "Help"
- "What can you do"
- "List all commands"

## Security & Restrictions

### Disallowed Actions (Voice commands CANNOT):
- ❌ Delete records
- ❌ Edit/Modify data
- ❌ Create new records
- ❌ Update information
- ❌ Remove entries
- ❌ Deactivate users

### Allowed Actions (Voice commands CAN):
- ✅ Navigate between pages
- ✅ View information
- ✅ Search data
- ✅ Export reports (with confirmation)
- ✅ Print pages (with confirmation)
- ✅ Apply filters
- ✅ Refresh data

## Development

### Run AI Assistant App

```bash
ng serve sma-ai-assistant-app --port 4203
```

### Build for Production

```bash
ng build sma-ai-assistant-app --configuration production
```

### Add New Command

1. Edit `voice-commands.json`
2. Add command to appropriate category:

```json
{
  "id": "your_command_id",
  "patterns": ["command phrase 1", "command phrase 2"],
  "action": "actionName",
  "confirmationRequired": false
}
```

3. Implement action handler in command-processor.service.ts

## Architecture

```
sma-ai-assistant-app/
├── src/
│   ├── app/
│   │   ├── components/
│   │   │   ├── voice-panel/              # Main voice interface
│   │   │   ├── command-history/          # Command history display
│   │   │   └── settings-panel/           # Configuration UI
│   │   ├── services/
│   │   │   ├── voice-recognition.service.ts    # Web Speech API wrapper
│   │   │   ├── command-config.service.ts       # Config management
│   │   │   ├── command-parser.service.ts       # Parse voice to commands
│   │   │   └── command-processor.service.ts    # Execute commands
│   │   ├── models/
│   │   │   └── voice-command.model.ts          # TypeScript interfaces
│   │   └── guards/
│   │       └── safe-action.guard.ts            # Security validation
│   └── assets/
│       └── config/
│           ├── voice-commands.json             # ⚙️ EDITABLE CONFIG
│           └── ai-settings.json                # ⚙️ EDITABLE CONFIG
```

## Browser Compatibility

| Browser | Support | Notes |
|---------|---------|-------|
| Chrome | ✅ Full | Best support |
| Edge | ✅ Full | Chromium-based |
| Safari | ⚠️ Partial | Limited features |
| Firefox | ❌ Limited | Experimental only |

## Future Enhancements

### Phase 2 (Planned)
- [ ] Custom wake word ("Hey School Assistant")
- [ ] Multi-language support
- [ ] Voice feedback (text-to-speech)
- [ ] Command shortcuts/aliases

### Phase 3 (AI/ML)
- [ ] Custom trained ML models
- [ ] Context-aware suggestions
- [ ] Natural conversation support
- [ ] Intent prediction
- [ ] User behavior learning

### Phase 4 (Advanced)
- [ ] Offline voice recognition
- [ ] Voice biometrics
- [ ] Admin dashboard for command analytics
- [ ] A/B testing for command patterns

## Troubleshooting

### Microphone Not Working
1. Check browser permissions
2. Ensure HTTPS connection
3. Test microphone in browser settings

### Commands Not Recognized
1. Check confidence threshold in `ai-settings.json`
2. Add more patterns to `voice-commands.json`
3. Speak clearly and slowly
4. Check console for recognition errors

### Configuration Not Loading
1. Verify JSON syntax
2. Check file paths in angular.json assets
3. Clear browser cache
4. Rebuild the application

## Contributing

1. Edit configuration files for new commands
2. Test thoroughly in different browsers
3. Document new patterns
4. Submit PR with test results

## License

Part of School Management Application - Internal Use Only

## Support

For issues or questions, contact the development team.
