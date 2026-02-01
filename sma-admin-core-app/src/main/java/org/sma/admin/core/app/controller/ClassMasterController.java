package org.sma.admin.core.app.controller;

import io.swagger.annotations.Api;
import org.sma.admin.core.app.model.request.ClassMasterRequest;
import org.sma.admin.core.app.model.response.ClassMasterResponse;
import org.sma.admin.core.app.service.ClassMasterBusinessService;
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
 * ClassMaster Controller
 * Handles class/grade master data management
 */
@APIController
@RequestMapping("/class")
@Api(tags = "Class Master API")
public class ClassMasterController extends ApiRestServiceBinding {

    @Autowired
    ClassMasterBusinessService classMasterBusinessService;

    @PostMapping("/create")
    ResponseEntity<ClassMasterResponse> createClass(@RequestBody ClassMasterRequest request) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("CreateClass", 
            request.getClassName(), request.getClassName());

        try {
            ClassMasterResponse response = classMasterBusinessService.createClass(context, request);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to create class: " + e.getMessage(), e);
        }
    }

    @GetMapping("/get")
    ResponseEntity<ClassMasterResponse> getClass(@RequestParam("classId") String classId) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("GetClass", 
            classId, classId);

        try {
            ClassMasterResponse response = classMasterBusinessService.getClass(context, Long.parseLong(classId));
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to get class: " + e.getMessage(), e);
        }
    }

    @GetMapping("/school/{schoolId}")
    ResponseEntity<List<ClassMasterResponse>> getAllClassesBySchool(@PathVariable("schoolId") Long schoolId) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("GetAllClassesBySchool", 
            schoolId.toString(), schoolId.toString());

        try {
            List<ClassMasterResponse> response = classMasterBusinessService.getAllClassesBySchool(context, schoolId);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to get classes: " + e.getMessage(), e);
        }
    }

    @GetMapping("/academicYear/{academicYearId}")
    ResponseEntity<List<ClassMasterResponse>> getAllClassesByAcademicYear(@PathVariable("academicYearId") Long academicYearId) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("GetAllClassesByAcademicYear", 
            academicYearId.toString(), academicYearId.toString());

        try {
            List<ClassMasterResponse> response = classMasterBusinessService.getAllClassesByAcademicYear(context, academicYearId);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to get classes for academic year: " + e.getMessage(), e);
        }
    }

    @PutMapping("/update")
    ResponseEntity<ClassMasterResponse> updateClass(@RequestParam("classId") String classId,
                                                     @RequestBody ClassMasterRequest request) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("UpdateClass", 
            classId, classId);

        try {
            ClassMasterResponse response = classMasterBusinessService.updateClass(context, Long.parseLong(classId), request);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to update class: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/delete")
    ResponseEntity<Void> deleteClass(@RequestParam("classId") String classId) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("DeleteClass", 
            classId, classId);

        try {
            classMasterBusinessService.deleteClass(context, Long.parseLong(classId));
            return ResponseEntity.ok().build();
        } catch (SmaException e) {
            throw new RuntimeException("Unable to delete class: " + e.getMessage(), e);
        }
    }
}
