package org.sma.admin.core.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.sma.admin.core.app.model.request.FeeCategoryRequest;
import org.sma.admin.core.app.model.response.FeeCategoryResponse;
import org.sma.admin.core.app.service.FeeCategoryBusinessService;
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
 * Fee Category Controller
 * Handles fee category management - core structure applicable throughout every year
 */
@APIController
@RequestMapping("/fee-category")
@Api(tags = "Fee Category API")
public class FeeCategoryController extends ApiRestServiceBinding {

    @Autowired
    private FeeCategoryBusinessService feeCategoryBusinessService;

    @PostMapping("/create")
    @ApiOperation(value = "Create a new fee category", notes = "Creates a core fee category structure applicable every year")
    ResponseEntity<FeeCategoryResponse> createFeeCategory(
            @RequestParam("schoolId") Long schoolId,
            @Valid @RequestBody FeeCategoryRequest request) throws IOException {
        
        ServiceRequestContext context = createServiceRequestContext("CreateFeeCategory", 
            request.getCategoryCode(), request.getCategoryCode());

        try {
            FeeCategoryResponse response = feeCategoryBusinessService.createFeeCategory(schoolId, request);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to create fee category: " + e.getMessage(), e);
        }
    }

    @PutMapping("/update/{categoryId}")
    @ApiOperation(value = "Update fee category", notes = "Updates fee category including status changes (activate/deactivate)")
    ResponseEntity<FeeCategoryResponse> updateFeeCategory(
            @PathVariable("categoryId") Long categoryId,
            @Valid @RequestBody FeeCategoryRequest request) throws IOException {
        
        ServiceRequestContext context = createServiceRequestContext("UpdateFeeCategory", 
            categoryId.toString(), categoryId.toString());

        try {
            FeeCategoryResponse response = feeCategoryBusinessService.updateFeeCategory(categoryId, request);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to update fee category: " + e.getMessage(), e);
        }
    }

    @GetMapping("/get/{categoryId}")
    @ApiOperation(value = "Get fee category by ID")
    ResponseEntity<FeeCategoryResponse> getFeeCategoryById(@PathVariable("categoryId") Long categoryId) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("GetFeeCategory", 
            categoryId.toString(), categoryId.toString());

        try {
            FeeCategoryResponse response = feeCategoryBusinessService.getFeeCategoryById(categoryId);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to fetch fee category: " + e.getMessage(), e);
        }
    }

    @GetMapping("/list")
    @ApiOperation(value = "Get fee categories with optional filters", 
                  notes = "Filter by status (ACTIVE/INACTIVE/ALL) and/or categoryType (TUITION/TRANSPORT/etc)")
    ResponseEntity<List<FeeCategoryResponse>> listFeeCategories(
            @RequestParam("schoolId") Long schoolId,
            @RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
            @RequestParam(value = "categoryType", required = false) String categoryType) throws IOException {
        
        ServiceRequestContext context = createServiceRequestContext("ListFeeCategories", 
            schoolId.toString(), schoolId.toString());

        try {
            List<FeeCategoryResponse> response = feeCategoryBusinessService.listFeeCategories(schoolId, status, categoryType);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to fetch fee categories: " + e.getMessage(), e);
        }
    }
}
