package org.sma.jpa.model.student;

import org.sma.jpa.model.BaseEntity;
import org.sma.jpa.model.master.ClassMaster;
import org.sma.jpa.model.master.SectionMaster;
import org.sma.jpa.model.school.AcademicYear;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.studentmgmt.StudentProfile;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Entity for Student-Class-Section Mapping
 * Manages the relationship between students and their class/section assignments
 * Supports multiple academic years and student promotions
 */
@Entity
@Table(name = "student_class_section_mapping", schema = "sma_student",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"student_id", "academic_year_id", "is_active"},
                name = "uk_student_academic_year_active"
        ))
public class StudentClassSectionMapping extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfile student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private SchoolProfile school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassMaster classMaster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private SectionMaster section;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "roll_number", length = 20)
    private String rollNumber;

    @Column(name = "remarks", length = 500)
    private String remarks;

    // Constructors
    public StudentClassSectionMapping() {
    }

    // Getters and Setters
    public StudentProfile getStudent() {
        return student;
    }

    public void setStudent(StudentProfile student) {
        this.student = student;
    }

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

    public ClassMaster getClassMaster() {
        return classMaster;
    }

    public void setClassMaster(ClassMaster classMaster) {
        this.classMaster = classMaster;
    }

    public SectionMaster getSection() {
        return section;
    }

    public void setSection(SectionMaster section) {
        this.section = section;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
