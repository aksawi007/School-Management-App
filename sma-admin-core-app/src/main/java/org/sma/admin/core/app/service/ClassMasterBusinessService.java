package org.sma.admin.core.app.service;

import org.sma.admin.core.app.model.request.ClassMasterRequest;
import org.sma.admin.core.app.model.response.ClassMasterResponse;
import org.sma.jpa.model.master.ClassMaster;
import org.sma.jpa.model.school.AcademicYear;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.repository.master.ClassMasterRepository;
import org.sma.jpa.repository.school.AcademicYearRepository;
import org.sma.jpa.repository.school.SchoolProfileRepository;
import org.sma.platform.core.exception.SmaException;
import org.sma.platform.core.service.ServiceRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ClassMaster Business Service
 * Handles business logic for class/grade management
 */
@Component
public class ClassMasterBusinessService {

    @Autowired
    private ClassMasterRepository classMasterRepository;

    @Autowired
    private SchoolProfileRepository schoolProfileRepository;

    @Autowired
    private AcademicYearRepository academicYearRepository;

    /**
     * Create new class
     */
    @Transactional
    public ClassMasterResponse createClass(ServiceRequestContext context, 
                                          ClassMasterRequest request) throws SmaException {
        // Validate required fields
        if (request.getSchoolId() == null) {
            throw new SmaException("School ID is mandatory");
        }
        if (request.getAcademicYearId() == null) {
            throw new SmaException("Academic Year ID is mandatory");
        }
        if (request.getClassCode() == null || request.getClassCode().isEmpty()) {
            throw new SmaException("Class code is mandatory");
        }
        if (request.getClassName() == null || request.getClassName().isEmpty()) {
            throw new SmaException("Class name is mandatory");
        }

        // Fetch school
        SchoolProfile school = schoolProfileRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new SmaException("School not found with ID: " + request.getSchoolId()));

        // Fetch academic year
        AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                .orElseThrow(() -> new SmaException("Academic Year not found with ID: " + request.getAcademicYearId()));

        // Check if class code already exists
        Optional<ClassMaster> existingClass = classMasterRepository.findBySchoolAndClassCode(school, request.getClassCode());
        if (existingClass.isPresent()) {
            throw new SmaException("Class code already exists: " + request.getClassCode());
        }

        // Create class entity
        ClassMaster classMaster = new ClassMaster();
        classMaster.setSchool(school);
        classMaster.setAcademicYear(academicYear);
        classMaster.setClassCode(request.getClassCode());
        classMaster.setClassName(request.getClassName());
        classMaster.setDisplayOrder(request.getDisplayOrder());
        classMaster.setDescription(request.getDescription());
        
        ClassMaster savedClass = classMasterRepository.save(classMaster);
        return convertToResponse(savedClass);
    }

    /**
     * Get class by ID
     */
    public ClassMasterResponse getClass(ServiceRequestContext context, Long classId) throws SmaException {
        ClassMaster classMaster = classMasterRepository.findById(classId)
                .orElseThrow(() -> new SmaException("Class not found with id: " + classId));
        
        return convertToResponse(classMaster);
    }

    /**
     * Get all classes for a school
     */
    public List<ClassMasterResponse> getAllClassesBySchool(ServiceRequestContext context, Long schoolId) throws SmaException {
        SchoolProfile school = schoolProfileRepository.findById(schoolId)
                .orElseThrow(() -> new SmaException("School not found with ID: " + schoolId));
        
        List<ClassMaster> classes = classMasterRepository.findBySchoolAndIsActiveTrue(school);
        return classes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update class
     */
    @Transactional
    public ClassMasterResponse updateClass(ServiceRequestContext context, 
                                          Long classId, 
                                          ClassMasterRequest request) throws SmaException {
        ClassMaster existingClass = classMasterRepository.findById(classId)
                .orElseThrow(() -> new SmaException("Class not found with id: " + classId));

        // Fetch academic year if provided
        if (request.getAcademicYearId() != null) {
            AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                    .orElseThrow(() -> new SmaException("Academic Year not found with ID: " + request.getAcademicYearId()));
            existingClass.setAcademicYear(academicYear);
        }

        // Update fields
        existingClass.setClassCode(request.getClassCode());
        existingClass.setClassName(request.getClassName());
        existingClass.setDisplayOrder(request.getDisplayOrder());
        existingClass.setDescription(request.getDescription());
        
        ClassMaster updatedClass = classMasterRepository.save(existingClass);
        return convertToResponse(updatedClass);
    }

    /**
     * Delete class
     */
    @Transactional
    public void deleteClass(ServiceRequestContext context, Long classId) throws SmaException {
        ClassMaster existingClass = classMasterRepository.findById(classId)
                .orElseThrow(() -> new SmaException("Class not found with id: " + classId));
        
        classMasterRepository.delete(existingClass);
    }

    /**
     * Convert entity to response DTO
     */
    private ClassMasterResponse convertToResponse(ClassMaster classMaster) {
        ClassMasterResponse response = new ClassMasterResponse();
        response.setId(classMaster.getId() != null ? classMaster.getId().toString() : null);
        response.setSchoolId(classMaster.getSchool() != null ? classMaster.getSchool().getId() : null);
        response.setAcademicYearId(classMaster.getAcademicYear() != null ? classMaster.getAcademicYear().getId() : null);
        response.setAcademicYearName(classMaster.getAcademicYear() != null ? classMaster.getAcademicYear().getYearName() : null);
        response.setClassCode(classMaster.getClassCode());
        response.setClassName(classMaster.getClassName());
        response.setDisplayOrder(classMaster.getDisplayOrder());
        response.setDescription(classMaster.getDescription());
        return response;
    }
}
