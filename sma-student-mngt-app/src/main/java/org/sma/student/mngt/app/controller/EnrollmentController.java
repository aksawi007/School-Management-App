package org.sma.student.mngt.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.sma.student.mngt.app.dto.EnrollStudentRequest;
import org.sma.student.mngt.app.dto.EnrollmentDto;
import org.sma.jpa.model.studentmgmt.Enrollment;
import org.sma.student.mngt.app.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/schools/{schoolId}/students/{studentId}")
@Api(tags = "Student Enrollment", description = "APIs for student enrollment, promotion, and withdrawal")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping("/enrollments")
    @ApiOperation(value = "Enroll student", notes = "Enroll student in a class/section for an academic year")
    public ResponseEntity<Map<String, Object>> enrollStudent(
            @PathVariable UUID schoolId,
            @PathVariable UUID studentId,
            @Valid @RequestBody EnrollStudentRequest request) {
        
        request.setStudentId(studentId);
        Enrollment enrollment = enrollmentService.enrollStudent(schoolId, request, "SYSTEM");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Student enrolled successfully");
        response.put("enrollmentId", enrollment.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/enrollments")
    @ApiOperation(value = "Get enrollment history", notes = "Returns enrollment history for a student")
    public ResponseEntity<List<EnrollmentDto>> getEnrollmentHistory(
            @PathVariable UUID schoolId,
            @PathVariable UUID studentId) {
        
        List<EnrollmentDto> enrollments = enrollmentService.getStudentEnrollmentHistory(studentId);
        return ResponseEntity.ok(enrollments);
    }

    @PostMapping("/promote")
    @ApiOperation(value = "Promote student", notes = "Promote student to next academic year/class")
    public ResponseEntity<Map<String, Object>> promoteStudent(
            @PathVariable UUID schoolId,
            @PathVariable UUID studentId,
            @RequestParam UUID newAcademicYearId,
            @RequestParam UUID newClassId,
            @RequestParam(required = false) UUID newSectionId) {
        
        Enrollment enrollment = enrollmentService.promoteStudent(
                schoolId, studentId, newAcademicYearId, newClassId, newSectionId, "SYSTEM");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Student promoted successfully");
        response.put("enrollmentId", enrollment.getId());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdraw")
    @ApiOperation(value = "Withdraw student", notes = "Withdraw student from school")
    public ResponseEntity<Map<String, Object>> withdrawStudent(
            @PathVariable UUID schoolId,
            @PathVariable UUID studentId,
            @RequestParam(required = false) String reason) {
        
        enrollmentService.withdrawStudent(studentId, reason, "SYSTEM");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Student withdrawn successfully");
        
        return ResponseEntity.ok(response);
    }
}
