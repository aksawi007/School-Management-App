package org.sma.admin.core.app.model.request;

import java.util.List;

public class BulkAttendanceRequest {
    private Long sessionId;
    private List<StudentAttendanceItem> attendanceList;
    private Long markedBy; // Staff ID

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public List<StudentAttendanceItem> getAttendanceList() {
        return attendanceList;
    }

    public void setAttendanceList(List<StudentAttendanceItem> attendanceList) {
        this.attendanceList = attendanceList;
    }

    public Long getMarkedBy() {
        return markedBy;
    }

    public void setMarkedBy(Long markedBy) {
        this.markedBy = markedBy;
    }

    public static class StudentAttendanceItem {
        private Long studentId;
        private String attendanceStatus; // PRESENT, ABSENT, LATE, EXCUSED, SICK_LEAVE
        private String remarks;

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public String getAttendanceStatus() {
            return attendanceStatus;
        }

        public void setAttendanceStatus(String attendanceStatus) {
            this.attendanceStatus = attendanceStatus;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }
    }
}
