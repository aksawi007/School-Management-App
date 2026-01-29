package org.sma.admin.core.app.service;

import org.sma.admin.core.app.model.request.DepartmentRequest;
import org.sma.admin.core.app.model.response.DepartmentResponse;
import org.sma.jpa.model.master.DepartmentMaster;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.repository.master.DepartmentMasterRepository;
import org.sma.jpa.repository.school.SchoolProfileRepository;
import org.sma.platform.core.exception.SmaException;
import org.sma.platform.core.service.ServiceRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Department Business Service
 * Handles business logic for department management
 */
@Component
public class DepartmentBusinessService {

    @Autowired
    private DepartmentMasterRepository departmentRepository;

    @Autowired
    private SchoolProfileRepository schoolProfileRepository;

    /**
     * Create new department
     */
    @Transactional
    public DepartmentResponse createDepartment(ServiceRequestContext context, 
                                               DepartmentRequest request) throws SmaException {
        // Validate required fields
        if (request.getSchoolId() == null) {
            throw new SmaException("School ID is mandatory");
        }
        if (request.getDepartmentCode() == null || request.getDepartmentCode().isEmpty()) {
            throw new SmaException("Department code is mandatory");
        }
        if (request.getDepartmentName() == null || request.getDepartmentName().isEmpty()) {
            throw new SmaException("Department name is mandatory");
        }

        // Get school
        SchoolProfile school = schoolProfileRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new SmaException("School not found with id: " + request.getSchoolId()));

        // Check if department code already exists for this school
        if (departmentRepository.findBySchoolAndDepartmentCode(school, request.getDepartmentCode()).isPresent()) {
            throw new SmaException("Department with code " + request.getDepartmentCode() + " already exists for this school");
        }

        // Create department
        DepartmentMaster department = new DepartmentMaster();
        department.setSchool(school);
        department.setDepartmentCode(request.getDepartmentCode());
        department.setDepartmentName(request.getDepartmentName());
        department.setDepartmentType(request.getDepartmentType());
        department.setHodName(request.getHodName());
        department.setHodEmail(request.getHodEmail());
        department.setHodPhone(request.getHodPhone());
        department.setDescription(request.getDescription());
        department.setIsActive(true);

        DepartmentMaster savedDepartment = departmentRepository.save(department);
        return convertToResponse(savedDepartment);
    }

    /**
     * Get department by ID
     */
    public DepartmentResponse getDepartment(ServiceRequestContext context, Long departmentId) throws SmaException {
        DepartmentMaster department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new SmaException("Department not found with id: " + departmentId));
        
        return convertToResponse(department);
    }

    /**
     * Get all departments for a school
     */
    public List<DepartmentResponse> getAllDepartmentsBySchool(ServiceRequestContext context, 
                                                              Long schoolId) throws SmaException {
        SchoolProfile school = schoolProfileRepository.findById(schoolId)
                .orElseThrow(() -> new SmaException("School not found with id: " + schoolId));

        List<DepartmentMaster> departments = departmentRepository.findBySchoolAndIsActiveTrue(school);
        return departments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update department
     */
    @Transactional
    public DepartmentResponse updateDepartment(ServiceRequestContext context, 
                                              Long departmentId, 
                                              DepartmentRequest request) throws SmaException {
        DepartmentMaster existingDepartment = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new SmaException("Department not found with id: " + departmentId));

        // Update fields
        if (request.getDepartmentName() != null) {
            existingDepartment.setDepartmentName(request.getDepartmentName());
        }
        if (request.getDepartmentType() != null) {
            existingDepartment.setDepartmentType(request.getDepartmentType());
        }
        existingDepartment.setHodName(request.getHodName());
        existingDepartment.setHodEmail(request.getHodEmail());
        existingDepartment.setHodPhone(request.getHodPhone());
        existingDepartment.setDescription(request.getDescription());

        DepartmentMaster updatedDepartment = departmentRepository.save(existingDepartment);
        return convertToResponse(updatedDepartment);
    }

    /**
     * Delete department (soft delete)
     */
    @Transactional
    public void deleteDepartment(ServiceRequestContext context, Long departmentId) throws SmaException {
        DepartmentMaster department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new SmaException("Department not found with id: " + departmentId));

        // Soft delete
        department.setIsActive(false);
        departmentRepository.save(department);
    }

    /**
     * Convert DepartmentMaster entity to response DTO
     */
    private DepartmentResponse convertToResponse(DepartmentMaster department) {
        DepartmentResponse response = new DepartmentResponse();
        response.setDepartmentId(department.getId());
        response.setSchoolId(department.getSchool().getId());
        response.setDepartmentCode(department.getDepartmentCode());
        response.setDepartmentName(department.getDepartmentName());
        response.setDepartmentType(department.getDepartmentType());
        response.setHodName(department.getHodName());
        response.setHodEmail(department.getHodEmail());
        response.setHodPhone(department.getHodPhone());
        response.setDescription(department.getDescription());
        return response;
    }
}
