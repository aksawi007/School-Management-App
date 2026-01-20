package org.sma.jpa.model.exam;

import org.sma.jpa.model.BaseEntity;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.school.AcademicYear;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entity for Exam Schedule
 */
@Entity
@Table(name = "exam", schema = "sma_admin")
public class Exam extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private SchoolProfile school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @Column(name = "exam_code", nullable = false, length = 50)
    private String examCode;

    @Column(name = "exam_name", nullable = false, length = 150)
    private String examName;

    @Column(name = "exam_type", length = 30)
    private String examType; // UNIT_TEST, MONTHLY, QUARTERLY, HALF_YEARLY, ANNUAL, FINAL

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "exam_status", length = 20)
    private String examStatus; // SCHEDULED, ONGOING, COMPLETED, CANCELLED

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

    public String getExamCode() {
        return examCode;
    }

    public void setExamCode(String examCode) {
        this.examCode = examCode;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getExamStatus() {
        return examStatus;
    }

    public void setExamStatus(String examStatus) {
        this.examStatus = examStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
