package org.sma.admin.core.app.controller;

import org.sma.admin.core.app.model.request.DailyClassSessionRequest;
import org.sma.admin.core.app.service.DailyClassSessionBusinessService;
import org.sma.jpa.model.routine.DailyClassSession;
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
@RequestMapping("/schools/{schoolId}/routine/sessions")
@CrossOrigin
public class DailyClassSessionController {

    @Autowired
    private DailyClassSessionBusinessService dailyClassSessionService;

    @PostMapping
    public ResponseEntity<?> createOrUpdateSession(@PathVariable Long schoolId,
                                                   @RequestBody DailyClassSessionRequest request) {
        try {
            request.setSchoolId(schoolId);
            DailyClassSession session = dailyClassSessionService.createOrUpdateSession(request);
            return ResponseEntity.ok(session);
        } catch (SmaException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("error", "Business Rule Violation");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/complete-schedule")
    public ResponseEntity<List<Object>> getCompleteSchedule(
            @PathVariable Long schoolId,
            @RequestParam Long academicYearId,
            @RequestParam Long classId,
            @RequestParam Long sectionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Object> schedule = dailyClassSessionService.getDayCompleteSchedule(
                schoolId, academicYearId, classId, sectionId, date);
        return ResponseEntity.ok(schedule);
    }

    @GetMapping("/teacher-schedule")
    public ResponseEntity<List<DailyClassSession>> getTeacherSchedule(
            @PathVariable Long schoolId,
            @RequestParam Long academicYearId,
            @RequestParam Long teacherId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<DailyClassSession> schedule = dailyClassSessionService.getTeacherScheduleForDate(
                schoolId, academicYearId, teacherId, date);
        return ResponseEntity.ok(schedule);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<DailyClassSession>> getSessionsInDateRange(
            @PathVariable Long schoolId,
            @RequestParam Long academicYearId,
            @RequestParam Long classId,
            @RequestParam Long sectionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<DailyClassSession> sessions = dailyClassSessionService.getSessionsInDateRange(
                schoolId, academicYearId, classId, sectionId, startDate, endDate);
        return ResponseEntity.ok(sessions);
    }

    @PatchMapping("/{sessionId}/status")
    public ResponseEntity<?> updateSessionStatus(@PathVariable Long schoolId,
                                                 @PathVariable Long sessionId,
                                                 @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            DailyClassSession updated = dailyClassSessionService.updateSessionStatus(sessionId, status);
            return ResponseEntity.ok(updated);
        } catch (SmaException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("error", "Business Rule Violation");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<?> getSessionById(@PathVariable Long schoolId, 
                                           @PathVariable Long sessionId) {
        try {
            DailyClassSession session = dailyClassSessionService.getSessionById(sessionId);
            return ResponseEntity.ok(session);
        } catch (SmaException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("error", "Not Found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}
