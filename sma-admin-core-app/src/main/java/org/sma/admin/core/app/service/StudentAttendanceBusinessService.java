package org.sma.admin.core.app.service;

import org.sma.admin.core.app.model.request.BulkAttendanceRequest;
import org.sma.jpa.model.staff.Staff;
import org.sma.jpa.model.studentmgmt.StudentProfile;
import org.sma.jpa.model.routine.DailyClassSession;
import org.sma.jpa.model.routine.StudentAttendance;
import org.sma.jpa.repository.staff.StaffRepository;
import org.sma.jpa.repository.studentmgmt.StudentProfileRepository;
import org.sma.jpa.repository.routine.DailyClassSessionRepository;
import org.sma.jpa.repository.routine.StudentAttendanceRepository;
import org.sma.platform.core.exception.SmaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentAttendanceBusinessService {

    @Autowired
    private StudentAttendanceRepository studentAttendanceRepository;

    @Autowired
    private DailyClassSessionRepository dailyClassSessionRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private DailyClassSessionBusinessService dailyClassSessionBusinessService;

    @Transactional
    public List<StudentAttendance> markBulkAttendance(BulkAttendanceRequest request) throws SmaException {
        // Validate session
        DailyClassSession session = dailyClassSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new SmaException("Session not found"));

        // Validate staff
        Staff markedBy = staffRepository.findById(request.getMarkedBy())
                .orElseThrow(() -> new SmaException("Staff not found"));

        List<StudentAttendance> attendanceRecords = new ArrayList<>();
        LocalDateTime markedAt = LocalDateTime.now();

        for (BulkAttendanceRequest.StudentAttendanceItem item : request.getAttendanceList()) {
            // Validate student
            StudentProfile student = studentProfileRepository.findById(item.getStudentId())
                    .orElseThrow(() -> new SmaException("Student not found: " + item.getStudentId()));

            // Check if attendance already exists
            StudentAttendance attendance = studentAttendanceRepository
                    .findBySessionAndStudent(request.getSessionId(), item.getStudentId())
                    .orElse(new StudentAttendance());

            // Set or update fields
            attendance.setClassSession(session);
            attendance.setStudent(student);
            attendance.setAttendanceStatus(item.getAttendanceStatus());
            attendance.setMarkedAt(markedAt);
            attendance.setMarkedBy(markedBy.getId());
            attendance.setRemarks(item.getRemarks());

            attendanceRecords.add(studentAttendanceRepository.save(attendance));
        }

        // Mark session as attendance completed
        dailyClassSessionBusinessService.markAttendanceCompleted(request.getSessionId());

        return attendanceRecords;
    }

    public List<StudentAttendance> getSessionAttendance(Long sessionId) {
        return studentAttendanceRepository.findBySessionId(sessionId);
    }

    public List<StudentAttendance> getStudentAttendanceHistory(Long studentId, LocalDate startDate, LocalDate endDate) {
        return studentAttendanceRepository.findStudentAttendanceHistory(studentId, startDate, endDate);
    }

    public List<StudentAttendance> getClassAttendanceForDate(Long schoolId, Long academicYearId, 
                                                             Long classId, Long sectionId, LocalDate date) {
        return studentAttendanceRepository.findClassAttendanceForDate(sectionId, schoolId, date);
    }

    public Long countStudentAttendance(Long studentId, LocalDate startDate, LocalDate endDate, String status) {
        return studentAttendanceRepository.countStudentAttendanceByStatus(studentId, startDate, endDate, status);
    }

    /**
     * Get attendance summary for a class
     * Returns list of Object[] with [attendanceStatus, count]
     */
    public List<Object[]> getAttendanceSummary(Long schoolId, Long academicYearId, 
                                               Long classId, Long sectionId, 
                                               LocalDate startDate, LocalDate endDate) {
        return studentAttendanceRepository.getAttendanceSummaryForClass(
                sectionId, schoolId, startDate, endDate);
    }

    @Transactional
    public StudentAttendance updateAttendance(Long attendanceId, String status, String remarks) throws SmaException {
        StudentAttendance attendance = studentAttendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new SmaException("Attendance record not found"));

        attendance.setAttendanceStatus(status);
        attendance.setRemarks(remarks);

        return studentAttendanceRepository.save(attendance);
    }

    public StudentAttendance getAttendanceById(Long attendanceId) throws SmaException {
        return studentAttendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new SmaException("Attendance record not found"));
    }
}
