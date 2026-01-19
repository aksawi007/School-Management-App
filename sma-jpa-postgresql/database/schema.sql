-- =====================================================
-- School Management Application - Database Schema
-- Schema: sma_admin
-- PostgreSQL Database
-- =====================================================

-- Create schema
CREATE SCHEMA IF NOT EXISTS sma_admin;

-- =====================================================
-- 1. SCHOOL SETUP MODULE
-- =====================================================

-- School Profile Table (Multi-tenant)
CREATE TABLE sma_admin.school_profile (
    id BIGSERIAL PRIMARY KEY,
    school_code VARCHAR(50) UNIQUE NOT NULL,
    school_name VARCHAR(200) NOT NULL,
    school_type VARCHAR(50),
    affiliation_board VARCHAR(100),
    affiliation_number VARCHAR(100),
    established_date DATE,
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100),
    postal_code VARCHAR(20),
    phone_number VARCHAR(20),
    email VARCHAR(100),
    website VARCHAR(200),
    principal_name VARCHAR(150),
    principal_email VARCHAR(100),
    principal_phone VARCHAR(20),
    registration_status VARCHAR(20),
    logo_url VARCHAR(500),
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_school_profile_code ON sma_admin.school_profile(school_code);
CREATE INDEX idx_school_profile_status ON sma_admin.school_profile(registration_status);

-- Academic Year Table
CREATE TABLE sma_admin.academic_year (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL REFERENCES sma_admin.school_profile(id),
    year_code VARCHAR(20) NOT NULL,
    year_name VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_current BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20),
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE(school_id, year_code)
);

CREATE INDEX idx_academic_year_school ON sma_admin.academic_year(school_id);
CREATE INDEX idx_academic_year_current ON sma_admin.academic_year(school_id, is_current);

-- =====================================================
-- 2. MASTER DATA MODULE
-- =====================================================

-- Class Master Table
CREATE TABLE sma_admin.class_master (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL REFERENCES sma_admin.school_profile(id),
    class_code VARCHAR(20) NOT NULL,
    class_name VARCHAR(100) NOT NULL,
    display_order INTEGER,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE(school_id, class_code)
);

CREATE INDEX idx_class_master_school ON sma_admin.class_master(school_id);

-- Section Master Table
CREATE TABLE sma_admin.section_master (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL REFERENCES sma_admin.school_profile(id),
    class_id BIGINT NOT NULL REFERENCES sma_admin.class_master(id),
    section_code VARCHAR(10) NOT NULL,
    section_name VARCHAR(100) NOT NULL,
    capacity INTEGER,
    room_number VARCHAR(50),
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE(school_id, class_id, section_code)
);

CREATE INDEX idx_section_master_school_class ON sma_admin.section_master(school_id, class_id);

-- Subject Master Table
CREATE TABLE sma_admin.subject_master (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL REFERENCES sma_admin.school_profile(id),
    subject_code VARCHAR(20) NOT NULL,
    subject_name VARCHAR(150) NOT NULL,
    subject_type VARCHAR(30),
    is_mandatory BOOLEAN DEFAULT TRUE,
    credits INTEGER,
    max_marks INTEGER,
    pass_marks INTEGER,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE(school_id, subject_code)
);

CREATE INDEX idx_subject_master_school ON sma_admin.subject_master(school_id);

-- Department Master Table
CREATE TABLE sma_admin.department_master (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL REFERENCES sma_admin.school_profile(id),
    department_code VARCHAR(20) NOT NULL,
    department_name VARCHAR(150) NOT NULL,
    department_type VARCHAR(50),
    hod_name VARCHAR(150),
    hod_email VARCHAR(100),
    hod_phone VARCHAR(20),
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE(school_id, department_code)
);

CREATE INDEX idx_department_master_school ON sma_admin.department_master(school_id);

-- =====================================================
-- 3. STUDENT MANAGEMENT MODULE
-- =====================================================

-- Student Table
CREATE TABLE sma_admin.student (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL REFERENCES sma_admin.school_profile(id),
    admission_number VARCHAR(50) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(10),
    blood_group VARCHAR(10),
    nationality VARCHAR(100),
    religion VARCHAR(50),
    category VARCHAR(30),
    email VARCHAR(100),
    phone_number VARCHAR(20),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    admission_date DATE NOT NULL,
    student_status VARCHAR(20),
    photo_url VARCHAR(500),
    father_name VARCHAR(150),
    father_phone VARCHAR(20),
    father_email VARCHAR(100),
    father_occupation VARCHAR(100),
    mother_name VARCHAR(150),
    mother_phone VARCHAR(20),
    mother_email VARCHAR(100),
    mother_occupation VARCHAR(100),
    guardian_name VARCHAR(150),
    guardian_phone VARCHAR(20),
    guardian_email VARCHAR(100),
    guardian_relation VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE(school_id, admission_number)
);

CREATE INDEX idx_student_school ON sma_admin.student(school_id);
CREATE INDEX idx_student_status ON sma_admin.student(school_id, student_status);

-- Student Enrollment Table
CREATE TABLE sma_admin.student_enrollment (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES sma_admin.student(id),
    academic_year_id BIGINT NOT NULL REFERENCES sma_admin.academic_year(id),
    class_id BIGINT NOT NULL REFERENCES sma_admin.class_master(id),
    section_id BIGINT NOT NULL REFERENCES sma_admin.section_master(id),
    roll_number VARCHAR(50),
    enrollment_date DATE NOT NULL,
    enrollment_status VARCHAR(20),
    is_promoted BOOLEAN DEFAULT FALSE,
    remarks VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE(student_id, academic_year_id)
);

CREATE INDEX idx_enrollment_student ON sma_admin.student_enrollment(student_id);
CREATE INDEX idx_enrollment_year_class_section ON sma_admin.student_enrollment(academic_year_id, class_id, section_id);

-- =====================================================
-- 4. STAFF MANAGEMENT MODULE
-- =====================================================

-- Staff Table
CREATE TABLE sma_admin.staff (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL REFERENCES sma_admin.school_profile(id),
    employee_code VARCHAR(50) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(10),
    blood_group VARCHAR(10),
    email VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    staff_type VARCHAR(30),
    designation VARCHAR(100),
    department_id BIGINT REFERENCES sma_admin.department_master(id),
    qualification VARCHAR(200),
    specialization VARCHAR(200),
    experience_years INTEGER,
    joining_date DATE NOT NULL,
    employment_type VARCHAR(30),
    salary DECIMAL(10,2),
    staff_status VARCHAR(20),
    photo_url VARCHAR(500),
    aadhar_number VARCHAR(20),
    pan_number VARCHAR(20),
    bank_account_number VARCHAR(50),
    bank_name VARCHAR(100),
    bank_ifsc_code VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE(school_id, employee_code)
);

CREATE INDEX idx_staff_school ON sma_admin.staff(school_id);
CREATE INDEX idx_staff_type ON sma_admin.staff(school_id, staff_type);

-- Staff Subject Assignment Table
CREATE TABLE sma_admin.staff_subject_assignment (
    id BIGSERIAL PRIMARY KEY,
    staff_id BIGINT NOT NULL REFERENCES sma_admin.staff(id),
    subject_id BIGINT NOT NULL REFERENCES sma_admin.subject_master(id),
    class_id BIGINT NOT NULL REFERENCES sma_admin.class_master(id),
    section_id BIGINT REFERENCES sma_admin.section_master(id),
    academic_year_id BIGINT NOT NULL REFERENCES sma_admin.academic_year(id),
    is_class_teacher BOOLEAN DEFAULT FALSE,
    remarks VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_staff_assignment_staff ON sma_admin.staff_subject_assignment(staff_id, academic_year_id);
CREATE INDEX idx_staff_assignment_class_section ON sma_admin.staff_subject_assignment(academic_year_id, class_id, section_id);

-- =====================================================
-- 5. TIMETABLE MODULE
-- =====================================================

-- Timetable Table
CREATE TABLE sma_admin.timetable (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL REFERENCES sma_admin.school_profile(id),
    academic_year_id BIGINT NOT NULL REFERENCES sma_admin.academic_year(id),
    class_id BIGINT NOT NULL REFERENCES sma_admin.class_master(id),
    section_id BIGINT NOT NULL REFERENCES sma_admin.section_master(id),
    subject_id BIGINT NOT NULL REFERENCES sma_admin.subject_master(id),
    staff_id BIGINT NOT NULL REFERENCES sma_admin.staff(id),
    day_of_week VARCHAR(20) NOT NULL,
    period_number INTEGER NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    room_number VARCHAR(50),
    is_lab_session BOOLEAN DEFAULT FALSE,
    remarks VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_timetable_class_section ON sma_admin.timetable(school_id, academic_year_id, class_id, section_id);
CREATE INDEX idx_timetable_staff ON sma_admin.timetable(staff_id, academic_year_id);

-- =====================================================
-- 6. EXAM MODULE
-- =====================================================

-- Exam Table
CREATE TABLE sma_admin.exam (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL REFERENCES sma_admin.school_profile(id),
    academic_year_id BIGINT NOT NULL REFERENCES sma_admin.academic_year(id),
    exam_code VARCHAR(50) NOT NULL,
    exam_name VARCHAR(150) NOT NULL,
    exam_type VARCHAR(30),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    exam_status VARCHAR(20),
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_exam_school_year ON sma_admin.exam(school_id, academic_year_id);

-- Exam Schedule Table
CREATE TABLE sma_admin.exam_schedule (
    id BIGSERIAL PRIMARY KEY,
    exam_id BIGINT NOT NULL REFERENCES sma_admin.exam(id),
    class_id BIGINT NOT NULL REFERENCES sma_admin.class_master(id),
    subject_id BIGINT NOT NULL REFERENCES sma_admin.subject_master(id),
    exam_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    duration_minutes INTEGER NOT NULL,
    max_marks INTEGER NOT NULL,
    pass_marks INTEGER NOT NULL,
    room_number VARCHAR(50),
    remarks VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_exam_schedule_exam ON sma_admin.exam_schedule(exam_id);
CREATE INDEX idx_exam_schedule_class ON sma_admin.exam_schedule(exam_id, class_id);

-- =====================================================
-- 7. TRANSPORT MODULE
-- =====================================================

-- Transport Route Table
CREATE TABLE sma_admin.transport_route (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL REFERENCES sma_admin.school_profile(id),
    route_code VARCHAR(50) NOT NULL,
    route_name VARCHAR(150) NOT NULL,
    start_location VARCHAR(200),
    end_location VARCHAR(200),
    total_distance_km DOUBLE PRECISION,
    estimated_duration_minutes INTEGER,
    route_status VARCHAR(20),
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE(school_id, route_code)
);

CREATE INDEX idx_transport_route_school ON sma_admin.transport_route(school_id);

-- Transport Stop Table
CREATE TABLE sma_admin.transport_stop (
    id BIGSERIAL PRIMARY KEY,
    route_id BIGINT NOT NULL REFERENCES sma_admin.transport_route(id),
    stop_name VARCHAR(150) NOT NULL,
    stop_address VARCHAR(255),
    stop_sequence INTEGER NOT NULL,
    pickup_time TIME,
    drop_time TIME,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_transport_stop_route ON sma_admin.transport_stop(route_id);

-- Vehicle Table
CREATE TABLE sma_admin.vehicle (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL REFERENCES sma_admin.school_profile(id),
    vehicle_number VARCHAR(50) NOT NULL,
    vehicle_type VARCHAR(30),
    vehicle_model VARCHAR(100),
    seating_capacity INTEGER NOT NULL,
    driver_name VARCHAR(150),
    driver_phone VARCHAR(20),
    driver_license_number VARCHAR(50),
    conductor_name VARCHAR(150),
    conductor_phone VARCHAR(20),
    insurance_number VARCHAR(100),
    fitness_certificate_number VARCHAR(100),
    vehicle_status VARCHAR(20),
    remarks VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE(school_id, vehicle_number)
);

CREATE INDEX idx_vehicle_school ON sma_admin.vehicle(school_id);

-- Student Transport Table
CREATE TABLE sma_admin.student_transport (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES sma_admin.student(id),
    route_id BIGINT NOT NULL REFERENCES sma_admin.transport_route(id),
    stop_id BIGINT NOT NULL REFERENCES sma_admin.transport_stop(id),
    vehicle_id BIGINT REFERENCES sma_admin.vehicle(id),
    academic_year_id BIGINT NOT NULL REFERENCES sma_admin.academic_year(id),
    allocation_date DATE NOT NULL,
    transport_type VARCHAR(20),
    allocation_status VARCHAR(20),
    remarks VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_student_transport_student ON sma_admin.student_transport(student_id, academic_year_id);
CREATE INDEX idx_student_transport_route ON sma_admin.student_transport(route_id, academic_year_id);

-- =====================================================
-- 8. FEE MODULE
-- =====================================================

-- Fee Structure Table
CREATE TABLE sma_admin.fee_structure (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL REFERENCES sma_admin.school_profile(id),
    academic_year_id BIGINT NOT NULL REFERENCES sma_admin.academic_year(id),
    structure_code VARCHAR(50) NOT NULL,
    structure_name VARCHAR(150) NOT NULL,
    applicable_for VARCHAR(50),
    structure_status VARCHAR(20),
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_fee_structure_school_year ON sma_admin.fee_structure(school_id, academic_year_id);

-- Fee Category Table
CREATE TABLE sma_admin.fee_category (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL REFERENCES sma_admin.school_profile(id),
    category_code VARCHAR(50) NOT NULL,
    category_name VARCHAR(150) NOT NULL,
    category_type VARCHAR(30),
    is_mandatory BOOLEAN NOT NULL DEFAULT TRUE,
    is_refundable BOOLEAN DEFAULT FALSE,
    display_order INTEGER,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE(school_id, category_code)
);

CREATE INDEX idx_fee_category_school ON sma_admin.fee_category(school_id);

-- Fee Component Table
CREATE TABLE sma_admin.fee_component (
    id BIGSERIAL PRIMARY KEY,
    fee_structure_id BIGINT NOT NULL REFERENCES sma_admin.fee_structure(id),
    fee_category_id BIGINT NOT NULL REFERENCES sma_admin.fee_category(id),
    class_id BIGINT REFERENCES sma_admin.class_master(id),
    amount DECIMAL(10,2) NOT NULL,
    frequency VARCHAR(20),
    due_day INTEGER,
    late_fee_applicable BOOLEAN DEFAULT FALSE,
    late_fee_amount DECIMAL(10,2),
    late_fee_grace_days INTEGER,
    discount_percentage DECIMAL(5,2),
    remarks VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_fee_component_structure ON sma_admin.fee_component(fee_structure_id);
CREATE INDEX idx_fee_component_class ON sma_admin.fee_component(fee_structure_id, class_id);

-- =====================================================
-- COMMENTS
-- =====================================================

COMMENT ON SCHEMA sma_admin IS 'School Management Application - Admin Module Schema';
COMMENT ON TABLE sma_admin.school_profile IS 'Multi-tenant school profile data';
COMMENT ON TABLE sma_admin.academic_year IS 'Academic year configuration per school';
COMMENT ON TABLE sma_admin.class_master IS 'Class/Grade master data';
COMMENT ON TABLE sma_admin.section_master IS 'Section master data per class';
COMMENT ON TABLE sma_admin.subject_master IS 'Subject master data';
COMMENT ON TABLE sma_admin.department_master IS 'Department master data';
COMMENT ON TABLE sma_admin.student IS 'Student profile information';
COMMENT ON TABLE sma_admin.student_enrollment IS 'Student enrollment per academic year';
COMMENT ON TABLE sma_admin.staff IS 'Staff/Teacher profile information';
COMMENT ON TABLE sma_admin.staff_subject_assignment IS 'Staff subject teaching assignments';
COMMENT ON TABLE sma_admin.timetable IS 'Class timetable/schedule';
COMMENT ON TABLE sma_admin.exam IS 'Exam configuration';
COMMENT ON TABLE sma_admin.exam_schedule IS 'Exam schedule per subject';
COMMENT ON TABLE sma_admin.transport_route IS 'Transport route configuration';
COMMENT ON TABLE sma_admin.transport_stop IS 'Bus stops along routes';
COMMENT ON TABLE sma_admin.vehicle IS 'Vehicle/Bus information';
COMMENT ON TABLE sma_admin.student_transport IS 'Student transport allocation';
COMMENT ON TABLE sma_admin.fee_structure IS 'Fee structure configuration';
COMMENT ON TABLE sma_admin.fee_category IS 'Fee category master';
COMMENT ON TABLE sma_admin.fee_component IS 'Individual fee components';
