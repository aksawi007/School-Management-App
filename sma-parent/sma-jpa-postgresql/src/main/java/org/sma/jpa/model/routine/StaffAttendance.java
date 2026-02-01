package org.sma.jpa.model.routine;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.sma.jpa.model.BaseEntity;
import org.sma.jpa.model.staff.Staff;

import javax.persistence.*;

@Entity
@Table(name = "staff_attendance", schema = "sma_admin",
        indexes = {
                @Index(name = "idx_staff_attendance_session", columnList = "class_session_id"),
                @Index(name = "idx_staff_attendance_staff", columnList = "staff_id, class_session_id"),
                @Index(name = "idx_staff_attendance_status", columnList = "attendance_status")
        })
public class StaffAttendance extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_session_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private DailyClassSession classSession; // optional for staff

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Staff staff;

    @Column(name = "attendance_status", nullable = false, length = 20)
    private String attendanceStatus;

    @Column(name = "marked_at")
    private java.time.LocalDateTime markedAt;

    @Column(name = "marked_by")
    private Long markedBy; // Staff ID who marked attendance

    @Column(name = "remarks", length = 500)
    private String remarks;

    public StaffAttendance() {}

    public DailyClassSession getClassSession() {
        return classSession;
    }

    public void setClassSession(DailyClassSession classSession) {
        this.classSession = classSession;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
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
