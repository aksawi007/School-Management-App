# Class Routine Management System - Architecture Document

## Overview
A modular, space-efficient system for managing class schedules with master templates, daily tracking, and attendance integration.

## Design Principles
1. **Data Minimization**: Only store changes/deltas from master routine
2. **Modular Structure**: Separate concerns (time slots, master routine, daily sessions, attendance)
3. **Flexibility**: Support substitutions, cancellations, and schedule changes
4. **Complete Tracking**: Capture what happened, who taught, who attended

## Database Schema

### 1. routine_time_slot (Master Time Slots)
**Purpose**: Reusable time periods across all classes
**Key Fields**:
- school_id, slot_name, start_time, end_time
- display_order, slot_type (TEACHING, BREAK, LUNCH, ASSEMBLY)
- is_active

**Example Data**:
```
Period 1: 08:00-08:45 (TEACHING)
Period 2: 08:45-09:30 (TEACHING)
Break:    09:30-09:45 (BREAK)
Period 3: 09:45-10:30 (TEACHING)
```

**Benefits**:
- Create once, use everywhere
- Easy to modify school-wide timings
- Consistent time allocation

---

### 2. class_routine_master (Weekly Template)
**Purpose**: Standard weekly schedule for each class/section
**Key Fields**:
- school_id, academic_year_id, class_id, section_id
- day_of_week (MONDAY-SATURDAY)
- time_slot_id, subject_id, teacher_id
- is_active

**Example Data**:
```
Class 10-A, Monday, Period 1: Mathematics, Teacher: John Doe
Class 10-A, Monday, Period 2: Physics, Teacher: Jane Smith
Class 10-A, Tuesday, Period 1: English, Teacher: Mike Brown
```

**Unique Constraint**: (school_id, academic_year_id, class_id, section_id, day_of_week, time_slot_id)
- Prevents duplicate entries for same slot

**Benefits**:
- Stores weekly template (repeats every week)
- Changed only when base schedule changes
- No daily duplication needed

---

### 3. daily_class_session (Daily Tracking)
**Purpose**: Track actual daily schedule (created only when needed)
**When Created**:
1. Substitute teacher assigned
2. Subject changed for specific day
3. Attendance to be recorded
4. Session cancelled/postponed

**Key Fields**:
- school_id, academic_year_id, class_id, section_id
- session_date, time_slot_id
- **routine_master_id** (reference to master template)
- **subject_override** (only if different from master)
- **teacher_override** (only if different from master)
- actual_teacher_id (who actually conducted)
- session_status (SCHEDULED, CONDUCTED, CANCELLED, POSTPONED)
- is_attendance_marked

**Data Flow**:
```
Normal Day (No Changes):
- No record in daily_class_session
- System refers to class_routine_master
- Zero redundancy

Day with Substitute:
- Create daily_class_session record
- Set routine_master_id (reference to template)
- Set teacher_override (substitute teacher)
- subject_override = NULL (use master)
- Minimal data stored

Day with Subject Change:
- Create daily_class_session record
- Set routine_master_id
- Set subject_override
- teacher_override = NULL or set if different
```

**Benefits**:
- **Space Efficient**: Records created only when needed
- **Flexible**: Supports all override scenarios
- **Traceable**: Links back to master template
- **Status Tracking**: Know if class happened

---

### 4. student_attendance (Per-Session Attendance)
**Purpose**: Record student presence for each class session
**Key Fields**:
- class_session_id (links to daily_class_session)
- student_id
- attendance_status (PRESENT, ABSENT, LATE, EXCUSED, SICK_LEAVE)
- marked_at, marked_by (audit trail)

**Data Flow**:
1. Teacher marks attendance for Period 1
2. System creates daily_class_session (if not exists)
3. Creates student_attendance records for all students
4. Links attendance to specific session

**Benefits**:
- **Granular**: Track attendance per class period
- **Audit Trail**: Who marked, when marked
- **Flexible**: Different statuses supported
- **Linked**: Complete picture of session

---

## Data Efficiency Analysis

### Scenario: 10 Classes, 6 Periods/Day, 5 Days/Week

**Master Routine**:
- Records: 10 classes × 6 periods × 5 days = 300 records
- Stored: Once per academic year
- Changes: Rarely (maybe 5-10 times per year)

**Daily Sessions (Traditional Approach - Store Everything)**:
- Records/Day: 10 × 6 = 60
- Records/Week: 60 × 5 = 300
- Records/Year (40 weeks): 300 × 40 = **12,000 records**

**Daily Sessions (Our Approach - Only Changes)**:
Assuming:
- 5% sessions have substitutions = 3 sessions/day
- Attendance marked for all sessions = 60 sessions/day

- Records/Day: 60 (for attendance tracking)
- Records/Week: 60 × 5 = 300
- Records/Year: 300 × 40 = 12,000
- **BUT**: These records are lean (mostly just session_date + routine_master_id)
- **Subject/Teacher overrides**: Only ~3/day = 120/year

**Attendance Data**:
- Students per class: 30
- Records/Day: 60 sessions × 30 students = 1,800
- Records/Year: 1,800 × 200 days = 360,000

**Total Storage**:
- Master Routine: 300 (stable, rarely changes)
- Daily Sessions: 12,000 (lightweight, mostly references)
- Attendance: 360,000 (necessary for tracking)

---

## Query Patterns

### Get Today's Schedule for Class 10-A
```sql
-- Step 1: Check daily_class_session
SELECT * FROM daily_class_session 
WHERE class_id = 10A AND session_date = TODAY

-- Step 2: For missing slots, use master routine
SELECT * FROM class_routine_master
WHERE class_id = 10A AND day_of_week = 'MONDAY'
  AND time_slot_id NOT IN (SELECT time_slot_id FROM daily_class_session WHERE...)
```

### Get Teacher's Schedule for Today
```sql
-- From master routine (default)
SELECT * FROM class_routine_master crm
JOIN routine_time_slot rts ON crm.time_slot_id = rts.id
WHERE crm.teacher_id = :teacherId 
  AND crm.day_of_week = :today
  
-- Override with daily sessions
UNION

SELECT * FROM daily_class_session dcs
WHERE dcs.session_date = :date
  AND (dcs.teacher_override = :teacherId OR dcs.actual_teacher_id = :teacherId)
```

### Get Attendance Report
```sql
SELECT 
  dcs.session_date,
  dcs.class_id,
  dcs.section_id,
  COALESCE(dcs.subject_override, crm.subject_id) as subject,
  COALESCE(dcs.teacher_override, crm.teacher_id) as teacher,
  sa.student_id,
  sa.attendance_status
FROM daily_class_session dcs
LEFT JOIN class_routine_master crm ON dcs.routine_master_id = crm.id
JOIN student_attendance sa ON sa.class_session_id = dcs.id
WHERE dcs.session_date BETWEEN :startDate AND :endDate
```

---

## API Design

### Time Slot Management
- `POST /api/routine/time-slots` - Create time slot
- `GET /api/routine/time-slots?schoolId=X` - Get all slots
- `PUT /api/routine/time-slots/{id}` - Update slot
- `DELETE /api/routine/time-slots/{id}` - Delete slot

### Master Routine Management
- `POST /api/routine/master` - Create/Update master routine
- `GET /api/routine/master?classId=X&sectionId=Y&academicYearId=Z` - Get weekly template
- `GET /api/routine/master/day?classId=X&day=MONDAY` - Get schedule for specific day
- `PUT /api/routine/master/{id}` - Update routine entry
- `DELETE /api/routine/master/{id}` - Delete routine entry

### Daily Session Management
- `POST /api/routine/daily-session` - Create session (for override or attendance)
- `GET /api/routine/daily-session?date=2026-01-30&classId=X` - Get day's schedule
- `PUT /api/routine/daily-session/{id}` - Update session (substitute, cancellation)
- `GET /api/routine/daily-session/teacher?teacherId=X&date=2026-01-30` - Teacher's schedule

### Attendance Management
- `POST /api/routine/attendance` - Mark attendance for session
- `POST /api/routine/attendance/bulk` - Bulk mark attendance for class
- `GET /api/routine/attendance?sessionId=X` - Get session attendance
- `GET /api/routine/attendance/student?studentId=X&startDate=...&endDate=...` - Student attendance history
- `GET /api/routine/attendance/report?classId=X&date=...` - Daily attendance report

---

## Frontend Components

### 1. Time Slot Management
- List/Create/Edit time slots
- Visual timeline view

### 2. Master Routine Builder
- Weekly grid view (Days × Time Slots)
- Drag-drop subject/teacher assignment
- Bulk copy (copy Monday to Tuesday, etc.)

### 3. Daily Schedule View
- Today's schedule for class
- Override capability (substitute teacher)
- Session status update
- Quick attendance marking

### 4. Teacher Dashboard
- My schedule for today/week
- Attendance marking interface
- Substitution alerts

### 5. Attendance Reports
- Daily attendance summary
- Student attendance history
- Defaulter list
- Export to Excel

---

## Implementation Benefits

### 1. Space Efficiency
- Master routine: 300 records (for 10 classes)
- Daily sessions: Only created when needed
- Estimated 90% reduction vs storing full daily schedule

### 2. Flexibility
- Easy substitutions (just override teacher)
- Subject changes (override subject)
- Cancellations (mark status)
- No impact on master template

### 3. Data Integrity
- Master routine: Single source of truth
- Daily sessions: Exception handling
- Attendance: Linked to actual sessions

### 4. Query Performance
- Indexed on common query patterns
- Minimal joins for daily schedule
- Efficient date-range queries

### 5. Maintenance
- Update master once, affects all future weeks
- Historical data preserved in daily sessions
- Audit trail for all changes

---

## Migration Strategy

### Phase 1: Time Slots
- Create time slot master
- Populate with school timings

### Phase 2: Master Routine
- Build weekly template for each class/section
- Teacher assignment
- Subject allocation

### Phase 3: Daily Tracking
- Start recording daily sessions
- Enable substitutions
- Track session status

### Phase 4: Attendance Integration
- Link attendance to sessions
- Teacher attendance marking interface
- Reports and dashboards

---

## Future Enhancements

1. **Smart Scheduling**: Auto-suggest optimal routine based on teacher availability
2. **Conflict Detection**: Alert when teacher assigned to multiple classes same time
3. **Load Balancing**: Distribute teacher workload evenly
4. **Notifications**: Alert teachers about substitutions
5. **Analytics**: Teacher utilization, subject distribution, attendance trends
6. **Mobile App**: Teacher app for quick attendance marking
7. **Integration**: Link with exam schedule, homework, etc.
