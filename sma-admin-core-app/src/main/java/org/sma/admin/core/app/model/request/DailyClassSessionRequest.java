package org.sma.admin.core.app.model.request;

import java.time.LocalDate;

public class DailyClassSessionRequest {
    private Long schoolId;
    private Long academicYearId;
    private Long classId;
    private Long sectionId;
    private LocalDate sessionDate;
    private Long timeSlotId;
    private Long routineMasterId; // Reference to master routine
    private Long subjectOverride; // Only if different from master
    private Long teacherOverride; // Only if different from master
    private Long actualTeacherId; // Who actually conducted
    private String sessionStatus; // SCHEDULED, CONDUCTED, CANCELLED, POSTPONED
    private String remarks;

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public Long getAcademicYearId() {
        return academicYearId;
    }

    public void setAcademicYearId(Long academicYearId) {
        this.academicYearId = academicYearId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public Long getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlotId(Long timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

    public Long getRoutineMasterId() {
        return routineMasterId;
    }

    public void setRoutineMasterId(Long routineMasterId) {
        this.routineMasterId = routineMasterId;
    }

    public Long getSubjectOverride() {
        return subjectOverride;
    }

    public void setSubjectOverride(Long subjectOverride) {
        this.subjectOverride = subjectOverride;
    }

    public Long getTeacherOverride() {
        return teacherOverride;
    }

    public void setTeacherOverride(Long teacherOverride) {
        this.teacherOverride = teacherOverride;
    }

    public Long getActualTeacherId() {
        return actualTeacherId;
    }

    public void setActualTeacherId(Long actualTeacherId) {
        this.actualTeacherId = actualTeacherId;
    }

    public String getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(String sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
