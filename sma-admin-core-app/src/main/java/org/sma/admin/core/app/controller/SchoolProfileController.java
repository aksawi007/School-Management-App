package org.sma.admin.core.app.controller;

import io.swagger.annotations.Api;
import org.sma.admin.core.app.model.request.SchoolProfileRequest;
import org.sma.admin.core.app.model.response.SchoolProfileResponse;
import org.sma.admin.core.app.service.SchoolSetupBusinessService;
import org.sma.platform.core.annotation.APIController;
import org.sma.platform.core.exception.SmaException;
import org.sma.platform.core.restcontroller.ApiRestServiceBinding;
import org.sma.platform.core.service.ServiceRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * School Profile Controller
 * Handles school setup and profile management operations
 */
@APIController
@RequestMapping("/school")
@Api(tags = "School Setup API")
public class SchoolProfileController extends ApiRestServiceBinding {

    @Autowired
    SchoolSetupBusinessService schoolSetupBusinessService;

    @PostMapping("/profile/create")
    ResponseEntity<SchoolProfileResponse> createSchoolProfile(@RequestBody SchoolProfileRequest request) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("CreateSchoolProfile", 
            request.getSchoolCode(), request.getSchoolName());

        try {
            SchoolProfileResponse response = schoolSetupBusinessService.createSchoolProfile(context, request);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to create school profile: " + e.getMessage(), e);
        }
    }

    @GetMapping("/profile/get")
    ResponseEntity<SchoolProfileResponse> getSchoolProfile(@RequestParam("schoolId") Long schoolId) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("GetSchoolProfile", 
            schoolId.toString(), schoolId.toString());

        try {
            SchoolProfileResponse response = schoolSetupBusinessService.getSchoolProfile(context, schoolId);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("School profile not found: " + e.getMessage(), e);
        }
    }

    @GetMapping("/profile/getAll")
    ResponseEntity<List<SchoolProfileResponse>> getAllSchools() throws IOException {
        ServiceRequestContext context = createServiceRequestContext("GetAllSchools", "ALL", "ALL");

        try {
            List<SchoolProfileResponse> response = schoolSetupBusinessService.getAllSchools(context);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to fetch schools: " + e.getMessage(), e);
        }
    }

    @PutMapping("/profile/update")
    ResponseEntity<SchoolProfileResponse> updateSchoolProfile(
            @RequestParam("schoolId") Long schoolId,
            @RequestBody SchoolProfileRequest request) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("UpdateSchoolProfile", 
            schoolId.toString(), request.getSchoolName());

        try {
            SchoolProfileResponse response = schoolSetupBusinessService.updateSchoolProfile(context, schoolId, request);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to update school profile: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/profile/delete")
    ResponseEntity<String> deleteSchoolProfile(@RequestParam("schoolId") Long schoolId) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("DeleteSchoolProfile", 
            schoolId.toString(), schoolId.toString());

        try {
            schoolSetupBusinessService.deleteSchoolProfile(context, schoolId);
            return ResponseEntity.ok("School profile deleted successfully");
        } catch (SmaException e) {
            throw new RuntimeException("Unable to delete school profile: " + e.getMessage(), e);
        }
    }

    @GetMapping("/profile/getByCode")
    ResponseEntity<SchoolProfileResponse> getSchoolByCode(@RequestParam("schoolCode") String schoolCode) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("GetSchoolByCode", schoolCode, schoolCode);

        try {
            SchoolProfileResponse response = schoolSetupBusinessService.getSchoolByCode(context, schoolCode);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("School not found with code: " + schoolCode, e);
        }
    }
}
