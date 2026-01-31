package org.sma.admin.core.app.service;

import org.sma.admin.core.app.model.request.StaffSubjectMappingRequest;
import org.sma.admin.core.app.model.response.StaffSubjectMappingResponse;
import org.sma.jpa.model.master.SubjectMaster;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.staff.Staff;
import org.sma.jpa.model.staff.StaffSubjectMapping;
import org.sma.jpa.repository.master.SubjectMasterRepository;
import org.sma.jpa.repository.school.SchoolProfileRepository;
import org.sma.jpa.repository.staff.StaffRepository;
import org.sma.jpa.repository.staff.StaffSubjectMappingRepository;
import org.sma.platform.core.exception.SmaException;
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
}
