package org.sma.admin.core.app.controller;

import io.swagger.annotations.Api;
import org.sma.admin.core.app.model.request.DepartmentRequest;
import org.sma.admin.core.app.model.response.DepartmentResponse;
import org.sma.admin.core.app.service.DepartmentBusinessService;
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
 * Department Controller
 * Handles department master data management operations
 */
@APIController
@RequestMapping("/department")
@Api(tags = "Department API")
public class DepartmentController extends ApiRestServiceBinding {

    @Autowired
    DepartmentBusinessService departmentBusinessService;

    @PostMapping("/create")
    ResponseEntity<DepartmentResponse> createDepartment(@RequestBody DepartmentRequest request) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("CreateDepartment", 
            request.getDepartmentCode(), request.getDepartmentName());

        try {
            DepartmentResponse response = departmentBusinessService.createDepartment(context, request);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to create department: " + e.getMessage(), e);
        }
    }

    @GetMapping("/get")
    ResponseEntity<DepartmentResponse> getDepartment(@RequestParam("departmentId") Long departmentId) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("GetDepartment", 
            departmentId.toString(), departmentId.toString());

        try {
            DepartmentResponse response = departmentBusinessService.getDepartment(context, departmentId);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Department not found: " + e.getMessage(), e);
        }
    }

    @GetMapping("/getBySchool")
    ResponseEntity<List<DepartmentResponse>> getAllDepartmentsBySchool(@RequestParam("schoolId") Long schoolId) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("GetAllDepartmentsBySchool", 
            schoolId.toString(), schoolId.toString());

        try {
            List<DepartmentResponse> response = departmentBusinessService.getAllDepartmentsBySchool(context, schoolId);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to fetch departments: " + e.getMessage(), e);
        }
    }

    @PutMapping("/update")
    ResponseEntity<DepartmentResponse> updateDepartment(
            @RequestParam("departmentId") Long departmentId,
            @RequestBody DepartmentRequest request) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("UpdateDepartment", 
            departmentId.toString(), request.getDepartmentName());

        try {
            DepartmentResponse response = departmentBusinessService.updateDepartment(context, departmentId, request);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to update department: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/delete")
    ResponseEntity<String> deleteDepartment(@RequestParam("departmentId") Long departmentId) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("DeleteDepartment", 
            departmentId.toString(), departmentId.toString());

        try {
            departmentBusinessService.deleteDepartment(context, departmentId);
            return ResponseEntity.ok("Department deleted successfully");
        } catch (SmaException e) {
            throw new RuntimeException("Unable to delete department: " + e.getMessage(), e);
        }
    }
}
