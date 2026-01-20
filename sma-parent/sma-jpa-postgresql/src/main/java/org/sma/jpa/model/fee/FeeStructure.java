package org.sma.jpa.model.fee;

import org.sma.jpa.model.BaseEntity;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.school.AcademicYear;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Entity for Fee Structure - Defines fee categories and amounts
 */
@Entity
@Table(name = "fee_structure", schema = "sma_admin")
public class FeeStructure extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private SchoolProfile school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @Column(name = "structure_code", nullable = false, length = 50)
    private String structureCode;

    @Column(name = "structure_name", nullable = false, length = 150)
    private String structureName;

    @Column(name = "applicable_for", length = 50)
    private String applicableFor; // ALL, SPECIFIC_CLASS, SPECIFIC_CATEGORY

    @Column(name = "structure_status", length = 20)
    private String structureStatus; // DRAFT, ACTIVE, INACTIVE

    @Column(name = "description", length = 500)
    private String description;

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

    public String getStructureCode() {
        return structureCode;
    }

    public void setStructureCode(String structureCode) {
        this.structureCode = structureCode;
    }

    public String getStructureName() {
        return structureName;
    }

    public void setStructureName(String structureName) {
        this.structureName = structureName;
    }

    public String getApplicableFor() {
        return applicableFor;
    }

    public void setApplicableFor(String applicableFor) {
        this.applicableFor = applicableFor;
    }

    public String getStructureStatus() {
        return structureStatus;
    }

    public void setStructureStatus(String structureStatus) {
        this.structureStatus = structureStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
