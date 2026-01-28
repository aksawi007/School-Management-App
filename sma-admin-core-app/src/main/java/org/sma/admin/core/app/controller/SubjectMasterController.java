package org.sma.admin.core.app.controller;

import io.swagger.annotations.Api;
import org.sma.admin.core.app.model.request.SubjectMasterRequest;
import org.sma.admin.core.app.model.response.SubjectMasterResponse;
import org.sma.admin.core.app.service.SubjectMasterBusinessService;
import org.sma.platform.core.annotation.APIController;
import org.sma.platform.core.exception.SmaException;
import org.sma.platform.core.restcontroller.ApiRestServiceBinding;
import org.sma.platform.core.service.ServiceRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * SubjectMaster Controller
 * Handles subject master data management
 */
@APIController
@RequestMapping("/subject")
@Api(tags = "Subject Master API")
public class SubjectMasterController extends ApiRestServiceBinding {

    @Autowired
    SubjectMasterBusinessService subjectMasterBusinessService;

    @PostMapping("/create")
    ResponseEntity<SubjectMasterResponse> createSubject(@RequestBody SubjectMasterRequest request) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("CreateSubject", 
            request.getSubjectName(), request.getSubjectName());

        try {
            SubjectMasterResponse response = subjectMasterBusinessService.createSubject(context, request);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to create subject: " + e.getMessage(), e);
        }
    }

    @GetMapping("/get")
    ResponseEntity<SubjectMasterResponse> getSubject(@RequestParam("subjectId") String subjectId) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("GetSubject", 
            subjectId, subjectId);

        try {
            SubjectMasterResponse response = subjectMasterBusinessService.getSubject(context, UUID.fromString(subjectId));
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to get subject: " + e.getMessage(), e);
        }
    }

    @GetMapping("/school/{schoolId}")
    ResponseEntity<List<SubjectMasterResponse>> getAllSubjectsBySchool(@PathVariable("schoolId") Long schoolId) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("GetAllSubjectsBySchool", 
            schoolId.toString(), schoolId.toString());

        try {
            List<SubjectMasterResponse> response = subjectMasterBusinessService.getAllSubjectsBySchool(context, schoolId);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to get subjects: " + e.getMessage(), e);
        }
    }

    @GetMapping("/school/{schoolId}/type/{subjectType}")
    ResponseEntity<List<SubjectMasterResponse>> getSubjectsByType(@PathVariable("schoolId") Long schoolId,
                                                                   @PathVariable("subjectType") String subjectType) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("GetSubjectsByType", 
            schoolId.toString(), subjectType);

        try {
            List<SubjectMasterResponse> response = subjectMasterBusinessService.getSubjectsByType(context, schoolId, subjectType);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to get subjects by type: " + e.getMessage(), e);
        }
    }

    @PutMapping("/update")
    ResponseEntity<SubjectMasterResponse> updateSubject(@RequestParam("subjectId") String subjectId,
                                                        @RequestBody SubjectMasterRequest request) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("UpdateSubject", 
            subjectId, subjectId);

        try {
            SubjectMasterResponse response = subjectMasterBusinessService.updateSubject(context, UUID.fromString(subjectId), request);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to update subject: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/delete")
    ResponseEntity<Void> deleteSubject(@RequestParam("subjectId") String subjectId) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("DeleteSubject", 
            subjectId, subjectId);

        try {
            subjectMasterBusinessService.deleteSubject(context, UUID.fromString(subjectId));
            return ResponseEntity.ok().build();
        } catch (SmaException e) {
            throw new RuntimeException("Unable to delete subject: " + e.getMessage(), e);
        }
    }
}
