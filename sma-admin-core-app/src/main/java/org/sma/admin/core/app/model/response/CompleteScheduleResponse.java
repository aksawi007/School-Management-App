package org.sma.admin.core.app.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.sma.jpa.model.master.SubjectMaster;
import org.sma.jpa.model.routine.RoutineTimeSlot;
import org.sma.jpa.model.staff.Staff;

import java.time.LocalDate;

/**
 * Response DTO for complete schedule that combines master routine with daily sessions
 * Clearly indicates whether a daily session exists or not
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompleteScheduleResponse {
    
    // Session ID - null if no session created yet
    private Long sessionId;
    
    // Routine Master ID - always present (template)
    private Long routineMasterId;
    
    // Time slot
    private RoutineTimeSlot timeSlot;
    
    // Subject (from master or override)
    private SubjectMaster subject;
    private SubjectMaster effectiveSubject;
    private SubjectMaster subjectOverride;
    
    // Teacher (from master or override)
    private Staff teacher;
    private Staff effectiveTeacher;
    private Staff teacherOverride;
    private Staff actualTeacher;
    
    // Session details
    private LocalDate sessionDate;
    private String sessionStatus; // SCHEDULED, ONGOING, COMPLETED, CANCELLED
    private Boolean isAttendanceMarked;
    private String dayOfWeek;
    private String remarks;
    
    // Additional context
    private Object academicYear;
    private Object classMaster;
    private Object section;
    private Object routineMaster; // Full routine master for reference
    
    // Flag to indicate if this is from a created session or just master routine
    private Boolean hasSession;

    public CompleteScheduleResponse() {
    }

    // Getters and Setters
    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getRoutineMasterId() {
        return routineMasterId;
    }

    public void setRoutineMasterId(Long routineMasterId) {
        this.routineMasterId = routineMasterId;
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

    public SubjectMaster getEffectiveSubject() {
        return effectiveSubject;
    }

    public void setEffectiveSubject(SubjectMaster effectiveSubject) {
        this.effectiveSubject = effectiveSubject;
    }

    public SubjectMaster getSubjectOverride() {
        return subjectOverride;
    }

    public void setSubjectOverride(SubjectMaster subjectOverride) {
        this.subjectOverride = subjectOverride;
    }

    public Staff getTeacher() {
        return teacher;
    }

    public void setTeacher(Staff teacher) {
        this.teacher = teacher;
    }

    public Staff getEffectiveTeacher() {
        return effectiveTeacher;
    }

    public void setEffectiveTeacher(Staff effectiveTeacher) {
        this.effectiveTeacher = effectiveTeacher;
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

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
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

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Object getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(Object academicYear) {
        this.academicYear = academicYear;
    }

    public Object getClassMaster() {
        return classMaster;
    }

    public void setClassMaster(Object classMaster) {
        this.classMaster = classMaster;
    }

    public Object getSection() {
        return section;
    }

    public void setSection(Object section) {
        this.section = section;
    }

    public Object getRoutineMaster() {
        return routineMaster;
    }

    public void setRoutineMaster(Object routineMaster) {
        this.routineMaster = routineMaster;
    }

    public Boolean getHasSession() {
        return hasSession;
    }

    public void setHasSession(Boolean hasSession) {
        this.hasSession = hasSession;
    }
}
