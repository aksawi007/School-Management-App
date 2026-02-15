package org.sma.admin.core.app.model.request;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for creating/updating Fee Plan
 */
public class FeePlanRequest {

    @NotNull(message = "Academic year ID is required")
    private Long academicYearId;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
    private BigDecimal totalAmount;

    @NotBlank(message = "Frequency is required")
    @Size(max = 20, message = "Frequency must not exceed 20 characters")
    private String frequency; // ONCE, MONTHLY, QUARTERLY, HALF_YEARLY

    @NotNull(message = "Installments count is required")
    @Min(value = 1, message = "Installments count must be at least 1")
    private Integer installmentsCount;

    @Size(max = 20, message = "Status must not exceed 20 characters")
    private String status; // ACTIVE, INACTIVE

    private List<InstallmentDetail> installments;

    // Nested class for installment details
    public static class InstallmentDetail {
        @NotNull(message = "Installment number is required")
        private Integer installmentNo;

        @NotBlank(message = "Installment name is required")
        @Size(max = 100, message = "Installment name must not exceed 100 characters")
        private String installmentName;

        @NotNull(message = "Period start date is required")
        private LocalDate periodStartDate;

        @NotNull(message = "Period end date is required")
        private LocalDate periodEndDate;

        @NotNull(message = "Amount due is required")
        @DecimalMin(value = "0.00", message = "Amount due must be non-negative")
        private BigDecimal amountDue;

        private LocalDate dueDate;

        // Getters and Setters
        public Integer getInstallmentNo() {
            return installmentNo;
        }

        public void setInstallmentNo(Integer installmentNo) {
            this.installmentNo = installmentNo;
        }

        public String getInstallmentName() {
            return installmentName;
        }

        public void setInstallmentName(String installmentName) {
            this.installmentName = installmentName;
        }

        public LocalDate getPeriodStartDate() {
            return periodStartDate;
        }

        public void setPeriodStartDate(LocalDate periodStartDate) {
            this.periodStartDate = periodStartDate;
        }

        public LocalDate getPeriodEndDate() {
            return periodEndDate;
        }

        public void setPeriodEndDate(LocalDate periodEndDate) {
            this.periodEndDate = periodEndDate;
        }

        public BigDecimal getAmountDue() {
            return amountDue;
        }

        public void setAmountDue(BigDecimal amountDue) {
            this.amountDue = amountDue;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public void setDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
        }
    }

    // Getters and Setters
    public Long getAcademicYearId() {
        return academicYearId;
    }

    public void setAcademicYearId(Long academicYearId) {
        this.academicYearId = academicYearId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Integer getInstallmentsCount() {
        return installmentsCount;
    }

    public void setInstallmentsCount(Integer installmentsCount) {
        this.installmentsCount = installmentsCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<InstallmentDetail> getInstallments() {
        return installments;
    }

    public void setInstallments(List<InstallmentDetail> installments) {
        this.installments = installments;
    }
}
