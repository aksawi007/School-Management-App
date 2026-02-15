-- Create Academic Year Fee Allocation table
CREATE TABLE sma_admin.academic_year_fee_allocation (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL REFERENCES sma_admin.school_profile(id),
    academic_year_id BIGINT NOT NULL REFERENCES sma_admin.academic_year(id),
    fee_category_id BIGINT NOT NULL REFERENCES sma_admin.fee_category(id),
    allocation_code VARCHAR(50) NOT NULL UNIQUE,
    fee_amount DECIMAL(10,2) NOT NULL,
    due_date DATE,
    is_active BOOLEAN NOT NULL DEFAULT true,
    remarks VARCHAR(500),
    allocated_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uk_ayfa_school_year_category UNIQUE (school_id, academic_year_id, fee_category_id)
);

CREATE INDEX idx_ayfa_school_year ON sma_admin.academic_year_fee_allocation(school_id, academic_year_id);
CREATE INDEX idx_ayfa_category ON sma_admin.academic_year_fee_allocation(fee_category_id);
CREATE INDEX idx_ayfa_active ON sma_admin.academic_year_fee_allocation(is_active);

COMMENT ON TABLE sma_admin.academic_year_fee_allocation IS 'Fee allocation per academic year and category - Admin declares amounts here';
COMMENT ON COLUMN sma_admin.academic_year_fee_allocation.allocation_code IS 'Unique code like AYFA-2024-25-TUITION';
COMMENT ON COLUMN sma_admin.academic_year_fee_allocation.fee_amount IS 'Fee amount for this category for this academic year';

-- Create Student Fee Payment Status table
CREATE TABLE sma_admin.student_fee_payment_status (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL REFERENCES sma_admin.school_profile(id),
    academic_year_id BIGINT NOT NULL REFERENCES sma_admin.academic_year(id),
    student_id BIGINT NOT NULL REFERENCES sma_student.student_profile(id),
    fee_allocation_id BIGINT NOT NULL REFERENCES sma_admin.academic_year_fee_allocation(id),
    total_amount DECIMAL(10,2) NOT NULL,
    paid_amount DECIMAL(10,2) DEFAULT 0,
    pending_amount DECIMAL(10,2),
    discount_amount DECIMAL(10,2) DEFAULT 0,
    discount_reason VARCHAR(300),
    late_fee_amount DECIMAL(10,2) DEFAULT 0,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    due_date DATE,
    last_payment_date DATE,
    is_exempted BOOLEAN NOT NULL DEFAULT false,
    exemption_reason VARCHAR(300),
    remarks VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT uk_sfps_student_allocation UNIQUE (student_id, fee_allocation_id)
);

CREATE INDEX idx_sfps_student ON sma_admin.student_fee_payment_status(student_id);
CREATE INDEX idx_sfps_allocation ON sma_admin.student_fee_payment_status(fee_allocation_id);
CREATE INDEX idx_sfps_status ON sma_admin.student_fee_payment_status(payment_status);
CREATE INDEX idx_sfps_school_year ON sma_admin.student_fee_payment_status(school_id, academic_year_id);

COMMENT ON TABLE sma_admin.student_fee_payment_status IS 'Tracks payment status for each student - auto-generated from fee allocation';
COMMENT ON COLUMN sma_admin.student_fee_payment_status.payment_status IS 'PENDING, PAID, PARTIALLY_PAID, OVERDUE, WAIVED, EXEMPTED';
COMMENT ON COLUMN sma_admin.student_fee_payment_status.is_exempted IS 'For scholarship/waiver students';
