-- Migration to add status column to fee_category table
-- Version: 1.8
-- Date: February 15, 2026

-- Add status column to fee_category
ALTER TABLE sma_admin.fee_category
ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

-- Add comment to status column
COMMENT ON COLUMN sma_admin.fee_category.status IS 'Status of fee category: ACTIVE or INACTIVE';

-- Create index on status for better query performance
CREATE INDEX idx_fee_category_status ON sma_admin.fee_category(status);

-- Create composite index for common query patterns
CREATE INDEX idx_fee_category_school_status ON sma_admin.fee_category(school_id, status);
