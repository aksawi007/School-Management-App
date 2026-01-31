package org.sma.admin.core.app.controller;

import org.sma.admin.core.app.model.request.StaffSubjectMappingRequest;
import org.sma.admin.core.app.model.response.StaffSubjectMappingResponse;
import org.sma.admin.core.app.service.StaffSubjectMappingBusinessService;
import org.sma.platform.core.exception.SmaException;
import org.sma.platform.core.restcontroller.ApiRestServiceBinding;
import org.sma.platform.core.service.ServiceRequestContext;
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
public class StaffSubjectMappingController  extends ApiRestServiceBinding{

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
     * Updated logic: Gets staff based on subject's linked department using getDepartmentStaff logic
     */
    @GetMapping("/qualified")
    public ResponseEntity<?> getQualifiedTeachers(
            @PathVariable Long schoolId,
            @RequestParam Long subjectId,
            @RequestParam(required = false) Long departmentId) {
                 ServiceRequestContext context = createServiceRequestContext("getQualifiedTeachers", 
            schoolId.toString(), subjectId.toString());
        try {
            List<?> staffList = staffSubjectMappingService.getStaffBySubjectDepartment(context, schoolId, subjectId, departmentId);
            return ResponseEntity.ok(staffList);
        } catch (SmaException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
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
