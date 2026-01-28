package org.sma.admin.core.app.service;

import org.sma.admin.core.app.model.request.SectionMasterRequest;
import org.sma.admin.core.app.model.response.SectionMasterResponse;
import org.sma.jpa.model.master.ClassMaster;
import org.sma.jpa.model.master.SectionMaster;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.repository.master.ClassMasterRepository;
import org.sma.jpa.repository.master.SectionMasterRepository;
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
 * SectionMaster Business Service
 * Handles business logic for section management
 */
@Component
public class SectionMasterBusinessService {

    @Autowired
    private SectionMasterRepository sectionMasterRepository;

    @Autowired
    private ClassMasterRepository classMasterRepository;

    @Autowired
    private SchoolProfileRepository schoolProfileRepository;

    /**
     * Create new section
     */
    @Transactional
    public SectionMasterResponse createSection(ServiceRequestContext context, 
                                               SectionMasterRequest request) throws SmaException {
        // Validate required fields
        if (request.getSchoolId() == null) {
            throw new SmaException("School ID is mandatory");
        }
        if (request.getClassId() == null) {
            throw new SmaException("Class ID is mandatory");
        }
        if (request.getSectionCode() == null || request.getSectionCode().isEmpty()) {
            throw new SmaException("Section code is mandatory");
        }
        if (request.getSectionName() == null || request.getSectionName().isEmpty()) {
            throw new SmaException("Section name is mandatory");
        }

        // Fetch school and class
        SchoolProfile school = schoolProfileRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new SmaException("School not found with ID: " + request.getSchoolId()));
        
        // Validate classId format before parsing
        String classId = request.getClassId();
        if (classId == null || classId.isEmpty()) {
            throw new SmaException("Class ID is mandatory");
        }
        
        // Try to parse Long
        Long classIdLong;
        try {
            classIdLong = Long.parseLong(classId);
        } catch (NumberFormatException e) {
            throw new SmaException("Invalid Class ID format. Expected numeric string but got: " + classId);
        }
        
        ClassMaster classMaster = classMasterRepository.findById(classIdLong)
                .orElseThrow(() -> new SmaException("Class not found with ID: " + classId));

        // Check if section code already exists for this class
        Optional<SectionMaster> existingSection = sectionMasterRepository
                .findBySchoolAndClassMasterAndSectionCode(school, classMaster, request.getSectionCode());
        if (existingSection.isPresent()) {
            throw new SmaException("Section code already exists for this class: " + request.getSectionCode());
        }

        // Create section entity
        SectionMaster sectionMaster = new SectionMaster();
        sectionMaster.setSchool(school);
        sectionMaster.setClassMaster(classMaster);
        sectionMaster.setSectionCode(request.getSectionCode());
        sectionMaster.setSectionName(request.getSectionName());
        sectionMaster.setCapacity(request.getCapacity());
        sectionMaster.setRoomNumber(request.getRoomNumber());
        sectionMaster.setDescription(request.getDescription());
        
        SectionMaster savedSection = sectionMasterRepository.save(sectionMaster);
        return convertToResponse(savedSection);
    }

    /**
     * Get section by ID
     */
    public SectionMasterResponse getSection(ServiceRequestContext context, Long sectionId) throws SmaException {
        SectionMaster sectionMaster = sectionMasterRepository.findById(sectionId)
                .orElseThrow(() -> new SmaException("Section not found with id: " + sectionId));
        
        return convertToResponse(sectionMaster);
    }

    /**
     * Get all sections for a class
     */
    public List<SectionMasterResponse> getSectionsByClass(ServiceRequestContext context, 
                                                          Long schoolId, 
                                                          Long classId) throws SmaException {
        SchoolProfile school = schoolProfileRepository.findById(schoolId)
                .orElseThrow(() -> new SmaException("School not found with ID: " + schoolId));
        
        ClassMaster classMaster = classMasterRepository.findById(classId)
                .orElseThrow(() -> new SmaException("Class not found with ID: " + classId));
        
        List<SectionMaster> sections = sectionMasterRepository
                .findBySchoolAndClassMasterAndIsActiveTrue(school, classMaster);
        return sections.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update section
     */
    @Transactional
    public SectionMasterResponse updateSection(ServiceRequestContext context, 
                                              Long sectionId, 
                                              SectionMasterRequest request) throws SmaException {
        SectionMaster existingSection = sectionMasterRepository.findById(sectionId)
                .orElseThrow(() -> new SmaException("Section not found with id: " + sectionId));

        // Update fields
        existingSection.setSectionCode(request.getSectionCode());
        existingSection.setSectionName(request.getSectionName());
        existingSection.setCapacity(request.getCapacity());
        existingSection.setRoomNumber(request.getRoomNumber());
        existingSection.setDescription(request.getDescription());
        
        SectionMaster updatedSection = sectionMasterRepository.save(existingSection);
        return convertToResponse(updatedSection);
    }

    /**
     * Delete section
     */
    @Transactional
    public void deleteSection(ServiceRequestContext context, Long sectionId) throws SmaException {
        SectionMaster existingSection = sectionMasterRepository.findById(sectionId)
                .orElseThrow(() -> new SmaException("Section not found with id: " + sectionId));
        
        sectionMasterRepository.delete(existingSection);
    }

    /**
     * Convert entity to response DTO
     */
    private SectionMasterResponse convertToResponse(SectionMaster sectionMaster) {
        SectionMasterResponse response = new SectionMasterResponse();
        response.setId(sectionMaster.getId() != null ? sectionMaster.getId().toString() : null);
        response.setSchoolId(sectionMaster.getSchool() != null ? sectionMaster.getSchool().getId() : null);
        response.setClassId(sectionMaster.getClassMaster() != null ? sectionMaster.getClassMaster().getId().toString() : null);
        response.setClassName(sectionMaster.getClassMaster() != null ? sectionMaster.getClassMaster().getClassName() : null);
        response.setSectionCode(sectionMaster.getSectionCode());
        response.setSectionName(sectionMaster.getSectionName());
        response.setCapacity(sectionMaster.getCapacity());
        response.setRoomNumber(sectionMaster.getRoomNumber());
        response.setDescription(sectionMaster.getDescription());
        return response;
    }
}
