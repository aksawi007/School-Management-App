package org.sma.admin.core.app.service;

import org.sma.admin.core.app.model.request.StaffBulkAttendanceRequest;
import org.sma.jpa.model.routine.DailyClassSession;
import org.sma.jpa.model.routine.StaffAttendance;
import org.sma.jpa.model.staff.Staff;
import org.sma.jpa.repository.routine.DailyClassSessionRepository;
import org.sma.jpa.repository.routine.StaffAttendanceRepository;
import org.sma.jpa.repository.staff.StaffRepository;
import org.sma.platform.core.exception.SmaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StaffAttendanceBusinessService {

    @Autowired
    private StaffAttendanceRepository staffAttendanceRepository;

    @Autowired
    private DailyClassSessionRepository dailyClassSessionRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private DailyClassSessionBusinessService dailyClassSessionBusinessService;

    @Transactional
    public List<StaffAttendance> markBulkAttendance(StaffBulkAttendanceRequest request) throws SmaException {
        DailyClassSession session = null;
        if (request.getSessionId() != null) {
            session = dailyClassSessionRepository.findById(request.getSessionId())
                    .orElseThrow(() -> new SmaException("Session not found"));
        }

        List<StaffAttendance> attendanceRecords = new ArrayList<>();
        LocalDateTime markedAt = LocalDateTime.now();

        for (StaffBulkAttendanceRequest.StaffAttendanceItem item : request.getAttendanceList()) {
            Staff staff = staffRepository.findById(item.getStaffId())
                    .orElseThrow(() -> new SmaException("Staff not found: " + item.getStaffId()));

            StaffAttendance attendance = staffAttendanceRepository
                    .findBySessionAndStaff(request.getSessionId(), item.getStaffId())
                    .orElse(new StaffAttendance());

            attendance.setClassSession(session);
            attendance.setStaff(staff);
            attendance.setAttendanceStatus(item.getAttendanceStatus());
            attendance.setMarkedAt(markedAt);
            attendance.setMarkedBy(request.getMarkedBy());
            attendance.setRemarks(item.getRemarks());

            attendanceRecords.add(staffAttendanceRepository.save(attendance));
        }

        if (request.getSessionId() != null) {
            dailyClassSessionBusinessService.markAttendanceCompleted(request.getSessionId());
        }

        return attendanceRecords;
    }
}
