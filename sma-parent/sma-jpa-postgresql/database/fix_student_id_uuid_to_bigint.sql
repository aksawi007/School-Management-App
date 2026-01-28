-- Script to convert student_id columns from UUID to BIGINT
-- This fixes the type mismatch between Java Long and PostgreSQL UUID for student_id foreign keys

-- Disable foreign key checks temporarily
SET session_replication_role = replica;

-- Convert guardian table
ALTER TABLE sma_student.guardian 
  ALTER COLUMN student_id TYPE BIGINT USING student_id::text::bigint;

-- Convert address table
ALTER TABLE sma_student.address 
  ALTER COLUMN student_id TYPE BIGINT USING student_id::text::bigint;

-- Convert enrollment table (if exists)
ALTER TABLE sma_student.enrollment 
  ALTER COLUMN student_id TYPE BIGINT USING student_id::text::bigint;

-- Convert attendance table (if exists)
DO $$ 
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.tables 
             WHERE table_schema = 'sma_student' 
             AND table_name = 'attendance') THEN
    ALTER TABLE sma_student.attendance 
      ALTER COLUMN student_id TYPE BIGINT USING student_id::text::bigint;
  END IF;
END $$;

-- Convert fee_payment table (if exists)
DO $$ 
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.tables 
             WHERE table_schema = 'sma_student' 
             AND table_name = 'fee_payment') THEN
    ALTER TABLE sma_student.fee_payment 
      ALTER COLUMN student_id TYPE BIGINT USING student_id::text::bigint;
  END IF;
END $$;

-- Convert exam_result table (if exists)
DO $$ 
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.tables 
             WHERE table_schema = 'sma_student' 
             AND table_name = 'exam_result') THEN
    ALTER TABLE sma_student.exam_result 
      ALTER COLUMN student_id TYPE BIGINT USING student_id::text::bigint;
  END IF;
END $$;

-- Re-enable foreign key checks
SET session_replication_role = DEFAULT;

-- Verify the changes
SELECT 
    table_name,
    column_name,
    data_type 
FROM information_schema.columns 
WHERE table_schema = 'sma_student' 
  AND column_name = 'student_id'
ORDER BY table_name;
