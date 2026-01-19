package org.sma.jpa.model.fee;

import org.sma.jpa.model.BaseEntity;
import org.sma.jpa.model.master.ClassMaster;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Entity for Fee Component - Individual fee items within a structure
 */
@Entity
@Table(name = "fee_component", schema = "sma_admin")
public class FeeComponent extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_structure_id", nullable = false)
    private FeeStructure feeStructure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_category_id", nullable = false)
    private FeeCategory feeCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private ClassMaster classMaster;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "frequency", length = 20)
    private String frequency; // MONTHLY, QUARTERLY, HALF_YEARLY, ANNUAL, ONE_TIME

    @Column(name = "due_day")
    private Integer dueDay; // Day of month when due

    @Column(name = "late_fee_applicable")
    private Boolean lateFeeApplicable = false;

    @Column(name = "late_fee_amount", precision = 10, scale = 2)
    private BigDecimal lateFeeAmount;

    @Column(name = "late_fee_grace_days")
    private Integer lateFeeGraceDays;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(name = "remarks", length = 500)
    private String remarks;

    // Getters and Setters
    public FeeStructure getFeeStructure() {
        return feeStructure;
    }

    public void setFeeStructure(FeeStructure feeStructure) {
        this.feeStructure = feeStructure;
    }

    public FeeCategory getFeeCategory() {
        return feeCategory;
    }

    public void setFeeCategory(FeeCategory feeCategory) {
        this.feeCategory = feeCategory;
    }

    public ClassMaster getClassMaster() {
        return classMaster;
    }

    public void setClassMaster(ClassMaster classMaster) {
        this.classMaster = classMaster;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Integer getDueDay() {
        return dueDay;
    }

    public void setDueDay(Integer dueDay) {
        this.dueDay = dueDay;
    }

    public Boolean getLateFeeApplicable() {
        return lateFeeApplicable;
    }

    public void setLateFeeApplicable(Boolean lateFeeApplicable) {
        this.lateFeeApplicable = lateFeeApplicable;
    }

    public BigDecimal getLateFeeAmount() {
        return lateFeeAmount;
    }

    public void setLateFeeAmount(BigDecimal lateFeeAmount) {
        this.lateFeeAmount = lateFeeAmount;
    }

    public Integer getLateFeeGraceDays() {
        return lateFeeGraceDays;
    }

    public void setLateFeeGraceDays(Integer lateFeeGraceDays) {
        this.lateFeeGraceDays = lateFeeGraceDays;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
