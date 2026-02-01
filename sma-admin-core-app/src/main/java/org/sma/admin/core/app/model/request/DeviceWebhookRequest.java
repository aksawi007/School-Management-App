package org.sma.admin.core.app.model.request;

import java.time.LocalDateTime;
import java.util.List;

public class DeviceWebhookRequest {
    private String deviceId;
    private String deviceTxnId; // optional, for idempotency
    private LocalDateTime timestamp;
    private List<DeviceAttendanceItem> items;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceTxnId() {
        return deviceTxnId;
    }

    public void setDeviceTxnId(String deviceTxnId) {
        this.deviceTxnId = deviceTxnId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<DeviceAttendanceItem> getItems() {
        return items;
    }

    public void setItems(List<DeviceAttendanceItem> items) {
        this.items = items;
    }

    public static class DeviceAttendanceItem {
        private String targetType; // STUDENT or STAFF
        private Long targetId; // studentId or staffId
        private Long sessionId; // optional
        private String attendanceStatus;
        private String remarks;

        public String getTargetType() {
            return targetType;
        }

        public void setTargetType(String targetType) {
            this.targetType = targetType;
        }

        public Long getTargetId() {
            return targetId;
        }

        public void setTargetId(Long targetId) {
            this.targetId = targetId;
        }

        public Long getSessionId() {
            return sessionId;
        }

        public void setSessionId(Long sessionId) {
            this.sessionId = sessionId;
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
