-- Migration to create fresh fee management structure
-- Version: 1.9
-- Date: February 15, 2026

-- Drop existing fee tables if they exist (except fee_category)
DROP TABLE IF EXISTS sma_admin.student_fee_payment CASCADE;
DROP TABLE IF EXISTS sma_admin.fee_installment CASCADE;
DROP TABLE IF EXISTS sma_admin.fee_plan CASCADE;
DROP TABLE IF EXISTS sma_admin.student_fee_payment_status CASCADE;
DROP TABLE IF EXISTS sma_admin.academic_year_fee_allocation CASCADE;

-- 1. Create fee_plan table (Year + Category Level)
CREATE TABLE sma_admin.fee_plan (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL,
    academic_year_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    total_amount NUMERIC(12,2) NOT NULL,
    frequency VARCHAR(20) NOT NULL,
    installments_count INTEGER NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_fee_plan_school FOREIGN KEY (school_id) 
        REFERENCES sma_admin.school_profile(id) ON DELETE CASCADE,
    CONSTRAINT fk_fee_plan_academic_year FOREIGN KEY (academic_year_id) 
        REFERENCES sma_admin.academic_year(id) ON DELETE CASCADE,
    CONSTRAINT fk_fee_plan_category FOREIGN KEY (category_id) 
        REFERENCES sma_admin.fee_category(id) ON DELETE CASCADE,
    
    CONSTRAINT uq_fee_plan_school_year_category 
        UNIQUE (school_id, academic_year_id, category_id),
    
    CONSTRAINT chk_fee_plan_amount CHECK (total_amount > 0),
    CONSTRAINT chk_fee_plan_installments CHECK (installments_count > 0)
);

-- 2. Create fee_installment table (Month-Level Tracking)
CREATE TABLE sma_admin.fee_installment (
    id BIGSERIAL PRIMARY KEY,
    fee_plan_id BIGINT NOT NULL,
    installment_no INTEGER NOT NULL,
    installment_name VARCHAR(100) NOT NULL,
    period_start_date DATE NOT NULL,
    period_end_date DATE NOT NULL,
    amount_due NUMERIC(12,2) NOT NULL,
    due_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_fee_installment_plan FOREIGN KEY (fee_plan_id) 
        REFERENCES sma_admin.fee_plan(id) ON DELETE CASCADE,
    
    CONSTRAINT uq_fee_installment_plan_no 
        UNIQUE (fee_plan_id, installment_no),
    
    CONSTRAINT chk_fee_installment_amount CHECK (amount_due >= 0),
    CONSTRAINT chk_fee_installment_period CHECK (period_end_date >= period_start_date)
);

-- 3. Create student_fee_payment table (Payment Tracking Per Installment)
CREATE TABLE sma_admin.student_fee_payment (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    fee_installment_id BIGINT NOT NULL,
    amount_paid NUMERIC(12,2) NOT NULL,
    discount_amount NUMERIC(12,2) DEFAULT 0,
    paid_on TIMESTAMP NOT NULL,
    payment_ref VARCHAR(100),
    remarks VARCHAR(300),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_student_fee_payment_school FOREIGN KEY (school_id) 
        REFERENCES sma_admin.school_profile(id) ON DELETE CASCADE,
    CONSTRAINT fk_student_fee_payment_student FOREIGN KEY (student_id) 
        REFERENCES sma_admin.student_profile(id) ON DELETE CASCADE,
    CONSTRAINT fk_student_fee_payment_installment FOREIGN KEY (fee_installment_id) 
        REFERENCES sma_admin.fee_installment(id) ON DELETE CASCADE,
    
    CONSTRAINT chk_student_fee_payment_amount CHECK (amount_paid >= 0),
    CONSTRAINT chk_student_fee_payment_discount CHECK (discount_amount >= 0)
);

-- Create indexes for performance
CREATE INDEX idx_fee_plan_school_year ON sma_admin.fee_plan(school_id, academic_year_id);
CREATE INDEX idx_fee_plan_status ON sma_admin.fee_plan(status);

CREATE INDEX idx_fee_installment_plan ON sma_admin.fee_installment(fee_plan_id);
CREATE INDEX idx_fee_installment_status ON sma_admin.fee_installment(status);

CREATE INDEX idx_student_fee_payment_student_installment ON sma_admin.student_fee_payment(student_id, fee_installment_id);
CREATE INDEX idx_student_fee_payment_installment ON sma_admin.student_fee_payment(fee_installment_id);
CREATE INDEX idx_student_fee_payment_school_student ON sma_admin.student_fee_payment(school_id, student_id);
CREATE INDEX idx_student_fee_payment_paid_on ON sma_admin.student_fee_payment(paid_on);

-- Add comments
COMMENT ON TABLE sma_admin.fee_plan IS 'Fee plan at year and category level';
COMMENT ON TABLE sma_admin.fee_installment IS 'Month-level installment tracking';
COMMENT ON TABLE sma_admin.student_fee_payment IS 'Payment tracking per installment per student';

COMMENT ON COLUMN sma_admin.fee_plan.frequency IS 'ONCE, MONTHLY, QUARTERLY, HALF_YEARLY';
COMMENT ON COLUMN sma_admin.fee_plan.status IS 'ACTIVE or INACTIVE';
COMMENT ON COLUMN sma_admin.fee_installment.status IS 'ACTIVE or INACTIVE';
