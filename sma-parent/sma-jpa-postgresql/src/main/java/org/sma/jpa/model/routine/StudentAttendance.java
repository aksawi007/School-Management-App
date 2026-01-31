package org.sma.jpa.model.routine;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.sma.jpa.model.BaseEntity;
import org.sma.jpa.model.studentmgmt.StudentProfile;

import javax.persistence.*;

/**
 * Student Attendance - Records student presence for each class session
 * Linked to daily class session for complete tracking
 */
@Entity
@Table(name = "student_attendance", schema = "sma_admin",
        indexes = {
            @Index(name = "idx_attendance_session", columnList = "class_session_id"),
            @Index(name = "idx_attendance_student", columnList = "student_id, class_session_id"),
            @Index(name = "idx_attendance_status", columnList = "attendance_status")
        })
public class StudentAttendance extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_session_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private DailyClassSession classSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private StudentProfile student;

    @Column(name = "attendance_status", nullable = false, length = 20)
    private String attendanceStatus; // PRESENT, ABSENT, LATE, EXCUSED, SICK_LEAVE

    @Column(name = "marked_at")
    private java.time.LocalDateTime markedAt;

    @Column(name = "marked_by")
    private Long markedBy; // Staff ID who marked attendance

    @Column(name = "remarks", length = 500)
    private String remarks;

    // Constructors
    public StudentAttendance() {}

    // Getters and Setters
    public DailyClassSession getClassSession() {
        return classSession;
    }

    public void setClassSession(DailyClassSession classSession) {
        this.classSession = classSession;
    }

    public StudentProfile getStudent() {
        return student;
    }

    public void setStudent(StudentProfile student) {
        this.student = student;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public java.time.LocalDateTime getMarkedAt() {
        return markedAt;
    }

    public void setMarkedAt(java.time.LocalDateTime markedAt) {
        this.markedAt = markedAt;
    }

    public Long getMarkedBy() {
        return markedBy;
    }

    public void setMarkedBy(Long markedBy) {
        this.markedBy = markedBy;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
