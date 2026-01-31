package org.sma.admin.core.app.service;

import org.sma.admin.core.app.model.request.StaffSubjectMappingRequest;
import org.sma.admin.core.app.model.response.StaffSubjectMappingResponse;
import org.sma.admin.core.app.model.response.DepartmentStaffResponse;
import org.sma.jpa.model.master.SubjectMaster;
import org.sma.jpa.model.master.DepartmentMaster;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.staff.Staff;
import org.sma.jpa.model.staff.StaffSubjectMapping;
import org.sma.jpa.model.staff.DepartmentStaffMapping;
import org.sma.jpa.repository.master.SubjectMasterRepository;
import org.sma.jpa.repository.school.SchoolProfileRepository;
import org.sma.jpa.repository.staff.StaffRepository;
import org.sma.jpa.repository.staff.StaffSubjectMappingRepository;
import org.sma.jpa.repository.staff.DepartmentStaffMappingRepository;
import org.sma.platform.core.exception.SmaException;
import org.sma.platform.core.service.ServiceRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Business service for staff-subject mapping operations
 */
@Service
@Transactional
public class StaffSubjectMappingBusinessService {

    @Autowired
    private StaffSubjectMappingRepository staffSubjectMappingRepository;

    @Autowired
    private SchoolProfileRepository schoolRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private SubjectMasterRepository subjectRepository;

    @Autowired
    DepartmentBusinessService departmentBusinessService;

    /**
     * Create a new staff-subject mapping
     */
    public StaffSubjectMappingResponse createMapping(StaffSubjectMappingRequest request) throws SmaException {
        // Validate if mapping already exists
        if (staffSubjectMappingRepository.findByStaffAndSubject(
                request.getSchoolId(), request.getStaffId(), request.getSubjectId()).isPresent()) {
            throw new SmaException("Staff is already mapped to this subject");
        }

        StaffSubjectMapping mapping = new StaffSubjectMapping();
        populateMappingFromRequest(mapping, request);
        
        StaffSubjectMapping saved = staffSubjectMappingRepository.save(mapping);
        return mapToResponse(saved);
    }

    /**
     * Update an existing mapping
     */
    public StaffSubjectMappingResponse updateMapping(Long mappingId, StaffSubjectMappingRequest request) throws SmaException {
        StaffSubjectMapping mapping = staffSubjectMappingRepository.findById(mappingId)
                .orElseThrow(() -> new SmaException("Staff-subject mapping not found"));

        mapping.setProficiencyLevel(request.getProficiencyLevel());
        mapping.setCanTeachPrimary(request.getCanTeachPrimary());
        mapping.setCanTeachSecondary(request.getCanTeachSecondary());
        mapping.setCanTeachHigherSecondary(request.getCanTeachHigherSecondary());
        mapping.setRemarks(request.getRemarks());

        StaffSubjectMapping updated = staffSubjectMappingRepository.save(mapping);
        return mapToResponse(updated);
    }

    /**
     * Delete a mapping
     */
    public void deleteMapping(Long mappingId) throws SmaException {
        StaffSubjectMapping mapping = staffSubjectMappingRepository.findById(mappingId)
                .orElseThrow(() -> new SmaException("Staff-subject mapping not found"));
        
        mapping.setIsActive(false);
        staffSubjectMappingRepository.save(mapping);
    }

    /**
     * Get all subjects for a staff member
     */
    public List<StaffSubjectMappingResponse> getSubjectsForStaff(Long schoolId, Long staffId) {
        List<StaffSubjectMapping> mappings = staffSubjectMappingRepository.findByStaffId(schoolId, staffId);
        return mappings.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Get qualified teachers for a subject
     */
    public List<StaffSubjectMappingResponse> getQualifiedTeachersForSubject(Long schoolId, Long subjectId) {
        List<StaffSubjectMapping> mappings = staffSubjectMappingRepository.findQualifiedTeachersForSubject(schoolId, subjectId);
        return mappings.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Get qualified teachers for a class-subject combination
     */
    public List<StaffSubjectMappingResponse> getQualifiedTeachersForClassSubject(Long schoolId, Long classId, Long subjectId) {
        List<StaffSubjectMapping> mappings = staffSubjectMappingRepository.findQualifiedTeachersForClassSubject(schoolId, classId, subjectId);
        return mappings.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Get staff for a subject based on the subject's linked department
     * Uses getDepartmentStaff logic: fetch departmentId from subject, then get all staff in that department
     * Returns DepartmentStaffResponse list instead of StaffSubjectMappingResponse
     * @throws SmaException if subject is not linked to any department
     */
    public List<DepartmentStaffResponse> getStaffBySubjectDepartment(ServiceRequestContext context, Long schoolId, Long subjectId, Long departmentId) throws SmaException {
        // Get subject and fetch its department
        if(departmentId==null) {
            SubjectMaster subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new SmaException("Subject not found with id: " + subjectId));

            DepartmentMaster department = subject.getDepartment();
            departmentId = department.getId();
            if (department == null) {
                throw new SmaException("Subject is not linked to any department");
            }
        }
        // Get all staff mappings for this department
       List<DepartmentStaffResponse> response = departmentBusinessService.getDepartmentStaff(context, departmentId);
            return response;
    }

    /**
     * Get all mappings for a school
     */
    public List<StaffSubjectMappingResponse> getAllMappingsForSchool(Long schoolId) {
        List<StaffSubjectMapping> mappings = staffSubjectMappingRepository.findBySchoolId(schoolId);
        return mappings.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // Helper methods
    private void populateMappingFromRequest(StaffSubjectMapping mapping, StaffSubjectMappingRequest request) throws SmaException {
        SchoolProfile school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new SmaException("School not found"));
        Staff staff = staffRepository.findById(request.getStaffId())
                .orElseThrow(() -> new SmaException("Staff not found"));
        SubjectMaster subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new SmaException("Subject not found"));

        mapping.setSchool(school);
        mapping.setStaff(staff);
        mapping.setSubject(subject);
        mapping.setProficiencyLevel(request.getProficiencyLevel());
        mapping.setCanTeachPrimary(request.getCanTeachPrimary());
        mapping.setCanTeachSecondary(request.getCanTeachSecondary());
        mapping.setCanTeachHigherSecondary(request.getCanTeachHigherSecondary());
        mapping.setRemarks(request.getRemarks());
        mapping.setIsActive(true);
    }

    private StaffSubjectMappingResponse mapToResponse(StaffSubjectMapping mapping) {
        StaffSubjectMappingResponse response = new StaffSubjectMappingResponse();
        response.setId(mapping.getId());
        response.setSchoolId(mapping.getSchool().getId());
        response.setStaffId(mapping.getStaff().getId());
        response.setStaffName(mapping.getStaff().getFirstName() + " " + mapping.getStaff().getLastName());
        response.setEmployeeCode(mapping.getStaff().getEmployeeCode());
        response.setSubjectId(mapping.getSubject().getId());
        response.setSubjectName(mapping.getSubject().getSubjectName());
        response.setSubjectCode(mapping.getSubject().getSubjectCode());
        response.setProficiencyLevel(mapping.getProficiencyLevel());
        response.setCanTeachPrimary(mapping.getCanTeachPrimary());
        response.setCanTeachSecondary(mapping.getCanTeachSecondary());
        response.setCanTeachHigherSecondary(mapping.getCanTeachHigherSecondary());
        response.setIsActive(mapping.getIsActive());
        response.setRemarks(mapping.getRemarks());
        return response;
    }

    /**
     * Convert DepartmentStaffMapping to DepartmentStaffResponse
     * Similar logic to DepartmentBusinessService but reused here for consistency
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
        response.setMemberSince(mapping.getCreatedAt());
        response.setRemarks(mapping.getRemarks());
        
        return response;
    }
}
