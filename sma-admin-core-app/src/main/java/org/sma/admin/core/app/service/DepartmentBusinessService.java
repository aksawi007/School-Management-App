package org.sma.admin.core.app.service;

import org.sma.admin.core.app.model.request.DepartmentRequest;
import org.sma.admin.core.app.model.response.DepartmentResponse;
import org.sma.admin.core.app.model.response.DepartmentStaffResponse;
import org.sma.jpa.model.master.DepartmentMaster;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.staff.DepartmentStaffMapping;
import org.sma.jpa.model.staff.Staff;
import org.sma.jpa.repository.master.DepartmentMasterRepository;
import org.sma.jpa.repository.school.SchoolProfileRepository;
import org.sma.jpa.repository.staff.DepartmentStaffMappingRepository;
import org.sma.jpa.repository.staff.StaffRepository;
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

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private DepartmentStaffMappingRepository departmentStaffMappingRepository;

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
        
        // Set HOD if provided
        if (request.getHodStaffId() != null) {
            Staff hodStaff = staffRepository.findById(request.getHodStaffId())
                    .orElseThrow(() -> new SmaException("HOD Staff not found with id: " + request.getHodStaffId()));
            department.setHeadOfDepartment(hodStaff);
        }
        
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
     * Get departments by school and type
     */
    public List<DepartmentResponse> getDepartmentsByType(ServiceRequestContext context,
                                                         Long schoolId,
                                                         String departmentType) throws SmaException {
        SchoolProfile school = schoolProfileRepository.findById(schoolId)
                .orElseThrow(() -> new SmaException("School not found with id: " + schoolId));

        List<DepartmentMaster> departments = departmentRepository.findBySchoolAndDepartmentTypeAndIsActiveTrue(school, departmentType);
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
        
        // Update HOD
        if (request.getHodStaffId() != null) {
            Staff hodStaff = staffRepository.findById(request.getHodStaffId())
                    .orElseThrow(() -> new SmaException("HOD Staff not found with id: " + request.getHodStaffId()));
            existingDepartment.setHeadOfDepartment(hodStaff);
        } else {
            existingDepartment.setHeadOfDepartment(null);
        }
        
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
     * Get staff members associated with a department
     */
    public List<DepartmentStaffResponse> getDepartmentStaff(ServiceRequestContext context, 
                                                           Long departmentId) throws SmaException {
        // Verify department exists
        DepartmentMaster department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new SmaException("Department not found with id: " + departmentId));

        // Get all staff mappings for this department
        List<DepartmentStaffMapping> mappings = departmentStaffMappingRepository.findByDepartmentId(departmentId);
        
        return mappings.stream()
                .map(this::convertMappingToStaffResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert DepartmentStaffMapping to DepartmentStaffResponse
     */
    private DepartmentStaffResponse convertMappingToStaffResponse(DepartmentStaffMapping mapping) {
        Staff staff = mapping.getStaff();
        DepartmentStaffResponse response = new DepartmentStaffResponse();
        
        response.setStaffId(staff.getId());
        response.setEmployeeCode(staff.getEmployeeCode());
        response.setFirstName(staff.getFirstName());
        response.setMiddleName(staff.getMiddleName());
        response.setLastName(staff.getLastName());
        
        // Build full name
        StringBuilder fullName = new StringBuilder();
        if (staff.getFirstName() != null) {
            fullName.append(staff.getFirstName());
        }
        if (staff.getMiddleName() != null && !staff.getMiddleName().isEmpty()) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(staff.getMiddleName());
        }
        if (staff.getLastName() != null) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(staff.getLastName());
        }
        response.setFullName(fullName.toString());
        
        response.setEmail(staff.getEmail());
        response.setPhone(staff.getPhoneNumber());
        response.setStaffType(staff.getStaffType());
        response.setDesignation(staff.getDesignation());
        
        // Set mapping-specific fields
        response.setRoleInDepartment(mapping.getRoleInDepartment());
        response.setIsPrimaryDepartment(mapping.getIsPrimaryDepartment());
        response.setAssignmentDate(mapping.getAssignmentDate());
        response.setMemberSince(mapping.getCreatedAt()); // createdAt as "Member Since"
        response.setRemarks(mapping.getRemarks());
        
        return response;
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
        
        // Extract HOD details from Staff relationship
        Staff hod = department.getHeadOfDepartment();
        if (hod != null) {
            response.setHodStaffId(hod.getId());
            response.setHodEmployeeCode(hod.getEmployeeCode());
            
            // Build full name from individual name fields
            StringBuilder fullName = new StringBuilder();
            if (hod.getFirstName() != null) {
                fullName.append(hod.getFirstName());
            }
            if (hod.getMiddleName() != null && !hod.getMiddleName().isEmpty()) {
                if (fullName.length() > 0) fullName.append(" ");
                fullName.append(hod.getMiddleName());
            }
            if (hod.getLastName() != null) {
                if (fullName.length() > 0) fullName.append(" ");
                fullName.append(hod.getLastName());
            }
            response.setHodFullName(fullName.toString());
            
            response.setHodEmail(hod.getEmail());
            response.setHodPhone(hod.getPhoneNumber());
        }
        
        response.setDescription(department.getDescription());
        return response;
    }
}
