package org.sma.admin.core.app.model.request;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Request DTO for creating a fee category
 */
public class FeeCategoryRequest {

    @NotBlank(message = "Category code is required")
    @Size(max = 50, message = "Category code must not exceed 50 characters")
    private String categoryCode;

    @NotBlank(message = "Category name is required")
    @Size(max = 150, message = "Category name must not exceed 150 characters")
    private String categoryName;

    @Size(max = 30, message = "Category type must not exceed 30 characters")
    private String categoryType; // TUITION, TRANSPORT, LIBRARY, EXAM, MISCELLANEOUS

    @NotNull(message = "Mandatory flag is required")
    private Boolean isMandatory = true;

    private Boolean isRefundable = false;

    private Integer displayOrder;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Size(max = 20, message = "Fee applicability must not exceed 20 characters")
    private String feeApplicability; // ANNUAL, MONTHLY

    @Size(max = 20, message = "Payment frequency must not exceed 20 characters")
    private String paymentFrequency; // ONCE, MONTHLY, QUARTERLY, HALF_YEARLY

    @Size(max = 20, message = "Status must not exceed 20 characters")
    private String status; // ACTIVE, INACTIVE

    // Getters and Setters
    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public Boolean getIsMandatory() {
        return isMandatory;
    }

    public void setIsMandatory(Boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    public Boolean getIsRefundable() {
        return isRefundable;
    }

    public void setIsRefundable(Boolean isRefundable) {
        this.isRefundable = isRefundable;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFeeApplicability() {
        return feeApplicability;
    }

    public void setFeeApplicability(String feeApplicability) {
        this.feeApplicability = feeApplicability;
    }

    public String getPaymentFrequency() {
        return paymentFrequency;
    }

    public void setPaymentFrequency(String paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
