package org.sma.admin.core.app.model.request;

import java.util.List;

public class StaffBulkAttendanceRequest {
    private Long sessionId;
    private List<StaffAttendanceItem> attendanceList;
    private Long markedBy; // Staff ID

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public List<StaffAttendanceItem> getAttendanceList() {
        return attendanceList;
    }

    public void setAttendanceList(List<StaffAttendanceItem> attendanceList) {
        this.attendanceList = attendanceList;
    }

    public Long getMarkedBy() {
        return markedBy;
    }

    public void setMarkedBy(Long markedBy) {
        this.markedBy = markedBy;
    }

    public static class StaffAttendanceItem {
        private Long staffId;
        private String attendanceStatus;
        private String remarks;

        public Long getStaffId() {
            return staffId;
        }

        public void setStaffId(Long staffId) {
            this.staffId = staffId;
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
