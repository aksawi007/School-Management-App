package org.sma.staff.mngt.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.sma.staff.mngt.app.dto.StaffRequestDTO;
import org.sma.staff.mngt.app.dto.StaffResponseDTO;
import org.sma.staff.mngt.app.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Staff Management
 */
@RestController
@RequestMapping("/api/staff")
@Api(tags = "Staff Management", description = "APIs for managing teaching and non-teaching staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @PostMapping
    @ApiOperation("Create a new staff member")
    public ResponseEntity<StaffResponseDTO> createStaff(@Valid @RequestBody StaffRequestDTO requestDTO) {
        StaffResponseDTO response = staffService.createStaff(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{staffId}")
    @ApiOperation("Update staff member details")
    public ResponseEntity<StaffResponseDTO> updateStaff(
            @PathVariable UUID staffId,
            @Valid @RequestBody StaffRequestDTO requestDTO) {
        StaffResponseDTO response = staffService.updateStaff(staffId, requestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{staffId}")
    @ApiOperation("Get staff member by ID")
    public ResponseEntity<StaffResponseDTO> getStaffById(@PathVariable UUID staffId) {
        StaffResponseDTO response = staffService.getStaffById(staffId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/school/{schoolId}")
    @ApiOperation("Get all active staff members for a school")
    public ResponseEntity<List<StaffResponseDTO>> getAllStaffBySchool(@PathVariable UUID schoolId) {
        List<StaffResponseDTO> response = staffService.getAllStaffBySchool(schoolId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/school/{schoolId}/type/{staffType}")
    @ApiOperation("Get staff members by type (TEACHING, NON_TEACHING, ADMINISTRATIVE)")
    public ResponseEntity<List<StaffResponseDTO>> getStaffByType(
            @PathVariable UUID schoolId,
            @PathVariable String staffType) {
        List<StaffResponseDTO> response = staffService.getStaffByType(schoolId, staffType);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{staffId}")
    @ApiOperation("Deactivate staff member")
    public ResponseEntity<Void> deleteStaff(@PathVariable UUID staffId) {
        staffService.deleteStaff(staffId);
        return ResponseEntity.noContent().build();
    }
}
