# SMA Student Management Service

## Overview
Microservice responsible for complete student lifecycle management in the School Management Application.

## Scope & Responsibilities

### Owns:
- **Student Profile**: Identity, demographics, medical info
- **Guardians/Parents**: Contact information, relations
- **Addresses**: Current and permanent addresses
- **Admissions**: Student admission process and admission number generation
- **Enrollments**: Class/section assignment with complete history
- **Promotions**: Year-to-year class advancement
- **Transfers & Withdrawals**: Student lifecycle transitions
- **Documents Metadata**: References to student documents (photos, certificates, etc.)

### Does NOT Own:
- Fees & Payments (handled by Accounts Service)
- Attendance (future Attendance Service)
- Exams & Results (future Exam Service)
- Transport routing (Transport Service)

## Architecture

### Layers
```
API Layer (REST Controllers)
    ↓
Application Layer (Services/Use Cases)
    ↓
Domain Layer (Entities & Business Rules)
    ↓
Persistence Layer (Repositories)
```

### Technology Stack
- **Framework**: Spring Boot 2.5.2
- **Database**: PostgreSQL with schema `sma_student`
- **ORM**: Hibernate/JPA
- **API Documentation**: Swagger/OpenAPI
- **Service Discovery**: Eureka Client
- **Messaging**: RabbitMQ (AMQP)

## Data Model

### Core Entities

#### 1. Student
- **Purpose**: Core student profile
- **Key Fields**: admission_no, name, dob, gender, status, school_id
- **Status Values**: APPLIED, ACTIVE, INACTIVE, TRANSFERRED, ALUMNI, WITHDRAWN
- **Unique Constraint**: (school_id, admission_no)

#### 2. Guardian
- **Purpose**: Parent/guardian information
- **Key Fields**: relation, name, phone, email, is_primary
- **Relations**: FATHER, MOTHER, GUARDIAN, OTHER

#### 3. Address
- **Purpose**: Student addresses
- **Types**: CURRENT, PERMANENT
- **Key Fields**: line1, line2, city, state, pincode, country

#### 4. Enrollment (History-based)
- **Purpose**: Student class/section assignment with timeline
- **Key Fields**: academic_year_id, class_id, section_id, roll_no, start_date, end_date
- **Status**: ACTIVE, ENDED
- **End Reasons**: PROMOTION, TRANSFER, WITHDRAWAL, COMPLETION
- **Rule**: Only ONE ACTIVE enrollment per student per academic year

#### 5. Document
- **Purpose**: Metadata for student documents
- **Types**: PHOTO, AADHAR, BIRTH_CERTIFICATE, TC, MARKSHEET, MEDICAL, OTHER
- **Storage**: References file storage service/blob storage

## Multi-Tenancy
- **All tables include `school_id`**
- **Unique constraints scoped by school_id**
- **API endpoints require schoolId path parameter**

## API Endpoints

### Student Management
```
POST   /api/schools/{schoolId}/students                    - Create student (admission)
GET    /api/schools/{schoolId}/students/{studentId}        - Get student details
GET    /api/schools/{schoolId}/students                    - Search/List students
PATCH  /api/schools/{schoolId}/students/{studentId}/status - Update status
```

### Enrollment
```
POST   /api/schools/{schoolId}/students/{studentId}/enrollments   - Enroll student
GET    /api/schools/{schoolId}/students/{studentId}/enrollments   - Enrollment history
POST   /api/schools/{schoolId}/students/{studentId}/promote       - Promote student
POST   /api/schools/{schoolId}/students/{studentId}/withdraw      - Withdraw student
```

### Guardian Management
```
GET    /api/schools/{schoolId}/students/{studentId}/guardians              - List guardians
POST   /api/schools/{schoolId}/students/{studentId}/guardians              - Add guardian
DELETE /api/schools/{schoolId}/students/{studentId}/guardians/{guardianId} - Remove guardian
```

## Key Features

### 1. Admission Number Generation
- Auto-generated if not provided
- Format: `ADM{YEAR}{SEQUENCE}` (e.g., ADM2026000001)
- Can be customized per school requirements

### 2. Enrollment History
- Complete timeline of student's class/section assignments
- Never deleted - soft delete only
- Track promotions, transfers, withdrawals with reasons

### 3. Search & Filtering
- Search by name, admission number
- Filter by status, class, section, academic year
- Pagination and sorting support

### 4. Audit Trail
- Every entity includes: created_at, created_by, updated_at, updated_by
- Soft delete support (is_deleted flag)
- Active/Inactive status tracking

### 5. Student Details Aggregation
- Single endpoint returns:
  - Student profile
  - All guardians
  - All addresses
  - Current enrollment
  - Complete enrollment history

## Database Schema

### Indexes (Performance Optimization)
```sql
-- Student
CREATE INDEX idx_student_school_admission ON student(school_id, admission_no);
CREATE INDEX idx_student_school_status ON student(school_id, status);
CREATE INDEX idx_student_name ON student(first_name, last_name);

-- Guardian
CREATE INDEX idx_guardian_student ON guardian(student_id);
CREATE INDEX idx_guardian_phone ON guardian(phone);

-- Enrollment
CREATE INDEX idx_enrollment_student ON enrollment(student_id);
CREATE INDEX idx_enrollment_school_year_class ON enrollment(school_id, academic_year_id, class_id, section_id);
CREATE INDEX idx_enrollment_status ON enrollment(status);

-- Document
CREATE INDEX idx_document_student ON document(student_id);
CREATE INDEX idx_document_type ON document(doc_type);
```

## Configuration

### Application Port
- **Default**: 9091
- **Configure via**: `server.port`

### Database
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${PG_ADDRESS}:${PG_PORT}/${DB_NAME}
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        hbm2ddl:
          create_namespaces: true  # Auto-creates sma_student schema
```

### Environment Variables
```
PG_ADDRESS          - PostgreSQL host
PG_PORT             - PostgreSQL port (default: 5432)
DB_NAME             - Database name
PG_ADDRESS_USERID   - Database username
PG_ADDRESS_PASSWORD - Database password
EUREKA_SERVER_URL   - Eureka server URL
AMQP_ADDRESS        - RabbitMQ host
AMQP_PORT           - RabbitMQ port
AMQP_USERID         - RabbitMQ username
AMQP_PASSWORD       - RabbitMQ password
```

## Running the Service

### Build
```bash
cd sma-student-mngt-app
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

### Access Swagger
```
http://localhost:9091/swagger-ui.html
```

## Future Enhancements

### Phase 2
- [ ] Document upload/download APIs
- [ ] Bulk student import (CSV/Excel)
- [ ] Student transfer between schools
- [ ] Custom admission number formats
- [ ] Student tags (scholarship, special needs, etc.)
- [ ] Student notes/remarks history

### Phase 3
- [ ] Event publishing (StudentCreated, EnrollmentChanged)
- [ ] Integration with File Storage Service
- [ ] Advanced search (full-text, fuzzy matching)
- [ ] Export functionality (PDF, Excel)
- [ ] Duplicate detection (name + DOB + guardian phone)
- [ ] Idempotency support for admission creation

### Phase 4
- [ ] Sibling linking
- [ ] Student photo management
- [ ] TC (Transfer Certificate) generation
- [ ] Student ID card generation
- [ ] Analytics & reporting

## Security

### Current
- JWT-based authentication (configured, not enforced on Swagger)
- Multi-tenant data isolation by school_id

### Planned
- Role-based access control
  - Admin: Full access
  - Teacher: Read-only access
  - Parent: Own child access only
- Audit logging for sensitive operations
- Data encryption at rest

## Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

## Dependencies

### Internal
- `sma-core-service` - Core utilities, security config
- `sma-security-auth` - JWT authentication
- `sma-api-platform-amqp-service` - AMQP messaging

### External
- Spring Boot Data JPA
- PostgreSQL Driver
- Eureka Client
- Spring Boot Validation
- Swagger

## Troubleshooting

### Common Issues

**Issue**: Tables not created
- **Solution**: Check `spring.jpa.hibernate.ddl-auto` is set to `update`
- **Solution**: Verify `hibernate.hbm2ddl.create_namespaces: true` is set

**Issue**: Student search slow
- **Solution**: Ensure indexes are created
- **Solution**: Use pagination with reasonable page size

**Issue**: Duplicate admission numbers
- **Solution**: Check unique constraint is in place
- **Solution**: Verify admission number generation logic

## Contact & Support
For issues or questions, contact the SMA platform team.
