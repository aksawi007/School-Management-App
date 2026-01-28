-- =====================================================
-- Fix UUID to BIGINT Type Mismatch
-- Converts all school_id columns from UUID to BIGINT
-- =====================================================

-- Drop existing foreign key constraints (if they exist)
ALTER TABLE sma_admin.academic_year DROP CONSTRAINT IF EXISTS academic_year_school_id_fkey;
ALTER TABLE sma_admin.staff DROP CONSTRAINT IF EXISTS staff_school_id_fkey;
ALTER TABLE sma_admin.class_master DROP CONSTRAINT IF EXISTS class_master_school_id_fkey;
ALTER TABLE sma_admin.section_master DROP CONSTRAINT IF EXISTS section_master_school_id_fkey;
ALTER TABLE sma_admin.subject_master DROP CONSTRAINT IF EXISTS subject_master_school_id_fkey;
ALTER TABLE sma_admin.department_master DROP CONSTRAINT IF EXISTS department_master_school_id_fkey;
ALTER TABLE sma_admin.fee_category DROP CONSTRAINT IF EXISTS fee_category_school_id_fkey;
ALTER TABLE sma_admin.fee_structure DROP CONSTRAINT IF EXISTS fee_structure_school_id_fkey;
ALTER TABLE sma_admin.exam DROP CONSTRAINT IF EXISTS exam_school_id_fkey;
ALTER TABLE sma_admin.timetable DROP CONSTRAINT IF EXISTS timetable_school_id_fkey;
ALTER TABLE sma_admin.transport_route DROP CONSTRAINT IF EXISTS transport_route_school_id_fkey;
ALTER TABLE sma_admin.vehicle DROP CONSTRAINT IF EXISTS vehicle_school_id_fkey;
ALTER TABLE sma_student.student_profile DROP CONSTRAINT IF EXISTS student_profile_school_id_fkey;

-- Alter school_profile.id to BIGINT (if it's UUID)
ALTER TABLE sma_admin.school_profile ALTER COLUMN id TYPE BIGINT USING id::text::bigint;

-- Alter all school_id columns to BIGINT in sma_admin schema
ALTER TABLE sma_admin.academic_year ALTER COLUMN school_id TYPE BIGINT USING school_id::text::bigint;
ALTER TABLE sma_admin.staff ALTER COLUMN school_id TYPE BIGINT USING school_id::text::bigint;

-- Check if these tables exist before altering
DO $$
BEGIN
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'sma_admin' AND table_name = 'class_master') THEN
        ALTER TABLE sma_admin.class_master ALTER COLUMN school_id TYPE BIGINT USING school_id::text::bigint;
    END IF;
    
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'sma_admin' AND table_name = 'section_master') THEN
        ALTER TABLE sma_admin.section_master ALTER COLUMN school_id TYPE BIGINT USING school_id::text::bigint;
    END IF;
    
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'sma_admin' AND table_name = 'subject_master') THEN
        ALTER TABLE sma_admin.subject_master ALTER COLUMN school_id TYPE BIGINT USING school_id::text::bigint;
    END IF;
    
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'sma_admin' AND table_name = 'department_master') THEN
        ALTER TABLE sma_admin.department_master ALTER COLUMN school_id TYPE BIGINT USING school_id::text::bigint;
    END IF;
    
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'sma_admin' AND table_name = 'fee_category') THEN
        ALTER TABLE sma_admin.fee_category ALTER COLUMN school_id TYPE BIGINT USING school_id::text::bigint;
    END IF;
    
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'sma_admin' AND table_name = 'fee_structure') THEN
        ALTER TABLE sma_admin.fee_structure ALTER COLUMN school_id TYPE BIGINT USING school_id::text::bigint;
    END IF;
    
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'sma_admin' AND table_name = 'exam') THEN
        ALTER TABLE sma_admin.exam ALTER COLUMN school_id TYPE BIGINT USING school_id::text::bigint;
    END IF;
    
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'sma_admin' AND table_name = 'timetable') THEN
        ALTER TABLE sma_admin.timetable ALTER COLUMN school_id TYPE BIGINT USING school_id::text::bigint;
    END IF;
    
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'sma_admin' AND table_name = 'transport_route') THEN
        ALTER TABLE sma_admin.transport_route ALTER COLUMN school_id TYPE BIGINT USING school_id::text::bigint;
    END IF;
    
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'sma_admin' AND table_name = 'vehicle') THEN
        ALTER TABLE sma_admin.vehicle ALTER COLUMN school_id TYPE BIGINT USING school_id::text::bigint;
    END IF;
END $$;

-- Alter school_id in sma_student schema
DO $$
BEGIN
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'sma_student' AND table_name = 'student_profile') THEN
        ALTER TABLE sma_student.student_profile ALTER COLUMN school_id TYPE BIGINT USING school_id::text::bigint;
    END IF;
END $$;

-- Recreate foreign key constraints
ALTER TABLE sma_admin.academic_year ADD CONSTRAINT academic_year_school_id_fkey 
    FOREIGN KEY (school_id) REFERENCES sma_admin.school_profile(id) ON DELETE CASCADE;

ALTER TABLE sma_admin.staff ADD CONSTRAINT staff_school_id_fkey 
    FOREIGN KEY (school_id) REFERENCES sma_admin.school_profile(id) ON DELETE CASCADE;

-- Recreate constraints for other tables if they exist
DO $$
BEGIN
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'sma_admin' AND table_name = 'class_master') THEN
        ALTER TABLE sma_admin.class_master ADD CONSTRAINT class_master_school_id_fkey 
            FOREIGN KEY (school_id) REFERENCES sma_admin.school_profile(id) ON DELETE CASCADE;
    END IF;
    
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'sma_admin' AND table_name = 'section_master') THEN
        ALTER TABLE sma_admin.section_master ADD CONSTRAINT section_master_school_id_fkey 
            FOREIGN KEY (school_id) REFERENCES sma_admin.school_profile(id) ON DELETE CASCADE;
    END IF;
    
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'sma_admin' AND table_name = 'subject_master') THEN
        ALTER TABLE sma_admin.subject_master ADD CONSTRAINT subject_master_school_id_fkey 
            FOREIGN KEY (school_id) REFERENCES sma_admin.school_profile(id) ON DELETE CASCADE;
    END IF;
    
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'sma_admin' AND table_name = 'department_master') THEN
        ALTER TABLE sma_admin.department_master ADD CONSTRAINT department_master_school_id_fkey 
            FOREIGN KEY (school_id) REFERENCES sma_admin.school_profile(id) ON DELETE CASCADE;
    END IF;
    
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'sma_admin' AND table_name = 'fee_category') THEN
        ALTER TABLE sma_admin.fee_category ADD CONSTRAINT fee_category_school_id_fkey 
            FOREIGN KEY (school_id) REFERENCES sma_admin.school_profile(id) ON DELETE CASCADE;
    END IF;
    
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'sma_admin' AND table_name = 'fee_structure') THEN
        ALTER TABLE sma_admin.fee_structure ADD CONSTRAINT fee_structure_school_id_fkey 
            FOREIGN KEY (school_id) REFERENCES sma_admin.school_profile(id) ON DELETE CASCADE;
    END IF;
    
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'sma_admin' AND table_name = 'exam') THEN
        ALTER TABLE sma_admin.exam ADD CONSTRAINT exam_school_id_fkey 
            FOREIGN KEY (school_id) REFERENCES sma_admin.school_profile(id) ON DELETE CASCADE;
    END IF;
    
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'sma_admin' AND table_name = 'timetable') THEN
        ALTER TABLE sma_admin.timetable ADD CONSTRAINT timetable_school_id_fkey 
            FOREIGN KEY (school_id) REFERENCES sma_admin.school_profile(id) ON DELETE CASCADE;
    END IF;
    
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'sma_admin' AND table_name = 'transport_route') THEN
        ALTER TABLE sma_admin.transport_route ADD CONSTRAINT transport_route_school_id_fkey 
            FOREIGN KEY (school_id) REFERENCES sma_admin.school_profile(id) ON DELETE CASCADE;
    END IF;
    
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'sma_admin' AND table_name = 'vehicle') THEN
        ALTER TABLE sma_admin.vehicle ADD CONSTRAINT vehicle_school_id_fkey 
            FOREIGN KEY (school_id) REFERENCES sma_admin.school_profile(id) ON DELETE CASCADE;
    END IF;
END $$;

-- Recreate student_profile constraint
DO $$
BEGIN
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'sma_student' AND table_name = 'student_profile') THEN
        ALTER TABLE sma_student.student_profile ADD CONSTRAINT student_profile_school_id_fkey 
            FOREIGN KEY (school_id) REFERENCES sma_admin.school_profile(id) ON DELETE CASCADE;
    END IF;
END $$;

-- Verify the changes
SELECT 
    table_schema,
    table_name, 
    column_name, 
    data_type 
FROM information_schema.columns 
WHERE column_name = 'school_id' 
ORDER BY table_schema, table_name;
