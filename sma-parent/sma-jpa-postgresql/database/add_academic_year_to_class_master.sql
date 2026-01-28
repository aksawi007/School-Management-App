-- Add academic_year_id column to class_master table
-- This links each class to a specific academic year

-- Step 1: Add the column as nullable first
ALTER TABLE sma_admin.class_master
ADD COLUMN academic_year_id BIGINT;

-- Step 2: Add foreign key constraint
ALTER TABLE sma_admin.class_master
ADD CONSTRAINT fk_class_master_academic_year
FOREIGN KEY (academic_year_id) REFERENCES sma_admin.academic_year(id);

-- Step 3: Update the unique constraint to include academic_year_id
ALTER TABLE sma_admin.class_master
DROP CONSTRAINT IF EXISTS class_master_school_id_class_code_key;

ALTER TABLE sma_admin.class_master
ADD CONSTRAINT class_master_school_id_academic_year_id_class_code_key
UNIQUE (school_id, academic_year_id, class_code);

-- Step 4: After data migration, make the column NOT NULL
-- Note: First ensure all existing records have academic_year_id populated
-- ALTER TABLE sma_admin.class_master
-- ALTER COLUMN academic_year_id SET NOT NULL;

-- Optional: Add an index for better query performance
CREATE INDEX IF NOT EXISTS idx_class_master_academic_year_id
ON sma_admin.class_master(academic_year_id);

-- Display current state
SELECT 
    tc.table_name, 
    kcu.column_name, 
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name 
FROM 
    information_schema.table_constraints AS tc 
    JOIN information_schema.key_column_usage AS kcu
      ON tc.constraint_name = kcu.constraint_name
      AND tc.table_schema = kcu.table_schema
    JOIN information_schema.constraint_column_usage AS ccu
      ON ccu.constraint_name = tc.constraint_name
      AND ccu.table_schema = tc.table_schema
WHERE tc.constraint_type = 'FOREIGN KEY' 
    AND tc.table_schema = 'sma_admin'
    AND tc.table_name = 'class_master';
