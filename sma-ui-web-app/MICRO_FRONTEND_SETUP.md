# Micro Frontend Architecture - SMA UI Web App

## Overview
The SMA UI Web App has been restructured as a **Micro Frontend Architecture** using Angular Module Federation. This allows independent development, deployment, and scaling of different modules.

## Architecture

### Shell Application (Host)
- **sma-admin-ui-app** (Port 4202)
  - Acts as the main container/shell
  - Hosts the dashboard with module tiles
  - Loads remote micro frontends on demand
  - Manages school context across all modules

### Remote Modules
- **sma-student-ui-app** (Port 4200)
  - Student management micro frontend
  - Loaded dynamically by admin shell
  
- **sma-staff-ui-app** (Port 4201)
  - Staff management micro frontend
  - Loaded dynamically by admin shell

## School Context Sharing

The `SchoolContextService` in `sma-shared-lib` provides a centralized way to share school information across all micro frontends:

```typescript
// Set school context in admin shell
schoolContextService.setSchoolContext({
  schoolId: '123',
  schoolName: 'ABC School',
  schoolCode: 'ABC001'
});

// Get school context in any remote module
const context = schoolContextService.getSchoolContext();
```

## Running the Applications

### Start All Applications:

1. **Start Shared Library Build (watch mode)**:
   ```bash
   npm run build:shared -- --watch
   ```

2. **Start Student UI (Remote)**:
   ```bash
   npm run start:student
   # Runs on http://localhost:4200
   ```

3. **Start Staff UI (Remote)**:
   ```bash
   npm run start:staff
   # Runs on http://localhost:4201
   ```

4. **Start Admin UI (Shell/Host)**:
   ```bash
   npm run start:admin
   # Runs on http://localhost:4202
   ```

### Access the Application:
Open browser at **http://localhost:4202** (Admin Shell)

## Dashboard Features

The admin dashboard displays:
- **Statistics Cards**: Total students, staff, courses, pending enrollments
- **Module Tiles**: Clickable tiles for each micro frontend
  - Student Management Module
  - Staff Management Module
- **Administrative Setup**: School Profile and Academic Year management

## Development Workflow

### Independent Development:
Each micro frontend can be developed independently:
- Student team works on port 4200
- Staff team works on port 4201
- Admin team works on port 4202

### Integration:
All modules integrate seamlessly through the admin shell at runtime.

### Context Sharing:
School ID and context are automatically shared via:
- `SchoolContextService` (in-memory)
- `sessionStorage` (for persistence)

## Technical Details

### Module Federation Configuration:

**Admin UI (webpack.config.js)**:
```javascript
remotes: {
  "smaStudentUiApp": "http://localhost:4200/remoteEntry.js",
  "smaStaffUiApp": "http://localhost:4201/remoteEntry.js",
}
```

**Student/Staff UI (webpack.config.js)**:
```javascript
exposes: {
  './Module': './projects/sma-xxx-ui-app/src/app/app.module.ts',
}
```

## Benefits

1. **Independent Deployment**: Each module can be deployed separately
2. **Team Autonomy**: Different teams can work on different modules
3. **Technology Flexibility**: Each module can use different versions (with care)
4. **Lazy Loading**: Modules load only when accessed
5. **Scalability**: Easy to add new modules
6. **Context Sharing**: Centralized school context management
