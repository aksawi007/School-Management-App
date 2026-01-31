package org.sma.jpa.model.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.sma.jpa.model.BaseEntity;
import org.sma.jpa.model.master.SubjectMaster;
import org.sma.jpa.model.school.SchoolProfile;

import javax.persistence.*;

/**
 * Staff-Subject Mapping - Defines which subjects a teacher can teach
 * Used to show only qualified teachers in routine builder
 */
@Entity
@Table(name = "staff_subject_mapping", schema = "sma_admin",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"school_id", "staff_id", "subject_id"},
                name = "uk_staff_subject"
        ),
        indexes = {
            @Index(name = "idx_staff_subject_staff", columnList = "staff_id"),
            @Index(name = "idx_staff_subject_subject", columnList = "subject_id")
        })
public class StaffSubjectMapping extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private SchoolProfile school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Staff staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private SubjectMaster subject;

    @Column(name = "proficiency_level", length = 20)
    private String proficiencyLevel; // EXPERT, QUALIFIED, SUBSTITUTE

    @Column(name = "can_teach_primary")
    private Boolean canTeachPrimary = true;

    @Column(name = "can_teach_secondary")
    private Boolean canTeachSecondary = true;

    @Column(name = "can_teach_higher_secondary")
    private Boolean canTeachHigherSecondary = true;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "remarks", length = 500)
    private String remarks;

    // Constructors
    public StaffSubjectMapping() {}

    // Getters and Setters
    public SchoolProfile getSchool() {
        return school;
    }

    public void setSchool(SchoolProfile school) {
        this.school = school;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public SubjectMaster getSubject() {
        return subject;
    }

    public void setSubject(SubjectMaster subject) {
        this.subject = subject;
    }

    public String getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(String proficiencyLevel) {
        this.proficiencyLevel = proficiencyLevel;
    }

    public Boolean getCanTeachPrimary() {
        return canTeachPrimary;
    }

    public void setCanTeachPrimary(Boolean canTeachPrimary) {
        this.canTeachPrimary = canTeachPrimary;
    }

    public Boolean getCanTeachSecondary() {
        return canTeachSecondary;
    }

    public void setCanTeachSecondary(Boolean canTeachSecondary) {
        this.canTeachSecondary = canTeachSecondary;
    }

    public Boolean getCanTeachHigherSecondary() {
        return canTeachHigherSecondary;
    }

    public void setCanTeachHigherSecondary(Boolean canTeachHigherSecondary) {
        this.canTeachHigherSecondary = canTeachHigherSecondary;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
