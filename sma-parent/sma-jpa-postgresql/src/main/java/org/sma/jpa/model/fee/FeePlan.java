package org.sma.jpa.model.fee;

import org.sma.jpa.model.BaseEntity;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.school.AcademicYear;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Entity for Fee Plan - Year + Category Level
 */
@Entity
@Table(name = "fee_plan", schema = "sma_admin",
        uniqueConstraints = @UniqueConstraint(columnNames = {"school_id", "academic_year_id", "category_id"}))
public class FeePlan extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private SchoolProfile school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private FeeCategory category;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "frequency", nullable = false, length = 20)
    private String frequency; // ONCE, MONTHLY, QUARTERLY, HALF_YEARLY

    @Column(name = "installments_count", nullable = false)
    private Integer installmentsCount;

    @Column(name = "status", length = 20)
    private String status = "ACTIVE"; // ACTIVE, INACTIVE

    // Getters and Setters
    public SchoolProfile getSchool() {
        return school;
    }

    public void setSchool(SchoolProfile school) {
        this.school = school;
    }

    public AcademicYear getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(AcademicYear academicYear) {
        this.academicYear = academicYear;
    }

    public FeeCategory getCategory() {
        return category;
    }

    public void setCategory(FeeCategory category) {
        this.category = category;
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
}
