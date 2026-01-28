# School Management System - Shell App Architecture

## Overview
The main shell application now implements a school selection workflow with proper routing and module federation for loading micro frontends.

## Application Flow

### 1. School Selection (localhost:4300/)
- Initial screen where users select their school
- Fetches list of schools from `/api/school/profile/getAll`
- Selected school is stored in `AppContextService` and persisted in sessionStorage
- After selection, navigates to dashboard

### 2. Dashboard (localhost:4300/dashboard)
- Shows tiles for three main modules:
  - **Admin Portal** - `/admin` route (port 4202)
  - **Student Management** - `/student` route (port 4200)
  - **Staff Management** - `/staff` route (port 4201)
- Displays selected school information in header
- Option to change school

### 3. Module Routes
- **localhost:4300/admin** - Loads admin micro frontend
- **localhost:4300/student** - Loads student micro frontend
- **localhost:4300/staff** - Loads staff micro frontend

## Shared Context

### AppContextService
Located in `sma-shared-lib`, provides shared state across all micro frontends:

**School Context:**
```typescript
{
  schoolId: number;
  schoolName: string;
  schoolCode: string;
}
```

**User Context:**
```typescript
{
  userId?: number;
  username?: string;
  role?: string;
  email?: string;
}
```

**Usage in Micro Frontends:**
```typescript
import { AppContextService, SchoolContext } from 'sma-shared-lib';

constructor(private appContext: AppContextService) {}

ngOnInit() {
  // Get selected school
  this.appContext.selectedSchool$.subscribe(school => {
    console.log('Current school:', school);
  });

  // Get user context
  this.appContext.userContext$.subscribe(user => {
    console.log('Current user:', user);
  });
}
```

## Running the Application

### Start All Applications
```bash
# Terminal 1 - Main Shell (Required)
npm run start:main

# Terminal 2 - Admin Module
npm run start:admin

# Terminal 3 - Student Module
npm run start:student

# Terminal 4 - Staff Module
npm run start:staff
```

### Start Individual Apps
```bash
# Main shell with proxy
ng serve sma-ui-app --port 4300 --proxy-config projects/sma-ui-app/proxy.conf.json

# Admin module
ng serve sma-admin-ui-app --port 4202

# Student module
ng serve sma-student-ui-app --port 4200

# Staff module
ng serve sma-staff-ui-app --port 4201
```

## Module Federation

The shell app uses Module Federation to load micro frontends dynamically:

- Each micro frontend exposes its module via `remoteEntry.js`
- Shell app loads them on-demand when navigating to routes
- If a module fails to load, shows fallback component

## Backend Integration

### API Proxy
Main shell app proxies API calls to backend:
- `/api/*` → `http://localhost:9090`
- Path rewrite removes `/api` prefix

### Required Backend Services
1. **admin-core-app** (port 9090)
   - School profile management
   - Academic year management
   - System configuration

2. **student-mngt-app**
   - Student enrollment
   - Student profiles
   - Guardian management

3. **staff-mngt-app**
   - Staff profiles
   - Department management
   - Staff assignments

## Features

### Security (To be implemented)
- Authentication integration
- Role-based access control
- JWT token management
- Route guards

### Context Sharing
- School selection persisted across page refreshes
- Context available to all micro frontends
- Centralized state management

### User Experience
- Clean school selection interface
- Dashboard with module tiles
- Breadcrumb navigation
- User profile in header
- Ability to switch schools

## Next Steps

1. **Authentication**
   - Integrate with authentication service
   - Implement login/logout
   - Add route guards

2. **Module Federation Enhancement**
   - Add loading indicators
   - Better error handling
   - Module health checks

3. **Context Enhancement**
   - Add more context properties
   - Event bus for inter-module communication
   - State synchronization

4. **UI/UX Improvements**
   - Breadcrumb navigation
   - Module-specific headers
   - Theme customization per school
