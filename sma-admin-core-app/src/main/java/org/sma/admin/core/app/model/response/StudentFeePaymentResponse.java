package org.sma.admin.core.app.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for Student Fee Payment
 */
public class StudentFeePaymentResponse {
    
    private Long id;
    private Long schoolId;
    private Long studentId;
    private String studentName;
    private Long feeInstallmentId;
    private String installmentName;
    private BigDecimal amountPaid;
    private BigDecimal discountAmount;
    private LocalDateTime paidOn;
    private String paymentRef;
    private String remarks;

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

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Long getFeeInstallmentId() {
        return feeInstallmentId;
    }

    public void setFeeInstallmentId(Long feeInstallmentId) {
        this.feeInstallmentId = feeInstallmentId;
    }

    public String getInstallmentName() {
        return installmentName;
    }

    public void setInstallmentName(String installmentName) {
        this.installmentName = installmentName;
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
