-- Drop all dependent tables first (in reverse order of dependencies)
DROP TABLE IF EXISTS sma_admin.student_transport CASCADE;
DROP TABLE IF EXISTS sma_admin.vehicle CASCADE;
DROP TABLE IF EXISTS sma_admin.transport_stop CASCADE;
DROP TABLE IF EXISTS sma_admin.transport_route CASCADE;
DROP TABLE IF EXISTS sma_admin.exam_schedule CASCADE;
DROP TABLE IF EXISTS sma_admin.exam CASCADE;
DROP TABLE IF EXISTS sma_admin.timetable CASCADE;
DROP TABLE IF EXISTS sma_admin.staff_subject_assignment CASCADE;
DROP TABLE IF EXISTS sma_admin.staff CASCADE;
DROP TABLE IF EXISTS sma_admin.student_enrollment CASCADE;
DROP TABLE IF EXISTS sma_admin.student CASCADE;
DROP TABLE IF EXISTS sma_admin.department_master CASCADE;
DROP TABLE IF EXISTS sma_admin.subject_master CASCADE;
DROP TABLE IF EXISTS sma_admin.section_master CASCADE;
DROP TABLE IF EXISTS sma_admin.class_master CASCADE;
DROP TABLE IF EXISTS sma_admin.academic_year CASCADE;
DROP TABLE IF EXISTS sma_admin.school_profile CASCADE;

-- Now run the schema.sql to recreate all tables
-- Or you can include the CREATE statements here
