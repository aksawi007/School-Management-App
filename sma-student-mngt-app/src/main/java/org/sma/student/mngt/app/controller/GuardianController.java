package org.sma.student.mngt.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.sma.student.mngt.app.dto.GuardianDto;
import org.sma.jpa.model.studentmgmt.Guardian;
import org.sma.jpa.repository.studentmgmt.GuardianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schools/{schoolId}/students/{studentId}/guardians")
@Api(tags = "Guardian Management", description = "APIs for managing student guardians/parents")
public class GuardianController {

    @Autowired
    private GuardianRepository guardianRepository;

    @GetMapping
    @ApiOperation(value = "Get student guardians", notes = "Returns all guardians for a student")
    public ResponseEntity<List<GuardianDto>> getGuardians(
            @PathVariable UUID schoolId,
            @PathVariable UUID studentId) {
        
        List<Guardian> guardians = guardianRepository.findByStudentIdAndIsDeletedFalse(studentId);
        List<GuardianDto> dtos = guardians.stream().map(this::mapToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @ApiOperation(value = "Add guardian", notes = "Add a new guardian for student")
    public ResponseEntity<Map<String, Object>> addGuardian(
            @PathVariable UUID schoolId,
            @PathVariable UUID studentId,
            @Valid @RequestBody GuardianDto dto) {
        
        Guardian guardian = new Guardian();
        guardian.setStudentId(studentId);
        guardian.setRelation(dto.getRelation());
        guardian.setName(dto.getName());
        guardian.setPhone(dto.getPhone());
        guardian.setAlternatePhone(dto.getAlternatePhone());
        guardian.setEmail(dto.getEmail());
        guardian.setOccupation(dto.getOccupation());
        guardian.setAnnualIncome(dto.getAnnualIncome());
        guardian.setEducation(dto.getEducation());
        guardian.setIsPrimary(dto.getIsPrimary() != null ? dto.getIsPrimary() : false);
        guardian.setAadharNo(dto.getAadharNo());
        guardian.setPanNo(dto.getPanNo());
        guardian.setPhotoUrl(dto.getPhotoUrl());
        guardian.setCreatedBy("SYSTEM");
        
        guardian = guardianRepository.save(guardian);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Guardian added successfully");
        response.put("guardianId", guardian.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{guardianId}")
    @ApiOperation(value = "Delete guardian", notes = "Soft delete a guardian")
    public ResponseEntity<Map<String, Object>> deleteGuardian(
            @PathVariable UUID schoolId,
            @PathVariable UUID studentId,
            @PathVariable UUID guardianId) {
        
        Guardian guardian = guardianRepository.findByIdAndStudentIdAndIsDeletedFalse(guardianId, studentId)
                .orElseThrow(() -> new RuntimeException("Guardian not found"));
        
        guardian.setIsDeleted(true);
        guardian.setUpdatedBy("SYSTEM");
        guardianRepository.save(guardian);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Guardian deleted successfully");
        
        return ResponseEntity.ok(response);
    }

    private GuardianDto mapToDto(Guardian guardian) {
        GuardianDto dto = new GuardianDto();
        dto.setId(guardian.getId());
        dto.setRelation(guardian.getRelation());
        dto.setName(guardian.getName());
        dto.setPhone(guardian.getPhone());
        dto.setAlternatePhone(guardian.getAlternatePhone());
        dto.setEmail(guardian.getEmail());
        dto.setOccupation(guardian.getOccupation());
        dto.setAnnualIncome(guardian.getAnnualIncome());
        dto.setEducation(guardian.getEducation());
        dto.setIsPrimary(guardian.getIsPrimary());
        dto.setAadharNo(guardian.getAadharNo());
        dto.setPanNo(guardian.getPanNo());
        dto.setPhotoUrl(guardian.getPhotoUrl());
        return dto;
    }
}
