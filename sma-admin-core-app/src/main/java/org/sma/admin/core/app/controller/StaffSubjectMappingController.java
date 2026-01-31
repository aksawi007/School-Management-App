package org.sma.admin.core.app.controller;

import org.sma.admin.core.app.model.request.StaffSubjectMappingRequest;
import org.sma.admin.core.app.model.response.StaffSubjectMappingResponse;
import org.sma.admin.core.app.service.StaffSubjectMappingBusinessService;
import org.sma.platform.core.exception.SmaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for staff-subject mapping operations
 */
@RestController
@RequestMapping("/schools/{schoolId}/staff-subjects")
@CrossOrigin
public class StaffSubjectMappingController {

    @Autowired
    private StaffSubjectMappingBusinessService staffSubjectMappingService;

    /**
     * Create a new staff-subject mapping
     */
    @PostMapping
    public ResponseEntity<?> createMapping(@PathVariable Long schoolId,
                                          @RequestBody StaffSubjectMappingRequest request) {
        try {
            request.setSchoolId(schoolId);
            StaffSubjectMappingResponse response = staffSubjectMappingService.createMapping(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (SmaException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Update a staff-subject mapping
     */
    @PutMapping("/{mappingId}")
    public ResponseEntity<?> updateMapping(@PathVariable Long schoolId,
                                          @PathVariable Long mappingId,
                                          @RequestBody StaffSubjectMappingRequest request) {
        try {
            request.setSchoolId(schoolId);
            StaffSubjectMappingResponse response = staffSubjectMappingService.updateMapping(mappingId, request);
            return ResponseEntity.ok(response);
        } catch (SmaException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Delete a staff-subject mapping
     */
    @DeleteMapping("/{mappingId}")
    public ResponseEntity<?> deleteMapping(@PathVariable Long schoolId,
                                          @PathVariable Long mappingId) {
        try {
            staffSubjectMappingService.deleteMapping(mappingId);
            return ResponseEntity.noContent().build();
        } catch (SmaException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Get all subjects for a staff member
     */
    @GetMapping("/staff/{staffId}")
    public ResponseEntity<List<StaffSubjectMappingResponse>> getSubjectsForStaff(
            @PathVariable Long schoolId,
            @PathVariable Long staffId) {
        List<StaffSubjectMappingResponse> mappings = staffSubjectMappingService.getSubjectsForStaff(schoolId, staffId);
        return ResponseEntity.ok(mappings);
    }

    /**
     * Get qualified teachers for a subject (optionally filtered by class)
     */
    @GetMapping("/qualified")
    public ResponseEntity<List<StaffSubjectMappingResponse>> getQualifiedTeachers(
            @PathVariable Long schoolId,
            @RequestParam Long subjectId,
            @RequestParam(required = false) Long classId) {
        
        List<StaffSubjectMappingResponse> mappings;
        if (classId != null) {
            mappings = staffSubjectMappingService.getQualifiedTeachersForClassSubject(schoolId, classId, subjectId);
        } else {
            mappings = staffSubjectMappingService.getQualifiedTeachersForSubject(schoolId, subjectId);
        }
        
        return ResponseEntity.ok(mappings);
    }

    /**
     * Get all staff-subject mappings for a school
     */
    @GetMapping
    public ResponseEntity<List<StaffSubjectMappingResponse>> getAllMappings(@PathVariable Long schoolId) {
        List<StaffSubjectMappingResponse> mappings = staffSubjectMappingService.getAllMappingsForSchool(schoolId);
        return ResponseEntity.ok(mappings);
    }
}
