package org.sma.admin.core.app.service;

import org.sma.admin.core.app.model.request.SubjectMasterRequest;
import org.sma.admin.core.app.model.response.SubjectMasterResponse;
import org.sma.jpa.model.master.SubjectMaster;
import org.sma.jpa.model.master.ClassMaster;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.repository.master.SubjectMasterRepository;
import org.sma.jpa.repository.master.ClassMasterRepository;
import org.sma.jpa.repository.school.SchoolProfileRepository;
import org.sma.platform.core.exception.SmaException;
import org.sma.platform.core.service.ServiceRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * SubjectMaster Business Service
 * Handles business logic for subject management
 */
@Component
public class SubjectMasterBusinessService {

    @Autowired
    private SubjectMasterRepository subjectMasterRepository;

    @Autowired
    private SchoolProfileRepository schoolProfileRepository;

    @Autowired
    private ClassMasterRepository classMasterRepository;

    /**
     * Create new subject
     */
    @Transactional
    public SubjectMasterResponse createSubject(ServiceRequestContext context, 
                                              SubjectMasterRequest request) throws SmaException {
        // Validate required fields
        if (request.getSchoolId() == null) {
            throw new SmaException("School ID is mandatory");
        }
        if (request.getClassId() == null || request.getClassId().isEmpty()) {
            throw new SmaException("Class ID is mandatory");
        }
        if (request.getSubjectCode() == null || request.getSubjectCode().isEmpty()) {
            throw new SmaException("Subject code is mandatory");
        }
        if (request.getSubjectName() == null || request.getSubjectName().isEmpty()) {
            throw new SmaException("Subject name is mandatory");
        }

        // Fetch school
        SchoolProfile school = schoolProfileRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new SmaException("School not found with ID: " + request.getSchoolId()));

        // Fetch class
        Long classIdLong;
        try {
            classIdLong = Long.parseLong(request.getClassId());
        } catch (NumberFormatException e) {
            throw new SmaException("Invalid Class ID format. Expected numeric string but got: " + request.getClassId());
        }
        
        ClassMaster classMaster = classMasterRepository.findById(classIdLong)
                .orElseThrow(() -> new SmaException("Class not found with ID: " + request.getClassId()));

        // Check if subject code already exists for this class
        Optional<SubjectMaster> existingSubject = subjectMasterRepository
                .findBySchoolAndClassMasterAndSubjectCode(school, classMaster, request.getSubjectCode());
        if (existingSubject.isPresent()) {
            throw new SmaException("Subject code already exists for this class: " + request.getSubjectCode());
        }

        // Create subject entity
        SubjectMaster subjectMaster = new SubjectMaster();
        subjectMaster.setSchool(school);
        subjectMaster.setClassMaster(classMaster);
        subjectMaster.setSubjectCode(request.getSubjectCode());
        subjectMaster.setSubjectName(request.getSubjectName());
        subjectMaster.setSubjectType(request.getSubjectType());
        subjectMaster.setIsMandatory(request.getIsMandatory() != null ? request.getIsMandatory() : true);
        subjectMaster.setCredits(request.getCredits());
        subjectMaster.setMaxMarks(request.getMaxMarks());
        subjectMaster.setPassMarks(request.getPassMarks());
        subjectMaster.setDescription(request.getDescription());
        
        SubjectMaster savedSubject = subjectMasterRepository.save(subjectMaster);
        return convertToResponse(savedSubject);
    }

    /**
     * Get subject by ID
     */
    public SubjectMasterResponse getSubject(ServiceRequestContext context, Long subjectId) throws SmaException {
        SubjectMaster subjectMaster = subjectMasterRepository.findById(subjectId)
                .orElseThrow(() -> new SmaException("Subject not found with id: " + subjectId));
        
        return convertToResponse(subjectMaster);
    }

    /**
     * Get all subjects for a school
     */
    public List<SubjectMasterResponse> getAllSubjectsBySchool(ServiceRequestContext context, Long schoolId) throws SmaException {
        SchoolProfile school = schoolProfileRepository.findById(schoolId)
                .orElseThrow(() -> new SmaException("School not found with ID: " + schoolId));
        
        List<SubjectMaster> subjects = subjectMasterRepository.findBySchoolAndIsActiveTrue(school);
        return subjects.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all subjects for a class
     */
    public List<SubjectMasterResponse> getSubjectsByClass(ServiceRequestContext context, 
                                                          Long schoolId, 
                                                          Long classId) throws SmaException {
        SchoolProfile school = schoolProfileRepository.findById(schoolId)
                .orElseThrow(() -> new SmaException("School not found with ID: " + schoolId));
        
        ClassMaster classMaster = classMasterRepository.findById(classId)
                .orElseThrow(() -> new SmaException("Class not found with ID: " + classId));
        
        List<SubjectMaster> subjects = subjectMasterRepository
                .findBySchoolAndClassMasterAndIsActiveTrue(school, classMaster);
        return subjects.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get subjects by type
     */
    public List<SubjectMasterResponse> getSubjectsByType(ServiceRequestContext context, 
                                                         Long schoolId, 
                                                         String subjectType) throws SmaException {
        SchoolProfile school = schoolProfileRepository.findById(schoolId)
                .orElseThrow(() -> new SmaException("School not found with ID: " + schoolId));
        
        List<SubjectMaster> subjects = subjectMasterRepository
                .findBySchoolAndSubjectTypeAndIsActiveTrue(school, subjectType);
        return subjects.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update subject
     */
    @Transactional
    public SubjectMasterResponse updateSubject(ServiceRequestContext context, 
                                              Long subjectId, 
                                              SubjectMasterRequest request) throws SmaException {
        SubjectMaster existingSubject = subjectMasterRepository.findById(subjectId)
                .orElseThrow(() -> new SmaException("Subject not found with id: " + subjectId));

        // Update fields
        existingSubject.setSubjectCode(request.getSubjectCode());
        existingSubject.setSubjectName(request.getSubjectName());
        existingSubject.setSubjectType(request.getSubjectType());
        existingSubject.setIsMandatory(request.getIsMandatory());
        existingSubject.setCredits(request.getCredits());
        existingSubject.setMaxMarks(request.getMaxMarks());
        existingSubject.setPassMarks(request.getPassMarks());
        existingSubject.setDescription(request.getDescription());
        
        SubjectMaster updatedSubject = subjectMasterRepository.save(existingSubject);
        return convertToResponse(updatedSubject);
    }

    /**
     * Delete subject
     */
    @Transactional
    public void deleteSubject(ServiceRequestContext context, Long subjectId) throws SmaException {
        SubjectMaster existingSubject = subjectMasterRepository.findById(subjectId)
                .orElseThrow(() -> new SmaException("Subject not found with id: " + subjectId));
        
        subjectMasterRepository.delete(existingSubject);
    }

    /**
     * Convert entity to response DTO
     */
    private SubjectMasterResponse convertToResponse(SubjectMaster subjectMaster) {
        SubjectMasterResponse response = new SubjectMasterResponse();
        response.setId(subjectMaster.getId() != null ? subjectMaster.getId().toString() : null);
        response.setSchoolId(subjectMaster.getSchool() != null ? subjectMaster.getSchool().getId() : null);
        response.setClassId(subjectMaster.getClassMaster() != null ? subjectMaster.getClassMaster().getId().toString() : null);
        response.setClassName(subjectMaster.getClassMaster() != null ? subjectMaster.getClassMaster().getClassName() : null);
        response.setSubjectCode(subjectMaster.getSubjectCode());
        response.setSubjectName(subjectMaster.getSubjectName());
        response.setSubjectType(subjectMaster.getSubjectType());
        response.setIsMandatory(subjectMaster.getIsMandatory());
        response.setCredits(subjectMaster.getCredits());
        response.setMaxMarks(subjectMaster.getMaxMarks());
        response.setPassMarks(subjectMaster.getPassMarks());
        response.setDescription(subjectMaster.getDescription());
        return response;
    }
}
