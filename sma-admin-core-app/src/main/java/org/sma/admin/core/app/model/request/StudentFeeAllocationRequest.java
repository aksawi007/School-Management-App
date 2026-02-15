package org.sma.admin.core.app.model.request;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for manually allocating fee to a student
 */
public class StudentFeeAllocationRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Fee category ID is required")
    private Long feeCategoryId;

    private Long feeStructureId; // Optional - link to fee structure

    @NotNull(message = "Fee amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Fee amount must be greater than 0")
    private BigDecimal feeAmount;

    @NotBlank(message = "Duration type is required")
    @Pattern(regexp = "MONTHLY|QUARTERLY|HALF_YEARLY|ANNUAL|ONE_TIME", 
             message = "Duration type must be MONTHLY, QUARTERLY, HALF_YEARLY, ANNUAL, or ONE_TIME")
    private String durationType;

    @Size(max = 10, message = "Applicable month must not exceed 10 characters")
    private String applicableMonth; // JANUARY, FEBRUARY, etc., for MONTHLY

    @Size(max = 10, message = "Quarter must not exceed 10 characters")
    private String quarter; // Q1, Q2, Q3, Q4 for QUARTERLY

    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in the future")
    private LocalDate dueDate;

    private LocalDate paymentDeadline;

    @NotNull(message = "Mandatory flag is required")
    private Boolean isMandatory = true;

    private BigDecimal discountAmount;

    @Size(max = 300, message = "Discount reason must not exceed 300 characters")
    private String discountReason;

    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;

    // Getters and Setters
    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getFeeCategoryId() {
        return feeCategoryId;
    }

    public void setFeeCategoryId(Long feeCategoryId) {
        this.feeCategoryId = feeCategoryId;
    }

    public Long getFeeStructureId() {
        return feeStructureId;
    }

    public void setFeeStructureId(Long feeStructureId) {
        this.feeStructureId = feeStructureId;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public String getDurationType() {
        return durationType;
    }

    public void setDurationType(String durationType) {
        this.durationType = durationType;
    }

    public String getApplicableMonth() {
        return applicableMonth;
    }

    public void setApplicableMonth(String applicableMonth) {
        this.applicableMonth = applicableMonth;
    }

    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        this.quarter = quarter;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getPaymentDeadline() {
        return paymentDeadline;
    }

    public void setPaymentDeadline(LocalDate paymentDeadline) {
        this.paymentDeadline = paymentDeadline;
    }

    public Boolean getIsMandatory() {
        return isMandatory;
    }

    public void setIsMandatory(Boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getDiscountReason() {
        return discountReason;
    }

    public void setDiscountReason(String discountReason) {
        this.discountReason = discountReason;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
