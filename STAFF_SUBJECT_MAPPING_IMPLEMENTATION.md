# Staff-Subject Mapping Implementation

## Database Changes

### 1. New Table: `staff_subject_mapping`
```sql
- Maps teachers to subjects they're qualified to teach
- Fields: staff_id, subject_id, proficiency_level, grade_level_flags
- Use in Routine Builder to show only qualified teachers
```

### 2. Updated Table: `subject_master`
```sql
- Added: department_id (optional FK to department_master)
- Links subjects to departments for organization
```

## Entity Changes

### Created:
- **StaffSubjectMapping.java** - Maps staff → subject qualifications

### Updated:
- **SubjectMaster.java** - Added department reference

## Repository Created:
- **StaffSubjectMappingRepository.java** 
  - `findQualifiedTeachersForSubject()` - Get all teachers for a subject
  - `findQualifiedTeachersForClassSubject()` - Get teachers for class+subject combo
  - `isStaffQualifiedForSubject()` - Check if staff can teach subject

## Next Steps (TODO):

### Backend:
1. Create `StaffSubjectMappingBusinessService`
2. Create `StaffSubjectMappingController` with endpoints:
   - POST `/schools/{schoolId}/staff-subjects` - Assign subject to teacher
   - GET `/schools/{schoolId}/staff-subjects/staff/{staffId}` - Get teacher's subjects
   - GET `/schools/{schoolId}/staff-subjects/qualified?subjectId={id}&classId={id}` - Get qualified teachers
   - DELETE `/schools/{schoolId}/staff-subjects/{id}` - Remove qualification

### Frontend (sma-shared-lib):
1. Create `StaffSubjectMappingService`
2. Add models: `StaffSubjectMappingRequest`, `StaffSubjectMappingResponse`

### Frontend (sma-admin-ui-app):
1. Create **Staff Qualification Management** UI:
   - List teachers with their subjects
   - Add/remove subject qualifications
   - Set proficiency levels

2. Update **RoutineEntryDialog**:
   - Instead of: `getAllStaffBySchool()`
   - Call: `getQualifiedTeachersForSubject(schoolId, classId, subjectId)`
   - Shows only qualified teachers in dropdown

## Usage Flow:

### Setup Phase (One-time):
1. Admin creates subjects with optional department
2. Admin assigns subjects to teachers in "Staff Qualifications" screen

### Routine Building:
1. User selects Class, Section, Day, Time Slot
2. User picks Subject
3. Dialog loads ONLY teachers qualified for that subject+class
4. User picks from qualified teachers only

## Migration:
- Run SQL script: `V1.5__add_staff_subject_mapping.sql`
- Rebuild `sma-jpa-postgresql`: `mvn clean install`

## Benefits:
✅ No more showing unqualified teachers in routine builder
✅ Department-based subject organization
✅ Teacher proficiency tracking (Expert vs Qualified)
✅ Grade-level restrictions (Primary/Secondary/Higher Secondary)
✅ Easy to find substitute teachers for a subject
