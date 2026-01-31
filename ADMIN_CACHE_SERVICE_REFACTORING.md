# Admin Cache Service Refactoring

## Overview
Renamed `AcademicYearCacheService` to `AdminCacheService` and enhanced it to centralize all session and cache management for the admin application.

## Changes Made

### 1. New Service: AdminCacheService
**Location**: `projects/sma-admin-ui-app/src/app/services/admin-cache.service.ts`

**Key Features**:
- **School Context Management**: Listens to `postMessage` events from parent shell application (localhost:4300)
- **SchoolDetails Interface**: Contains `schoolId`, `schoolName`, `schoolCode`
- **Reactive School Context**: Exposes `schoolDetails$` BehaviorSubject for components to subscribe
- **Academic Year Caching**: Preserves all original `AcademicYearCacheService` functionality
- **Auto-loading**: Automatically loads academic years when school context is received
- **Helper Methods**: 
  - `getSchoolId()` - Returns schoolId or 0 if not loaded
  - `getSchoolDetails()` - Returns observable of school details
  - `getSchoolDetailsSync()` - Returns school details synchronously
  - `initializeSchoolContext()` - Sets up event listener for parent messages
  - `isSchoolContextLoaded()` - Checks if school context is available
  - `getAcademicYears()` - Returns cached or fetches academic years
  - `getCurrentAcademicYear()` - Returns the current academic year
  - `clearCache()` - Clears all cached data

### 2. Components Updated

#### Core Components (Previously using AcademicYearCacheService):
1. **app.component.ts** - Root component now initializes school context
2. **student-class-management.component.ts** - Removed manual event listener
3. **class-list.component.ts** - Updated to use AdminCacheService
4. **class-form.component.ts** - Updated to use AdminCacheService
5. **section-list.component.ts** - Updated to use AdminCacheService
6. **section-form.component.ts** - Updated to use AdminCacheService

#### Routine Management Components (Previously using hardcoded schoolId):
7. **time-slot-management.component.ts** - Now uses `adminCache.getSchoolId()`
8. **routine-builder.component.ts** - Removed manual event listener, uses AdminCacheService
9. **daily-schedule-view.component.ts** - Now uses `adminCache.getSchoolId()`
10. **attendance-marking.component.ts** - Now uses `adminCache.getSchoolId()`

### 3. Pattern Changes

#### Before:
```typescript
// Manual event listener in each component
ngOnInit(): void {
  window.addEventListener('message', (event) => {
    if (event.origin !== 'http://localhost:4300') return;
    if (event.data && event.data.type === 'SCHOOL_CONTEXT') {
      const school = event.data.school;
      if (school) {
        this.schoolId = school.schoolId;
        this.loadData();
      }
    }
  });
  
  if (window.parent && window.parent !== window) {
    window.parent.postMessage({ type: 'REQUEST_CONTEXT' }, 'http://localhost:4300');
  }
}

// OR hardcoded
schoolId = 1; // TODO: Get from context/session
```

#### After:
```typescript
constructor(private adminCache: AdminCacheService) {
  this.schoolId = this.adminCache.getSchoolId();
}

ngOnInit(): void {
  this.adminCache.initializeSchoolContext();
  
  this.adminCache.getSchoolDetails().subscribe(school => {
    if (school) {
      this.schoolId = school.schoolId;
      this.loadData();
    }
  });
}
```

### 4. Benefits

1. **DRY Principle**: Single source of truth for school context
2. **Maintainability**: Changes to context management only need to be made in one place
3. **Testability**: Easier to mock the service for unit tests
4. **Consistency**: All components access school context the same way
5. **Type Safety**: SchoolDetails interface ensures type safety
6. **Reactive**: Components automatically update when school context changes
7. **Centralized**: All admin app session data in one service

### 5. Communication Flow

```
Parent Shell (localhost:4300)
    │
    ├─► postMessage({ type: 'SCHOOL_CONTEXT', school: {...} })
    │
    ▼
AdminCacheService
    │
    ├─► Receives message
    ├─► Validates origin
    ├─► Stores in schoolDetails$ BehaviorSubject
    ├─► Auto-loads academic years
    │
    ▼
Components
    │
    ├─► Subscribe to getSchoolDetails()
    ├─► Call getSchoolId() for immediate access
    └─► Subscribe to getAcademicYears()
```

### 6. Files Deleted
- `projects/sma-admin-ui-app/src/app/services/academic-year-cache.service.ts`

### 7. Build Status
✅ Shared library built successfully
✅ Admin UI app built successfully with 0 errors
⚠️ 1 warning about unused admin-routes.ts (not related to this refactoring)

## Testing Recommendations

1. **School Context Flow**: Verify parent shell sends SCHOOL_CONTEXT message correctly
2. **Component Initialization**: Test that all components receive schoolId properly
3. **Academic Year Loading**: Ensure academic years load after school context is available
4. **Error Handling**: Test behavior when school context is not available
5. **Cache Invalidation**: Test clearCache() method functionality

## Next Steps

1. Remove hardcoded origin check (`http://localhost:4300`) and make it configurable
2. Add retry logic for failed academic year loading
3. Consider adding other cached data (terms, grades, etc.) to AdminCacheService
4. Add unit tests for AdminCacheService
5. Document the postMessage contract between shell and admin apps
