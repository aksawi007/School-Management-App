package org.sma.jpa.model.routine;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.sma.jpa.model.BaseEntity;
import org.sma.jpa.model.master.ClassMaster;
import org.sma.jpa.model.master.SectionMaster;
import org.sma.jpa.model.master.SubjectMaster;
import org.sma.jpa.model.school.AcademicYear;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.staff.Staff;

import javax.persistence.*;

/**
 * Class Routine Master - Weekly template for each class/section
 * Stores the standard weekly routine that repeats each week
 * Only changes when the base schedule needs to be updated
 */
@Entity
@Table(name = "class_routine_master", schema = "sma_admin",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"school_id", "academic_year_id", "class_id", "section_id", "day_of_week", "time_slot_id"},
                name = "uk_routine_master_schedule"
        ),
        indexes = {
            @Index(name = "idx_routine_master_class", columnList = "school_id, academic_year_id, class_id, section_id"),
            @Index(name = "idx_routine_master_day", columnList = "day_of_week"),
            @Index(name = "idx_routine_master_teacher", columnList = "teacher_id")
        })
public class ClassRoutineMaster extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private SchoolProfile school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private AcademicYear academicYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ClassMaster classMaster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private SectionMaster section;

    @Column(name = "day_of_week", nullable = false, length = 10)
    private String dayOfWeek; // MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_slot_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private RoutineTimeSlot timeSlot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private SubjectMaster subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Staff teacher;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "remarks", length = 500)
    private String remarks;

    // Constructors
    public ClassRoutineMaster() {}

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

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public RoutineTimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(RoutineTimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public SubjectMaster getSubject() {
        return subject;
    }

    public void setSubject(SubjectMaster subject) {
        this.subject = subject;
    }

    public Staff getTeacher() {
        return teacher;
    }

    public void setTeacher(Staff teacher) {
        this.teacher = teacher;
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
