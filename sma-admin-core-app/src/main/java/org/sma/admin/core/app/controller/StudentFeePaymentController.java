package org.sma.admin.core.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.sma.admin.core.app.model.request.StudentFeePaymentRequest;
import org.sma.admin.core.app.model.response.StudentFeePaymentResponse;
import org.sma.admin.core.app.service.StudentFeePaymentBusinessService;
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
 * Student Fee Payment Controller
 * Handles student payment recording and querying
 */
@APIController
@RequestMapping("/fee-payment")
@Api(tags = "Student Fee Payment API")
public class StudentFeePaymentController extends ApiRestServiceBinding {

    @Autowired
    private StudentFeePaymentBusinessService studentFeePaymentBusinessService;

    @PostMapping("/record")
    @ApiOperation(value = "Record student fee payment")
    ResponseEntity<StudentFeePaymentResponse> recordPayment(
            @RequestParam("schoolId") Long schoolId,
            @Valid @RequestBody StudentFeePaymentRequest request) throws IOException {
        
        ServiceRequestContext context = createServiceRequestContext("RecordPayment", 
            schoolId.toString(), schoolId.toString());

        try {
            StudentFeePaymentResponse response = studentFeePaymentBusinessService.recordPayment(schoolId, request);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to record payment: " + e.getMessage(), e);
        }
    }

    @GetMapping("/get/{paymentId}")
    @ApiOperation(value = "Get payment by ID")
    ResponseEntity<StudentFeePaymentResponse> getPaymentById(
            @PathVariable("paymentId") Long paymentId) throws IOException {
        
        ServiceRequestContext context = createServiceRequestContext("GetPayment", 
            paymentId.toString(), paymentId.toString());

        try {
            StudentFeePaymentResponse response = studentFeePaymentBusinessService.getPaymentById(paymentId);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to fetch payment: " + e.getMessage(), e);
        }
    }

    @GetMapping("/list")
    @ApiOperation(value = "List payments with filters", 
                  notes = "Filter by studentId and/or installmentId")
    ResponseEntity<List<StudentFeePaymentResponse>> listPayments(
            @RequestParam("schoolId") Long schoolId,
            @RequestParam(value = "studentId", required = false) Long studentId,
            @RequestParam(value = "installmentId", required = false) Long installmentId) throws IOException {
        
        ServiceRequestContext context = createServiceRequestContext("ListPayments", 
            schoolId.toString(), schoolId.toString());

        try {
            List<StudentFeePaymentResponse> response = studentFeePaymentBusinessService.listPayments(
                schoolId, studentId, installmentId);
            return processResponse(context, response);
        } catch (SmaException e) {
            throw new RuntimeException("Unable to fetch payments: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/delete/{paymentId}")
    @ApiOperation(value = "Delete payment")
    ResponseEntity<Void> deletePayment(@PathVariable("paymentId") Long paymentId) throws IOException {
        ServiceRequestContext context = createServiceRequestContext("DeletePayment", 
            paymentId.toString(), paymentId.toString());

        try {
            studentFeePaymentBusinessService.deletePayment(paymentId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (SmaException e) {
            throw new RuntimeException("Unable to delete payment: " + e.getMessage(), e);
        }
    }
}
