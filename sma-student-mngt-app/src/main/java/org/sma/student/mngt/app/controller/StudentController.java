package org.sma.student.mngt.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.sma.student.mngt.app.dto.CreateStudentRequest;
import org.sma.student.mngt.app.dto.StudentDetailsResponse;
import org.sma.jpa.model.studentmgmt.StudentProfile;
import org.sma.student.mngt.app.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/schools/{schoolId}/students")
@Api(tags = "StudentProfile Management", description = "APIs for StudentProfile lifecycle management")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping
    @ApiOperation(value = "Create new StudentProfile (admission)", notes = "Creates a new StudentProfile with guardians and addresses")
    public ResponseEntity<Map<String, Object>> createStudent(
            @PathVariable Long schoolId,
            @Valid @RequestBody CreateStudentRequest request) {
        
        request.setSchoolId(schoolId);
        StudentProfile StudentProfile = studentService.createStudent(request, "SYSTEM"); // TODO: Get from security context
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "StudentProfile created successfully");
        response.put("studentId", StudentProfile.getId());
        response.put("admissionNo", StudentProfile.getAdmissionNo());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{studentId}")
    @ApiOperation(value = "Get StudentProfile details", notes = "Returns complete StudentProfile profile with guardians, addresses, and enrollment history")
    public ResponseEntity<StudentDetailsResponse> getStudentDetails(
            @PathVariable Long schoolId,
            @PathVariable Long studentId) {
        
        StudentDetailsResponse response = studentService.getStudentDetails(schoolId, studentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @ApiOperation(value = "Search/List students", notes = "Search students with filters and pagination")
    public ResponseEntity<Page<StudentProfile>> searchStudents(
            @PathVariable Long schoolId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "admissionNo") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<StudentProfile> students = studentService.searchStudents(schoolId, search, status, pageable);
        return ResponseEntity.ok(students);
    }

    @PatchMapping("/{studentId}/status")
    @ApiOperation(value = "Update StudentProfile status", notes = "Change StudentProfile status (ACTIVE, INACTIVE, TRANSFERRED, etc.)")
    public ResponseEntity<Map<String, Object>> updateStudentStatus(
            @PathVariable Long schoolId,
            @PathVariable Long studentId,
            @RequestParam String status) {
        
        StudentProfile StudentProfile = studentService.updateStudentStatus(schoolId, studentId, status, "SYSTEM");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "StudentProfile status updated successfully");
        response.put("studentId", StudentProfile.getId());
        response.put("status", StudentProfile.getStatus());
        
        return ResponseEntity.ok(response);
    }
}


