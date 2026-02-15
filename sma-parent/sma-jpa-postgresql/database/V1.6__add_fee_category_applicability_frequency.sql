-- Add fee applicability and payment frequency columns to fee_category table

ALTER TABLE sma_admin.fee_category 
ADD COLUMN fee_applicability VARCHAR(20) DEFAULT 'ANNUAL',
ADD COLUMN payment_frequency VARCHAR(20) DEFAULT 'ONCE';

-- Add comments for the new columns
COMMENT ON COLUMN sma_admin.fee_category.fee_applicability IS 'Defines if fee is applicable ANNUAL or MONTHLY';
COMMENT ON COLUMN sma_admin.fee_category.payment_frequency IS 'Defines payment frequency: ONCE, MONTHLY, QUARTERLY, HALF_YEARLY';
