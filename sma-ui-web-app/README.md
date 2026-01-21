# SMA UI Web App

Angular 15 based monorepo for School Management Application UI applications.

## Structure

```
sma-ui-web-app/
├── projects/
│   ├── sma-shared-lib/          # Shared library (models, services, constants, utils)
│   └── sma-student-ui-app/       # Student management UI application
├── package.json
├── angular.json
└── tsconfig.json
```

## Shared Library (sma-shared-lib)

Contains reusable code across all UI apps:
- **Models**: TypeScript interfaces for Student, Guardian, Enrollment
- **Services**: HTTP services for API communication
- **Constants**: Enums and constant values (GENDER, STUDENT_STATUS, etc.)
- **Utils**: Helper functions (DateUtils, etc.)

## Applications

### sma-student-ui-app (Port 4200)

Student management application with features:
- List all students
- Create new student
- Edit student details
- View student details with tabs for:
  - Basic information
  - Guardians
  - Enrollment history

## Prerequisites

- Node.js 16+ and npm
- Angular CLI 15.x

## Installation

```bash
npm install
```

## Build Shared Library

Before running any application, build the shared library:

```bash
npm run build:shared
```

## Running Applications

### Student UI App

```bash
npm run start:student
```

Access at: http://localhost:4200

### Future Apps

```bash
npm run start:staff      # Port 4201 (to be implemented)
npm run start:admin      # Port 4202 (to be implemented)
```

## Backend API Configuration

The student UI app connects to `sma-student-mngt-app` backend via proxy:

**Proxy Configuration** (`projects/sma-student-ui-app/proxy.conf.json`):
```json
{
  "/api/*": {
    "target": "http://localhost:9091",
    "secure": false,
    "changeOrigin": true
  }
}
```

Ensure the backend service is running on port 9091.

## API Endpoints Used

The application consumes these REST APIs from student-mngt-app:

**Student Management:**
- `POST /api/schools/{schoolId}/students` - Create student
- `GET /api/schools/{schoolId}/students` - List all students
- `GET /api/schools/{schoolId}/students/{studentId}` - Get student details
- `PUT /api/schools/{schoolId}/students/{studentId}` - Update student
- `DELETE /api/schools/{schoolId}/students/{studentId}` - Delete student

**Guardian Management:**
- `POST /api/schools/{schoolId}/students/{studentId}/guardians` - Add guardian
- `GET /api/schools/{schoolId}/students/{studentId}/guardians` - List guardians
- `DELETE /api/schools/{schoolId}/students/{studentId}/guardians/{guardianId}` - Remove guardian

**Enrollment Management:**
- `POST /api/schools/{schoolId}/students/{studentId}/enrollments` - Enroll student
- `GET /api/schools/{schoolId}/students/{studentId}/enrollments` - Get enrollments
- `POST /api/schools/{schoolId}/students/{studentId}/promote` - Promote student
- `POST /api/schools/{schoolId}/students/{studentId}/withdraw` - Withdraw student

## Technology Stack

- **Angular 15.2** - Frontend framework
- **Angular Material 15.2** - UI component library
- **RxJS 7.8** - Reactive programming
- **TypeScript 4.9** - Programming language

## Development Guidelines

### Adding New Models

Add to `projects/sma-shared-lib/src/lib/models/`:
```typescript
export interface NewModel {
  id: string;
  // ... fields
}
```

Export in `models/index.ts`.

### Adding New Services

Add to `projects/sma-shared-lib/src/lib/services/`:
```typescript
@Injectable({ providedIn: 'root' })
export class NewService {
  constructor(private http: HttpClient) {}
  // ... methods
}
```

Export in `services/index.ts`.

### Creating New UI Apps

1. Add project configuration to `angular.json`
2. Create project structure under `projects/`
3. Add scripts to `package.json`
4. Configure proxy for backend API
5. Import `SmaSharedModule` in app module

## Building for Production

```bash
# Build shared library
npm run build:shared

# Build student app
npm run build:student
```

Output will be in `dist/` directory.

## TODO

- [ ] Implement Guardian form component
- [ ] Implement Enrollment form component
- [ ] Add authentication and authorization
- [ ] Implement school selection (currently hardcoded)
- [ ] Add form validation messages
- [ ] Add loading states and error handling
- [ ] Implement staff UI app
- [ ] Implement admin UI app
- [ ] Add unit tests
- [ ] Add e2e tests
- [ ] Add Docker support

## Architecture Notes

This follows the same monorepo structure as `Rentin-ui-app`:
- Shared library pattern for code reuse
- Proxy configuration for API routing
- Material Design for consistent UI
- Reactive forms for data handling
- Service-based architecture for API calls
