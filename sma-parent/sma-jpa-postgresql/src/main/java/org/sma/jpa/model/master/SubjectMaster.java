package org.sma.jpa.model.master;

import org.sma.jpa.model.BaseEntity;
import org.sma.jpa.model.school.SchoolProfile;

import javax.persistence.*;

/**
 * Entity for Subject - Master Data
 */
@Entity
@Table(name = "subject_master", schema = "sma_admin",
        uniqueConstraints = @UniqueConstraint(columnNames = {"school_id", "class_id", "subject_code"}))
public class SubjectMaster extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private SchoolProfile school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassMaster classMaster;

    @Column(name = "subject_code", nullable = false, length = 20)
    private String subjectCode;

    @Column(name = "subject_name", nullable = false, length = 150)
    private String subjectName;

    @Column(name = "subject_type", length = 30)
    private String subjectType; // CORE, ELECTIVE, OPTIONAL, EXTRA_CURRICULAR

    @Column(name = "is_mandatory")
    private Boolean isMandatory = true;

    @Column(name = "credits")
    private Integer credits;

    @Column(name = "max_marks")
    private Integer maxMarks;

    @Column(name = "pass_marks")
    private Integer passMarks;

    @Column(name = "description", length = 500)
    private String description;

    // Getters and Setters
    public SchoolProfile getSchool() {
        return school;
    }

    public void setSchool(SchoolProfile school) {
        this.school = school;
    }

    public ClassMaster getClassMaster() {
        return classMaster;
    }

    public void setClassMaster(ClassMaster classMaster) {
        this.classMaster = classMaster;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public Boolean getIsMandatory() {
        return isMandatory;
    }

    public void setIsMandatory(Boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public Integer getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(Integer maxMarks) {
        this.maxMarks = maxMarks;
    }

    public Integer getPassMarks() {
        return passMarks;
    }

    public void setPassMarks(Integer passMarks) {
        this.passMarks = passMarks;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
