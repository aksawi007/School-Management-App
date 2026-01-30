package org.sma.admin.core.app.controller;

import io.swagger.annotations.Api;
import org.sma.admin.core.app.model.request.StudentClassSectionRequest;
import org.sma.admin.core.app.model.response.StudentClassSectionResponse;
import org.sma.admin.core.app.service.StudentClassSectionBusinessService;
import org.sma.platform.core.annotation.APIController;
import org.sma.platform.core.exception.SmaException;
import org.sma.platform.core.restcontroller.ApiRestServiceBinding;
import org.sma.platform.core.service.ServiceRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Student Class Section Controller
 * Manages student assignments to classes and sections
 */
@APIController
@RequestMapping("/student-class-section")
@Api(tags = "Student Class Section API")
public class StudentClassSectionController extends ApiRestServiceBinding {

    @Autowired
    private StudentClassSectionBusinessService studentClassSectionBusinessService;

    @PostMapping("/assign")
    ResponseEntity<?> assignStudentToClassSection(
            @RequestBody StudentClassSectionRequest request) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("AssignStudentToClassSection",
                request.getStudentId().toString(), request.getStudentId().toString());

        try {
            StudentClassSectionResponse response = studentClassSectionBusinessService
                    .assignStudentToClassSection(context, request);
            return processResponse(context, response);
        } catch (SmaException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "Business Rule Violation");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PutMapping("/update/{mappingId}")
    ResponseEntity<?> updateStudentClassSection(
            @PathVariable("mappingId") Long mappingId,
            @RequestBody StudentClassSectionRequest request) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("UpdateStudentClassSection",
                mappingId.toString(), mappingId.toString());

        try {
            StudentClassSectionResponse response = studentClassSectionBusinessService
                    .updateStudentClassSection(context, mappingId, request);
            return processResponse(context, response);
        } catch (SmaException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "Business Rule Violation");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/students")
    ResponseEntity<List<StudentClassSectionResponse>> getStudentsByClassAndSection(
            @RequestParam("academicYearId") Long academicYearId,
            @RequestParam("classId") Long classId,
            @RequestParam(value = "sectionId", required = false) Long sectionId) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("GetStudentsByClassAndSection",
                classId.toString(), classId.toString());

        try {
            List<StudentClassSectionResponse> response = studentClassSectionBusinessService
                    .getStudentsByClassAndSection(context, academicYearId, classId, sectionId);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to fetch students: " + e.getMessage(), e);
        }
    }

    @GetMapping("/student/current")
    ResponseEntity<StudentClassSectionResponse> getStudentCurrentAssignment(
            @RequestParam("studentId") Long studentId,
            @RequestParam("academicYearId") Long academicYearId) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("GetStudentCurrentAssignment",
                studentId.toString(), studentId.toString());

        try {
            StudentClassSectionResponse response = studentClassSectionBusinessService
                    .getStudentCurrentAssignment(context, studentId, academicYearId);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to fetch student assignment: " + e.getMessage(), e);
        }
    }

    @GetMapping("/student/history/{studentId}")
    ResponseEntity<?> getStudentHistory(
            @PathVariable("studentId") Long studentId) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("GetStudentHistory",
                studentId.toString(), studentId.toString());

        try {
            List<StudentClassSectionResponse> response = studentClassSectionBusinessService
                    .getStudentHistory(context, studentId);
            return processResponse(context, response);
        } catch (SmaException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "Business Rule Violation");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @DeleteMapping("/deactivate/{mappingId}")
    ResponseEntity<?> deactivateStudentAssignment(
            @PathVariable("mappingId") Long mappingId) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("DeactivateStudentAssignment",
                mappingId.toString(), mappingId.toString());

        try {
            studentClassSectionBusinessService.deactivateStudentAssignment(context, mappingId);
            return ResponseEntity.ok("Student assignment deactivated successfully");
        } catch (SmaException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", "Business Rule Violation");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
