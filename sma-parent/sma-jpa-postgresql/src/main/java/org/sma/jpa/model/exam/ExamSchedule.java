package org.sma.jpa.model.exam;

import org.sma.jpa.model.BaseEntity;
import org.sma.jpa.model.master.ClassMaster;
import org.sma.jpa.model.master.SubjectMaster;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entity for Exam Schedule - Individual subject exam details
 */
@Entity
@Table(name = "exam_schedule", schema = "sma_admin")
public class ExamSchedule extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassMaster classMaster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private SubjectMaster subject;

    @Column(name = "exam_date", nullable = false)
    private LocalDate examDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "max_marks", nullable = false)
    private Integer maxMarks;

    @Column(name = "pass_marks", nullable = false)
    private Integer passMarks;

    @Column(name = "room_number", length = 50)
    private String roomNumber;

    @Column(name = "remarks", length = 500)
    private String remarks;

    // Getters and Setters
    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public ClassMaster getClassMaster() {
        return classMaster;
    }

    public void setClassMaster(ClassMaster classMaster) {
        this.classMaster = classMaster;
    }

    public SubjectMaster getSubject() {
        return subject;
    }

    public void setSubject(SubjectMaster subject) {
        this.subject = subject;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
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

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
