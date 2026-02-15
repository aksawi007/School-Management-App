# Voice Navigation - Simple Setup Guide

## Overview

Voice commands for navigation - just say where you want to go!

## Configuration File

**Location:** `projects/sma-ui-app/src/assets/config/voice-commands.json`

### Structure

```json
{
  "commands": [
    {
      "route": "/admin/students",
      "patterns": ["students", "go to students", "show students"]
    }
  ]
}
```

That's it! Just add:
- **route**: Where to navigate
- **patterns**: What to say

## Adding New Commands

Edit `voice-commands.json` and add a new entry:

```json
{
  "route": "/your/route/here",
  "patterns": ["keyword", "phrase to say", "another phrase"]
}
```

### Examples

**1. Add Homework Page:**
```json
{
  "route": "/admin/homework",
  "patterns": ["homework", "assignments", "go to homework", "show homework"]
}
```

**2. Add Reports Page:**
```json
{
  "route": "/admin/reports",
  "patterns": ["reports", "show reports", "open reports", "view reports"]
}
```

**3. Add Settings:**
```json
{
  "route": "/settings",
  "patterns": ["settings", "preferences", "open settings", "configuration"]
}
```

## How to Use

1. **Click microphone button** (bottom-right corner)
2. **Wait for red indicator** (means listening)
3. **Say a command**: "students" or "go to classes"
4. **Done!** - Navigates to that page

## Supported Commands (Default)

| Say This | Goes To |
|----------|---------|
| "dashboard" | Dashboard page |
| "admin" | Admin panel |
| "students" or "student" | Student management |
| "classes" or "class" | Class management |
| "sections" or "section" | Section management |
| "routine" or "schedule" | Timetable/Routine |
| "attendance" | Attendance page |
| "staff" or "teachers" | Staff management |

## Pattern Matching

The system matches your speech in two ways:

1. **Exact match**: "students" matches "students"
2. **Contains match**: "go to students" matches "students"

### Tips for Good Patterns

✅ **Good:**
- Short keywords: "students", "classes"
- Natural phrases: "go to students", "show classes"
- Common variations: "student", "students", "student list"

❌ **Avoid:**
- Too long: "please navigate to the student management page"
- Ambiguous: "show" (could mean anything)
- Complex: "filter students by class and section"

## Micro-Frontend Support

Routes starting with `/admin/` or `/staff/` automatically work with iframes:
- Says "students" → Shell loads `/admin` → Sends `/students` to admin iframe
- No extra configuration needed!

## Testing

### Quick Test:
1. Open browser console (F12)
2. Click microphone
3. Say "classes"
4. Check console logs:
   ```
   Processing: classes
   ✓ Matched: classes → /admin/classes
   ```

### Debug Mode:
Open console and watch for:
- `Voice commands loaded: 8` (on page load)
- `Processing: your text` (when you speak)
- `✓ Matched: pattern → /route` (when matched)

## Troubleshooting

**Command not recognized:**
- Check if pattern exists in voice-commands.json
- Try exact keyword: "students" instead of "go to students"
- Check console for "No match found"

**Microphone not working:**
- Allow microphone permission in browser
- Check if browser supports Web Speech API (Chrome, Edge)
- Look for errors in console

**Page doesn't navigate:**
- Verify route exists in your app
- Check if route is correct in voice-commands.json
- Restart app after config changes

## Build & Deploy

After editing voice-commands.json:

```bash
# 1. Build shared library (only needed if service changed)
ng build sma-shared-lib

# 2. Restart main app
npm run start:main

# 3. Test voice commands
```

Configuration file changes don't need rebuild - just refresh the page!

## File Locations

- **Config:** `projects/sma-ui-app/src/assets/config/voice-commands.json`
- **Service:** `projects/sma-shared-lib/src/lib/services/voice/voice-command.service.ts`
- **Button:** `projects/sma-shared-lib/src/lib/components/voice-button/`

## Summary

**To add a new voice command:**
1. Open `voice-commands.json`
2. Add `{ "route": "/your/route", "patterns": ["what to say"] }`
3. Save file
4. Refresh browser
5. Test: Click mic, say command

That's all! No complex setup, no multiple files, just simple navigation.
