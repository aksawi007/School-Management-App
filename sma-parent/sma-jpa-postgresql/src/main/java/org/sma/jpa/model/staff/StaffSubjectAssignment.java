package org.sma.jpa.model.staff;

import org.sma.jpa.model.BaseEntity;
import org.sma.jpa.model.school.AcademicYear;
import org.sma.jpa.model.master.ClassMaster;
import org.sma.jpa.model.master.SectionMaster;
import org.sma.jpa.model.master.SubjectMaster;

import javax.persistence.*;

/**
 * Entity for Staff Subject Assignment - Maps which subjects a teacher teaches
 */
@Entity
@Table(name = "staff_subject_assignment", schema = "sma_admin")
public class StaffSubjectAssignment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private SubjectMaster subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassMaster classMaster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private SectionMaster section;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @Column(name = "is_class_teacher")
    private Boolean isClassTeacher = false;

    @Column(name = "remarks", length = 500)
    private String remarks;

    // Getters and Setters
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

    public AcademicYear getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(AcademicYear academicYear) {
        this.academicYear = academicYear;
    }

    public Boolean getIsClassTeacher() {
        return isClassTeacher;
    }

    public void setIsClassTeacher(Boolean isClassTeacher) {
        this.isClassTeacher = isClassTeacher;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
