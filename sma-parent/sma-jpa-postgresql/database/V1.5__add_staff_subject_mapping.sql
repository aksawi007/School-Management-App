-- Add department_id to subject_master table
ALTER TABLE sma_admin.subject_master 
ADD COLUMN IF NOT EXISTS department_id BIGINT;

ALTER TABLE sma_admin.subject_master 
ADD CONSTRAINT fk_subject_department 
FOREIGN KEY (department_id) REFERENCES sma_admin.department_master(id);

CREATE INDEX IF NOT EXISTS idx_subject_department 
ON sma_admin.subject_master(department_id);

-- Create staff_subject_mapping table
CREATE TABLE IF NOT EXISTS sma_admin.staff_subject_mapping (
    id BIGINT PRIMARY KEY,
    school_id BIGINT NOT NULL,
    staff_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    proficiency_level VARCHAR(20),
    can_teach_primary BOOLEAN DEFAULT TRUE,
    can_teach_secondary BOOLEAN DEFAULT TRUE,
    can_teach_higher_secondary BOOLEAN DEFAULT TRUE,
    is_active BOOLEAN DEFAULT TRUE,
    remarks VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    
    CONSTRAINT fk_staff_subject_school FOREIGN KEY (school_id) 
        REFERENCES sma_admin.school_profile(id),
    CONSTRAINT fk_staff_subject_staff FOREIGN KEY (staff_id) 
        REFERENCES sma_admin.staff(id),
    CONSTRAINT fk_staff_subject_subject FOREIGN KEY (subject_id) 
        REFERENCES sma_admin.subject_master(id),
    CONSTRAINT uk_staff_subject UNIQUE (school_id, staff_id, subject_id)
);

CREATE INDEX idx_staff_subject_staff ON sma_admin.staff_subject_mapping(staff_id);
CREATE INDEX idx_staff_subject_subject ON sma_admin.staff_subject_mapping(subject_id);

COMMENT ON TABLE sma_admin.staff_subject_mapping IS 'Maps teachers to subjects they can teach - used for routine builder qualification checks';
COMMENT ON COLUMN sma_admin.staff_subject_mapping.proficiency_level IS 'EXPERT, QUALIFIED, SUBSTITUTE';
COMMENT ON COLUMN sma_admin.subject_master.department_id IS 'Optional department classification for subject';
