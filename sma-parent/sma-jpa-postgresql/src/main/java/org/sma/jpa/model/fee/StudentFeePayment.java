package org.sma.jpa.model.fee;

import org.sma.jpa.model.BaseEntity;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.studentmgmt.StudentProfile;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity for Student Fee Payment - Payment Tracking Per Installment
 */
@Entity
@Table(name = "student_fee_payment", schema = "sma_admin",
        indexes = {
            @Index(name = "idx_student_installment", columnList = "student_id,fee_installment_id"),
            @Index(name = "idx_fee_installment", columnList = "fee_installment_id"),
            @Index(name = "idx_school_student", columnList = "school_id,student_id")
        })
public class StudentFeePayment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private SchoolProfile school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfile student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_installment_id", nullable = false)
    private FeeInstallment feeInstallment;

    @Column(name = "amount_paid", nullable = false, precision = 12, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "discount_amount", precision = 12, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "paid_on", nullable = false)
    private LocalDateTime paidOn;

    @Column(name = "payment_ref", length = 100)
    private String paymentRef;

    @Column(name = "remarks", length = 300)
    private String remarks;

    // Getters and Setters
    public SchoolProfile getSchool() {
        return school;
    }

    public void setSchool(SchoolProfile school) {
        this.school = school;
    }

    public StudentProfile getStudent() {
        return student;
    }

    public void setStudent(StudentProfile student) {
        this.student = student;
    }

    public FeeInstallment getFeeInstallment() {
        return feeInstallment;
    }

    public void setFeeInstallment(FeeInstallment feeInstallment) {
        this.feeInstallment = feeInstallment;
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
