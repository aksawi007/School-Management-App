package org.sma.admin.core.app.controller;

import org.sma.admin.core.app.model.request.BulkAttendanceRequest;
import org.sma.admin.core.app.service.StudentAttendanceBusinessService;
import org.sma.jpa.model.routine.StudentAttendance;
import org.sma.platform.core.exception.SmaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schools/{schoolId}/attendance")
@CrossOrigin
public class StudentAttendanceController {

    @Autowired
    private StudentAttendanceBusinessService studentAttendanceService;

    @PostMapping("/bulk")
    public ResponseEntity<?> markBulkAttendance(@PathVariable Long schoolId,
                                               @RequestBody BulkAttendanceRequest request) {
        try {
            List<StudentAttendance> attendance = studentAttendanceService.markBulkAttendance(request);
            return ResponseEntity.ok(attendance);
        } catch (SmaException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("error", "Business Rule Violation");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<StudentAttendance>> getSessionAttendance(
            @PathVariable Long schoolId,
            @PathVariable Long sessionId) {
        List<StudentAttendance> attendance = studentAttendanceService.getSessionAttendance(sessionId);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/student/{studentId}/history")
    public ResponseEntity<List<StudentAttendance>> getStudentAttendanceHistory(
            @PathVariable Long schoolId,
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<StudentAttendance> attendance = studentAttendanceService.getStudentAttendanceHistory(
                studentId, startDate, endDate);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/class/date")
    public ResponseEntity<List<StudentAttendance>> getClassAttendanceForDate(
            @PathVariable Long schoolId,
            @RequestParam Long academicYearId,
            @RequestParam Long classId,
            @RequestParam Long sectionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<StudentAttendance> attendance = studentAttendanceService.getClassAttendanceForDate(
                schoolId, academicYearId, classId, sectionId, date);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/student/{studentId}/count")
    public ResponseEntity<Map<String, Long>> getStudentAttendanceCount(
            @PathVariable Long schoolId,
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String status) {
        Long count = studentAttendanceService.countStudentAttendance(studentId, startDate, endDate, status);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/class/summary")
    public ResponseEntity<List<Object[]>> getAttendanceSummary(
            @PathVariable Long schoolId,
            @RequestParam Long academicYearId,
            @RequestParam Long classId,
            @RequestParam Long sectionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Object[]> summary = studentAttendanceService.getAttendanceSummary(
                schoolId, academicYearId, classId, sectionId, startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    @PatchMapping("/{attendanceId}")
    public ResponseEntity<?> updateAttendance(@PathVariable Long schoolId,
                                             @PathVariable Long attendanceId,
                                             @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            String remarks = request.get("remarks");
            StudentAttendance updated = studentAttendanceService.updateAttendance(
                    attendanceId, status, remarks);
            return ResponseEntity.ok(updated);
        } catch (SmaException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("error", "Business Rule Violation");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/{attendanceId}")
    public ResponseEntity<?> getAttendanceById(@PathVariable Long schoolId,
                                              @PathVariable Long attendanceId) {
        try {
            StudentAttendance attendance = studentAttendanceService.getAttendanceById(attendanceId);
            return ResponseEntity.ok(attendance);
        } catch (SmaException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("error", "Not Found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}
