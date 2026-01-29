package org.sma.admin.core.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.sma.admin.core.app.model.request.DepartmentStaffMappingRequest;
import org.sma.admin.core.app.model.response.DepartmentStaffMappingResponse;
import org.sma.admin.core.app.service.DepartmentStaffMappingBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST Controller for Department-Staff Mapping
 */
@RestController
@RequestMapping("/api/department-staff-mapping")
@Api(tags = "Department-Staff Mapping", description = "APIs for managing department-staff assignments")
public class DepartmentStaffMappingController {

    @Autowired
    private DepartmentStaffMappingBusinessService mappingService;

    @PostMapping("/assign")
    @ApiOperation("Assign staff to department")
    public ResponseEntity<DepartmentStaffMappingResponse> assignStaffToDepartment(
            @Valid @RequestBody DepartmentStaffMappingRequest request) {
        DepartmentStaffMappingResponse response = mappingService.assignStaffToDepartment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/department/{departmentId}")
    @ApiOperation("Get all staff in a department")
    public ResponseEntity<List<DepartmentStaffMappingResponse>> getStaffByDepartment(
            @PathVariable Long departmentId) {
        List<DepartmentStaffMappingResponse> mappings = mappingService.getStaffByDepartment(departmentId);
        return ResponseEntity.ok(mappings);
    }

    @GetMapping("/staff/{staffId}")
    @ApiOperation("Get all departments for a staff member")
    public ResponseEntity<List<DepartmentStaffMappingResponse>> getDepartmentsByStaff(
            @PathVariable Long staffId) {
        List<DepartmentStaffMappingResponse> mappings = mappingService.getDepartmentsByStaff(staffId);
        return ResponseEntity.ok(mappings);
    }

    @GetMapping("/school/{schoolId}")
    @ApiOperation("Get all department-staff mappings for a school")
    public ResponseEntity<List<DepartmentStaffMappingResponse>> getMappingsBySchool(
            @PathVariable Long schoolId) {
        List<DepartmentStaffMappingResponse> mappings = mappingService.getMappingsBySchool(schoolId);
        return ResponseEntity.ok(mappings);
    }

    @PutMapping("/{mappingId}")
    @ApiOperation("Update department-staff mapping")
    public ResponseEntity<DepartmentStaffMappingResponse> updateMapping(
            @PathVariable Long mappingId,
            @Valid @RequestBody DepartmentStaffMappingRequest request) {
        DepartmentStaffMappingResponse response = mappingService.updateMapping(mappingId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{mappingId}")
    @ApiOperation("Remove staff from department")
    public ResponseEntity<String> removeStaffFromDepartment(@PathVariable Long mappingId) {
        mappingService.removeStaffFromDepartment(mappingId);
        return ResponseEntity.ok("Staff removed from department successfully");
    }
}
