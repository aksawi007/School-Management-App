package org.sma.admin.core.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.sma.admin.core.app.model.request.FeePlanRequest;
import org.sma.admin.core.app.model.response.FeePlanResponse;
import org.sma.admin.core.app.service.FeePlanBusinessService;
import org.sma.platform.core.annotation.APIController;
import org.sma.platform.core.exception.SmaException;
import org.sma.platform.core.restcontroller.ApiRestServiceBinding;
import org.sma.platform.core.service.ServiceRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * Fee Plan Controller
 * Handles fee plan and installment management operations
 */
@APIController
@RequestMapping("/fee-plan")
@Api(tags = "Fee Plan API")
public class FeePlanController extends ApiRestServiceBinding {

    @Autowired
    private FeePlanBusinessService feePlanBusinessService;

    @PostMapping("/create")
    @ApiOperation(value = "Create fee plan", notes = "Create fee plan with optional installments")
    ResponseEntity<FeePlanResponse> createFeePlan(
            @RequestParam("schoolId") Long schoolId,
            @Valid @RequestBody FeePlanRequest request) throws IOException {
        
        ServiceRequestContext context = createServiceRequestContext("CreateFeePlan", 
            schoolId.toString(), schoolId.toString());

        try {
            FeePlanResponse response = feePlanBusinessService.createFeePlan(schoolId, request);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to create fee plan: " + e.getMessage(), e);
        }
    }

    @PutMapping("/update/{planId}")
    @ApiOperation(value = "Update fee plan")
    ResponseEntity<FeePlanResponse> updateFeePlan(
            @PathVariable("planId") Long planId,
            @Valid @RequestBody FeePlanRequest request) throws IOException {
        
        ServiceRequestContext context = createServiceRequestContext("UpdateFeePlan", 
            planId.toString(), planId.toString());

        try {
            FeePlanResponse response = feePlanBusinessService.updateFeePlan(planId, request);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to update fee plan: " + e.getMessage(), e);
        }
    }

    @GetMapping("/get/{planId}")
    @ApiOperation(value = "Get fee plan by ID")
    ResponseEntity<FeePlanResponse> getFeePlanById(
            @PathVariable("planId") Long planId,
            @RequestParam(value = "includeInstallments", defaultValue = "true") boolean includeInstallments) throws IOException {
        
        ServiceRequestContext context = createServiceRequestContext("GetFeePlan", 
            planId.toString(), planId.toString());

        try {
            FeePlanResponse response = feePlanBusinessService.getFeePlanById(planId, includeInstallments);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to fetch fee plan: " + e.getMessage(), e);
        }
    }

    @GetMapping("/list")
    @ApiOperation(value = "List fee plans with filters", 
                  notes = "Filter by academicYearId, categoryId, status. Use includeInstallments=true to get installment details")
    ResponseEntity<List<FeePlanResponse>> listFeePlans(
            @RequestParam("schoolId") Long schoolId,
            @RequestParam(value = "academicYearId", required = false) Long academicYearId,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
            @RequestParam(value = "includeInstallments", defaultValue = "false") boolean includeInstallments) throws IOException {
        
        ServiceRequestContext context = createServiceRequestContext("ListFeePlans", 
            schoolId.toString(), schoolId.toString());

        try {
            List<FeePlanResponse> response = feePlanBusinessService.listFeePlans(
                schoolId, academicYearId, categoryId, status, includeInstallments);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to fetch fee plans: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/delete/{planId}")
    @ApiOperation(value = "Delete fee plan")
    ResponseEntity<Void> deleteFeePlan(@PathVariable("planId") Long planId) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("DeleteFeePlan", 
            planId.toString(), planId.toString());

        try {
            feePlanBusinessService.deleteFeePlan(planId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (SmaException e) {
            throw new RuntimeException("Unable to delete fee plan: " + e.getMessage(), e);
        }
    }
}
