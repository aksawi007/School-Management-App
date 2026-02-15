package org.sma.admin.core.app.model.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Response DTO for Fee Plan
 */
public class FeePlanResponse {
    
    private Long id;
    private Long schoolId;
    private Long academicYearId;
    private String academicYearName;
    private Long categoryId;
    private String categoryName;
    private String categoryCode;
    private BigDecimal totalAmount;
    private String frequency;
    private Integer installmentsCount;
    private String status;
    private List<InstallmentResponse> installments;

    // Nested class for installment response
    public static class InstallmentResponse {
        private Long id;
        private Integer installmentNo;
        private String installmentName;
        private LocalDate periodStartDate;
        private LocalDate periodEndDate;
        private BigDecimal amountDue;
        private LocalDate dueDate;
        private String status;

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public Long getAcademicYearId() {
        return academicYearId;
    }

    public void setAcademicYearId(Long academicYearId) {
        this.academicYearId = academicYearId;
    }

    public String getAcademicYearName() {
        return academicYearName;
    }

    public void setAcademicYearName(String academicYearName) {
        this.academicYearName = academicYearName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
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

    public List<InstallmentResponse> getInstallments() {
        return installments;
    }

    public void setInstallments(List<InstallmentResponse> installments) {
        this.installments = installments;
    }
}
