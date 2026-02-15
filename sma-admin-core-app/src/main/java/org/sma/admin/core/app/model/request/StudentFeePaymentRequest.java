package org.sma.admin.core.app.model.request;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request DTO for recording Student Fee Payment
 */
public class StudentFeePaymentRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Fee installment ID is required")
    private Long feeInstallmentId;

    @NotNull(message = "Amount paid is required")
    @DecimalMin(value = "0.00", message = "Amount paid must be non-negative")
    private BigDecimal amountPaid;

    @DecimalMin(value = "0.00", message = "Discount amount must be non-negative")
    private BigDecimal discountAmount;

    @NotNull(message = "Paid on date is required")
    private LocalDateTime paidOn;

    @Size(max = 100, message = "Payment reference must not exceed 100 characters")
    private String paymentRef;

    @Size(max = 300, message = "Remarks must not exceed 300 characters")
    private String remarks;

    // Getters and Setters
    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getFeeInstallmentId() {
        return feeInstallmentId;
    }

    public void setFeeInstallmentId(Long feeInstallmentId) {
        this.feeInstallmentId = feeInstallmentId;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public LocalDateTime getPaidOn() {
        return paidOn;
    }

    public void setPaidOn(LocalDateTime paidOn) {
        this.paidOn = paidOn;
    }

    public String getPaymentRef() {
        return paymentRef;
    }

    public void setPaymentRef(String paymentRef) {
        this.paymentRef = paymentRef;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
