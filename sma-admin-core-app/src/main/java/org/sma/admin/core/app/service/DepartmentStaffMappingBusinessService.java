package org.sma.admin.core.app.service;

import org.sma.admin.core.app.model.request.DepartmentStaffMappingRequest;
import org.sma.admin.core.app.model.response.DepartmentStaffMappingResponse;
import org.sma.jpa.model.master.DepartmentMaster;
import org.sma.jpa.model.staff.DepartmentStaffMapping;
import org.sma.jpa.model.staff.Staff;
import org.sma.jpa.repository.master.DepartmentMasterRepository;
import org.sma.jpa.repository.staff.DepartmentStaffMappingRepository;
import org.sma.jpa.repository.staff.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Business service for Department-Staff Mapping operations
 */
@Service
public class DepartmentStaffMappingBusinessService {

    @Autowired
    private DepartmentStaffMappingRepository mappingRepository;

    @Autowired
    private DepartmentMasterRepository departmentRepository;

    @Autowired
    private StaffRepository staffRepository;

    /**
     * Assign staff to department
     */
    @Transactional
    public DepartmentStaffMappingResponse assignStaffToDepartment(DepartmentStaffMappingRequest request) {
        // Validate department exists
        DepartmentMaster department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + request.getDepartmentId()));

        // Validate staff exists
        Staff staff = staffRepository.findById(request.getStaffId())
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + request.getStaffId()));

        // Check if mapping already exists
        if (mappingRepository.findByDepartmentIdAndStaffId(request.getDepartmentId(), request.getStaffId()).isPresent()) {
            throw new RuntimeException("Staff is already assigned to this department");
        }

        // If this is primary department, unset any existing primary department for this staff
        if (Boolean.TRUE.equals(request.getIsPrimaryDepartment())) {
            mappingRepository.findPrimaryDepartmentByStaffId(request.getStaffId())
                    .ifPresent(existing -> {
                        existing.setIsPrimaryDepartment(false);
                        mappingRepository.save(existing);
                    });
        }

        // Create new mapping
        DepartmentStaffMapping mapping = new DepartmentStaffMapping();
        mapping.setDepartment(department);
        mapping.setStaff(staff);
        mapping.setRoleInDepartment(request.getRoleInDepartment());
        mapping.setAssignmentDate(request.getAssignmentDate() != null ? request.getAssignmentDate() : LocalDate.now());
        mapping.setIsPrimaryDepartment(request.getIsPrimaryDepartment() != null ? request.getIsPrimaryDepartment() : false);
        mapping.setIsActive(true);
        mapping.setRemarks(request.getRemarks());

        mapping = mappingRepository.save(mapping);

        return mapToResponse(mapping);
    }

    /**
     * Get all staff in a department
     */
    public List<DepartmentStaffMappingResponse> getStaffByDepartment(Long departmentId) {
        return mappingRepository.findByDepartmentId(departmentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all departments for a staff member
     */
    public List<DepartmentStaffMappingResponse> getDepartmentsByStaff(Long staffId) {
        return mappingRepository.findByStaffId(staffId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all mappings for a school
     */
    public List<DepartmentStaffMappingResponse> getMappingsBySchool(Long schoolId) {
        return mappingRepository.findBySchoolId(schoolId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update mapping details
     */
    @Transactional
    public DepartmentStaffMappingResponse updateMapping(Long mappingId, DepartmentStaffMappingRequest request) {
        DepartmentStaffMapping mapping = mappingRepository.findById(mappingId)
                .orElseThrow(() -> new RuntimeException("Mapping not found with id: " + mappingId));

        // If making this primary, unset existing primary for this staff
        if (Boolean.TRUE.equals(request.getIsPrimaryDepartment()) && !Boolean.TRUE.equals(mapping.getIsPrimaryDepartment())) {
            mappingRepository.findPrimaryDepartmentByStaffId(mapping.getStaff().getId())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(mappingId)) {
                            existing.setIsPrimaryDepartment(false);
                            mappingRepository.save(existing);
                        }
                    });
        }

        mapping.setRoleInDepartment(request.getRoleInDepartment());
        mapping.setAssignmentDate(request.getAssignmentDate());
        mapping.setIsPrimaryDepartment(request.getIsPrimaryDepartment());
        mapping.setRemarks(request.getRemarks());

        mapping = mappingRepository.save(mapping);

        return mapToResponse(mapping);
    }

    /**
     * Remove staff from department (soft delete)
     */
    @Transactional
    public void removeStaffFromDepartment(Long mappingId) {
        DepartmentStaffMapping mapping = mappingRepository.findById(mappingId)
                .orElseThrow(() -> new RuntimeException("Mapping not found with id: " + mappingId));

        mapping.setIsActive(false);
        mappingRepository.save(mapping);
    }

    /**
     * Map entity to response DTO
     */
    private DepartmentStaffMappingResponse mapToResponse(DepartmentStaffMapping mapping) {
        DepartmentStaffMappingResponse response = new DepartmentStaffMappingResponse();
        response.setMappingId(mapping.getId());
        response.setDepartmentId(mapping.getDepartment().getId());
        response.setDepartmentCode(mapping.getDepartment().getDepartmentCode());
        response.setDepartmentName(mapping.getDepartment().getDepartmentName());
        response.setStaffId(mapping.getStaff().getId());
        response.setEmployeeCode(mapping.getStaff().getEmployeeCode());
        response.setStaffName(mapping.getStaff().getFirstName() + " " + mapping.getStaff().getLastName());
        response.setRoleInDepartment(mapping.getRoleInDepartment());
        response.setAssignmentDate(mapping.getAssignmentDate());
        response.setIsPrimaryDepartment(mapping.getIsPrimaryDepartment());
        response.setIsActive(mapping.getIsActive());
        response.setRemarks(mapping.getRemarks());
        return response;
    }
}
