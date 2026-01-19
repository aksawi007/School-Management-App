package org.sma.admin.core.app.controller;

import io.swagger.annotations.Api;
import org.sma.admin.core.app.model.request.AcademicYearRequest;
import org.sma.admin.core.app.model.response.AcademicYearResponse;
import org.sma.admin.core.app.service.AcademicYearBusinessService;
import org.sma.platform.core.annotation.APIController;
import org.sma.platform.core.exception.SmaException;
import org.sma.platform.core.restcontroller.ApiRestServiceBinding;
import org.sma.platform.core.service.ServiceRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Academic Year Controller
 * Handles academic year setup and management operations
 */
@APIController
@RequestMapping("/academic-year")
@Api(tags = "Academic Year API")
public class AcademicYearController extends ApiRestServiceBinding {

    @Autowired
    AcademicYearBusinessService academicYearBusinessService;

    @PostMapping("/create")
    ResponseEntity<AcademicYearResponse> createAcademicYear(@RequestBody AcademicYearRequest request) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("CreateAcademicYear", 
            request.getYearName(), request.getYearName());

        try {
            AcademicYearResponse response = academicYearBusinessService.createAcademicYear(context, request);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to create academic year: " + e.getMessage(), e);
        }
    }

    @GetMapping("/get")
    ResponseEntity<AcademicYearResponse> getAcademicYear(@RequestParam("yearId") Long yearId) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("GetAcademicYear", 
            yearId.toString(), yearId.toString());

        try {
            AcademicYearResponse response = academicYearBusinessService.getAcademicYear(context, yearId);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Academic year not found: " + e.getMessage(), e);
        }
    }

    @GetMapping("/getAll")
    ResponseEntity<List<AcademicYearResponse>> getAllAcademicYears() throws IOException {
        ServiceRequestContext context = createServiceRequestContext("GetAllAcademicYears", "ALL", "ALL");

        try {
            List<AcademicYearResponse> response = academicYearBusinessService.getAllAcademicYears(context);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to fetch academic years: " + e.getMessage(), e);
        }
    }

    @GetMapping("/getCurrent")
    ResponseEntity<AcademicYearResponse> getCurrentAcademicYear() throws IOException {
        ServiceRequestContext context = createServiceRequestContext("GetCurrentAcademicYear", "CURRENT", "CURRENT");

        try {
            AcademicYearResponse response = academicYearBusinessService.getCurrentAcademicYear(context);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("No current academic year found: " + e.getMessage(), e);
        }
    }

    @PutMapping("/update")
    ResponseEntity<AcademicYearResponse> updateAcademicYear(
            @RequestParam("yearId") Long yearId,
            @RequestBody AcademicYearRequest request) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("UpdateAcademicYear", 
            yearId.toString(), request.getYearName());

        try {
            AcademicYearResponse response = academicYearBusinessService.updateAcademicYear(context, yearId, request);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to update academic year: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/delete")
    ResponseEntity<String> deleteAcademicYear(@RequestParam("yearId") Long yearId) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("DeleteAcademicYear", 
            yearId.toString(), yearId.toString());

        try {
            academicYearBusinessService.deleteAcademicYear(context, yearId);
            return ResponseEntity.ok("Academic year deleted successfully");
        } catch (SmaException e) {
            throw new RuntimeException("Unable to delete academic year: " + e.getMessage(), e);
        }
    }

    @PutMapping("/setCurrent")
    ResponseEntity<AcademicYearResponse> setCurrentAcademicYear(@RequestParam("yearId") Long yearId) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("SetCurrentAcademicYear", 
            yearId.toString(), yearId.toString());

        try {
            AcademicYearResponse response = academicYearBusinessService.setCurrentAcademicYear(context, yearId);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to set current academic year: " + e.getMessage(), e);
        }
    }
}
