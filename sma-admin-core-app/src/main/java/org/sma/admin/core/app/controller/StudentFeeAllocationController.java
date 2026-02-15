package org.sma.admin.core.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.sma.admin.core.app.model.request.StudentFeeAllocationRequest;
import org.sma.admin.core.app.model.response.StudentFeeAllocationResponse;
import org.sma.admin.core.app.service.StudentFeeAllocationBusinessService;
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
import java.math.BigDecimal;
import java.util.List;

/**
 * Student Fee Allocation Controller
 * Handles manual fee allocation to students by admin based on category, duration, and month
 */
@APIController
@RequestMapping("/student-fee-allocation")
@Api(tags = "Student Fee Allocation API")
public class StudentFeeAllocationController extends ApiRestServiceBinding {

    @Autowired
    private StudentFeeAllocationBusinessService studentFeeAllocationBusinessService;

    @PostMapping("/allocate")
    @ApiOperation(value = "Manually allocate fee to a student", 
                  notes = "Admin can allocate fee to specific student based on category, duration, and month")
    ResponseEntity<StudentFeeAllocationResponse> allocateFeeToStudent(
            @RequestParam("schoolId") Long schoolId,
            @RequestParam("academicYearId") Long academicYearId,
            @RequestParam("allocatedBy") Long allocatedBy,
            @Valid @RequestBody StudentFeeAllocationRequest request) throws IOException {
        
        ServiceRequestContext context = createServiceRequestContext("AllocateFeeToStudent", 
            request.getStudentId().toString(), request.getStudentId().toString());

        try {
            StudentFeeAllocationResponse response = studentFeeAllocationBusinessService
                .allocateFeeToStudent(schoolId, academicYearId, request, allocatedBy);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to allocate fee: " + e.getMessage(), e);
        }
    }

    @GetMapping("/student/{studentId}")
    @ApiOperation(value = "Get fee allocations for a student in an academic year")
    ResponseEntity<List<StudentFeeAllocationResponse>> getStudentFeeAllocations(
            @PathVariable("studentId") Long studentId,
            @RequestParam("academicYearId") Long academicYearId) throws IOException {
        
        ServiceRequestContext context = createServiceRequestContext("GetStudentFeeAllocations", 
            studentId.toString(), studentId.toString());

        try {
            List<StudentFeeAllocationResponse> response = studentFeeAllocationBusinessService
                .getStudentFeeAllocations(studentId, academicYearId);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to fetch fee allocations: " + e.getMessage(), e);
        }
    }

    @GetMapping("/student/{studentId}/pending")
    @ApiOperation(value = "Get pending fee allocations for a student")
    ResponseEntity<List<StudentFeeAllocationResponse>> getPendingFeeAllocations(
            @PathVariable("studentId") Long studentId) throws IOException {
        
        ServiceRequestContext context = createServiceRequestContext("GetPendingFeeAllocations", 
            studentId.toString(), studentId.toString());

        try {
            List<StudentFeeAllocationResponse> response = studentFeeAllocationBusinessService
                .getPendingFeeAllocations(studentId);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to fetch pending fees: " + e.getMessage(), e);
        }
    }

    @GetMapping("/overdue")
    @ApiOperation(value = "Get overdue fee allocations")
    ResponseEntity<List<StudentFeeAllocationResponse>> getOverdueFeeAllocations(
            @RequestParam("schoolId") Long schoolId,
            @RequestParam("academicYearId") Long academicYearId) throws IOException {
        
        ServiceRequestContext context = createServiceRequestContext("GetOverdueFeeAllocations", 
            schoolId.toString(), schoolId.toString());

        try {
            List<StudentFeeAllocationResponse> response = studentFeeAllocationBusinessService
                .getOverdueFeeAllocations(schoolId, academicYearId);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to fetch overdue fees: " + e.getMessage(), e);
        }
    }

    @GetMapping("/by-month")
    @ApiOperation(value = "Get fee allocations for a specific month")
    ResponseEntity<List<StudentFeeAllocationResponse>> getFeeAllocationsByMonth(
            @RequestParam("schoolId") Long schoolId,
            @RequestParam("academicYearId") Long academicYearId,
            @RequestParam("month") String month) throws IOException {
        
        ServiceRequestContext context = createServiceRequestContext("GetFeeAllocationsByMonth", 
            month, month);

        try {
            List<StudentFeeAllocationResponse> response = studentFeeAllocationBusinessService
                .getFeeAllocationsByMonth(schoolId, academicYearId, month);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to fetch fee allocations: " + e.getMessage(), e);
        }
    }

    @GetMapping("/student/{studentId}/total-pending")
    @ApiOperation(value = "Calculate total pending amount for a student")
    ResponseEntity<BigDecimal> calculateTotalPendingAmount(
            @PathVariable("studentId") Long studentId,
            @RequestParam("academicYearId") Long academicYearId) throws IOException {
        
        ServiceRequestContext context = createServiceRequestContext("CalculateTotalPending", 
            studentId.toString(), studentId.toString());

        try {
            BigDecimal totalPending = studentFeeAllocationBusinessService
                .calculateTotalPendingAmount(studentId, academicYearId);
            return processResponse(context, totalPending);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to calculate pending amount: " + e.getMessage(), e);
        }
    }

    @PutMapping("/update-status/{allocationId}")
    @ApiOperation(value = "Update fee allocation status", notes = "For payment processing")
    ResponseEntity<StudentFeeAllocationResponse> updateAllocationStatus(
            @PathVariable("allocationId") Long allocationId,
            @RequestParam("status") String status,
            @RequestParam(value = "paidAmount", required = false) BigDecimal paidAmount) throws IOException {
        
        ServiceRequestContext context = createServiceRequestContext("UpdateAllocationStatus", 
            allocationId.toString(), allocationId.toString());

        try {
            StudentFeeAllocationResponse response = studentFeeAllocationBusinessService
                .updateAllocationStatus(allocationId, status, paidAmount);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to update status: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/cancel/{allocationId}")
    @ApiOperation(value = "Cancel fee allocation")
    ResponseEntity<Void> cancelFeeAllocation(
            @PathVariable("allocationId") Long allocationId,
            @RequestParam("reason") String reason) throws IOException {
        
        ServiceRequestContext context = createServiceRequestContext("CancelFeeAllocation", 
            allocationId.toString(), allocationId.toString());

        try {
            studentFeeAllocationBusinessService.cancelFeeAllocation(allocationId, reason);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (SmaException e) {
            throw new RuntimeException("Unable to cancel allocation: " + e.getMessage(), e);
        }
    }
}
