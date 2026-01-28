# Class-AcademicYear Relationship Implementation

## Overview
Successfully linked the ClassMaster entity to AcademicYear, ensuring that classes are associated with specific academic years.

## Changes Made

### 1. Backend - JPA Entity

**File: `ClassMaster.java`**
- Added `@ManyToOne` relationship to `AcademicYear`
- Updated unique constraint to include `academic_year_id`: `(school_id, academic_year_id, class_code)`
- Added getter/setter for `academicYear`

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "academic_year_id", nullable = false)
private AcademicYear academicYear;
```

### 2. Backend - DTOs

**ClassMasterRequest.java**
- Added `academicYearId` field (Long, required)

**ClassMasterResponse.java**
- Added `academicYearId` field (Long)
- Added `academicYearName` field (String) for display purposes

### 3. Backend - Business Service

**ClassMasterBusinessService.java**
- Injected `AcademicYearRepository`
- Updated `createClass()`:
  - Added validation for `academicYearId`
  - Fetches `AcademicYear` entity
  - Sets academic year in `ClassMaster` entity
- Updated `updateClass()`:
  - Allows updating academic year if provided
- Updated `convertToResponse()`:
  - Maps `academicYearId` from entity
  - Maps `academicYearName` for UI display

### 4. Frontend - TypeScript Models

**class.model.ts**
- Added `academicYearId: number` (required)
- Added `academicYearName?: string` (optional, for display)
- Updated both `ClassMaster` and `ClassMasterRequest` interfaces

### 5. Frontend - UI Components

**ClassListComponent**
- Updated `displayedColumns` to include `'academicYearName'`
- Added "Academic Year" column to the table
- Displays academic year name or '-' if not available

**ClassFormComponent**
- Imported `AcademicYearService` and `AcademicYearResponse`
- Added `academicYears: AcademicYearResponse[]` array
- Added `loadAcademicYears()` method to fetch all academic years
- Updated form to include `academicYearId` with `Validators.required`
- Added academic year dropdown in template (first field)
- Loads academic years on component initialization
- Pre-selects academic year when editing existing class

### 6. Database Migration

**File: `add_academic_year_to_class_master.sql`**
- Adds `academic_year_id` column (BIGINT)
- Creates foreign key constraint to `academic_year` table
- Updates unique constraint to `(school_id, academic_year_id, class_code)`
- Creates index on `academic_year_id` for performance
- Column is initially nullable for migration compatibility

## User Experience

### Creating a New Class
1. User navigates to "Add New Class"
2. **First field**: Academic Year dropdown (required) - shows all available academic years
3. User fills in class code, name, display order, description
4. System validates that academic year is selected
5. Class is created and linked to selected academic year

### Viewing Classes
- Class list table displays:
  - Class Code
  - Class Name
  - **Academic Year** (new column)
  - Display Order
  - Description
  - Actions

### Editing a Class
- Academic year dropdown is pre-populated with current value
- User can change academic year if needed
- System validates the new academic year exists

## Business Rules

1. **Required Relationship**: Every class MUST be associated with an academic year
2. **Unique Classes**: Class code must be unique within a school and academic year
   - Same class code can exist across different academic years
   - Example: "Grade 10" can exist in "2024-25" and "2025-26"
3. **Data Integrity**: Foreign key ensures referenced academic year exists
4. **Historical Data**: Classes remain linked to their academic year even after year ends

## Migration Strategy

### For Existing Data:
1. Run the SQL migration script to add the column (nullable)
2. Manually assign academic years to existing classes:
   ```sql
   -- Example: Assign all existing classes to current academic year
   UPDATE sma_admin.class_master
   SET academic_year_id = (
     SELECT id FROM sma_admin.academic_year 
     WHERE is_current = true 
     LIMIT 1
   )
   WHERE academic_year_id IS NULL;
   ```
3. After migration, make column NOT NULL:
   ```sql
   ALTER TABLE sma_admin.class_master
   ALTER COLUMN academic_year_id SET NOT NULL;
   ```

## API Changes

### Create Class Endpoint
**Request Body:**
```json
{
  "schoolId": 1,
  "academicYearId": 5,  // NEW - Required
  "classCode": "10",
  "className": "Class 10",
  "displayOrder": 10,
  "description": "Tenth Grade"
}
```

### Response:
```json
{
  "id": 123,
  "schoolId": 1,
  "academicYearId": 5,       // NEW
  "academicYearName": "2024-25",  // NEW
  "classCode": "10",
  "className": "Class 10",
  "displayOrder": 10,
  "description": "Tenth Grade"
}
```

## Benefits

1. **Historical Tracking**: Maintains class structure across academic years
2. **Multi-Year Support**: Schools can set up classes for future academic years
3. **Data Organization**: Clear separation of classes by academic year
4. **Reporting**: Easy filtering and reporting by academic year
5. **Academic Planning**: Allows advance planning for upcoming years

## Testing Checklist

- [ ] Run database migration script
- [ ] Create a new class with academic year selection
- [ ] Verify academic year is required (form validation)
- [ ] View class list and verify academic year column displays
- [ ] Edit a class and change its academic year
- [ ] Verify unique constraint works (same class code in same year fails)
- [ ] Verify same class code in different years succeeds
- [ ] Test with multiple academic years
- [ ] Verify sections and subjects still work with updated class structure

## Dependencies

- AcademicYear entity and table must exist
- At least one academic year must be created before adding classes
- AcademicYearService already available in sma-shared-lib

## Future Enhancements

1. **Default Selection**: Auto-select current academic year in dropdown
2. **Filter by Year**: Add academic year filter to class list page
3. **Bulk Copy**: Copy classes from one academic year to another
4. **Year Transition**: Wizard to transition all master data to new academic year
5. **Read-Only Past Years**: Prevent modification of classes in completed academic years

## Related Files Modified

### Backend (Java)
- `ClassMaster.java`
- `ClassMasterRequest.java`
- `ClassMasterResponse.java`
- `ClassMasterBusinessService.java`

### Frontend (TypeScript/HTML)
- `class.model.ts`
- `class-list.component.ts`
- `class-list.component.html`
- `class-form.component.ts`
- `class-form.component.html`

### Database
- `add_academic_year_to_class_master.sql`

## Status
✅ **COMPLETE** - Class entity successfully linked to Academic Year with full CRUD support.
