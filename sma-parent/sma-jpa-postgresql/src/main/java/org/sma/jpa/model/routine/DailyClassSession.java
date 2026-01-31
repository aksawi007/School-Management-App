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
import java.time.LocalDate;

/**
 * Daily Class Session - Tracks actual daily schedule
 * Created only when:
 * 1. Changes from master routine (substitute teacher, subject change)
 * 2. Attendance needs to be recorded
 * 3. Session status needs tracking
 * 
 * If no changes and no attendance, no record needed (uses master routine)
 */
@Entity
@Table(name = "daily_class_session", schema = "sma_admin",
        indexes = {
            @Index(name = "idx_daily_session_date", columnList = "session_date, school_id"),
            @Index(name = "idx_daily_session_class", columnList = "class_id, section_id, session_date"),
            @Index(name = "idx_daily_session_teacher", columnList = "actual_teacher_id, session_date"),
            @Index(name = "idx_daily_session_master", columnList = "routine_master_id")
        })
public class DailyClassSession extends BaseEntity {

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

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_slot_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private RoutineTimeSlot timeSlot;

    // Reference to master routine (null if ad-hoc session)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_master_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ClassRoutineMaster routineMaster;

    // Override fields - only populated if different from master
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private SubjectMaster subjectOverride;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Staff teacherOverride;

    // Actual teacher who conducted the class (for tracking substitutions)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actual_teacher_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Staff actualTeacher;

    @Column(name = "session_status", length = 20)
    private String sessionStatus; // SCHEDULED, CONDUCTED, CANCELLED, POSTPONED

    @Column(name = "is_attendance_marked")
    private Boolean isAttendanceMarked = false;

    @Column(name = "remarks", length = 500)
    private String remarks;

    // Constructors
    public DailyClassSession() {}

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

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public RoutineTimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(RoutineTimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public ClassRoutineMaster getRoutineMaster() {
        return routineMaster;
    }

    public void setRoutineMaster(ClassRoutineMaster routineMaster) {
        this.routineMaster = routineMaster;
    }

    public SubjectMaster getSubjectOverride() {
        return subjectOverride;
    }

    public void setSubjectOverride(SubjectMaster subjectOverride) {
        this.subjectOverride = subjectOverride;
    }

    public Staff getTeacherOverride() {
        return teacherOverride;
    }

    public void setTeacherOverride(Staff teacherOverride) {
        this.teacherOverride = teacherOverride;
    }

    public Staff getActualTeacher() {
        return actualTeacher;
    }

    public void setActualTeacher(Staff actualTeacher) {
        this.actualTeacher = actualTeacher;
    }

    public String getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(String sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public Boolean getIsAttendanceMarked() {
        return isAttendanceMarked;
    }

    public void setIsAttendanceMarked(Boolean isAttendanceMarked) {
        this.isAttendanceMarked = isAttendanceMarked;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * Get the effective subject (override if exists, otherwise from master)
     */
    public SubjectMaster getEffectiveSubject() {
        if (subjectOverride != null) {
            return subjectOverride;
        }
        return routineMaster != null ? routineMaster.getSubject() : null;
    }

    /**
     * Get the effective teacher (override if exists, otherwise from master)
     */
    public Staff getEffectiveTeacher() {
        if (teacherOverride != null) {
            return teacherOverride;
        }
        return routineMaster != null ? routineMaster.getTeacher() : null;
    }
}
