package org.sma.admin.core.app.service;

import org.sma.admin.core.app.model.request.StudentClassSectionRequest;
import org.sma.admin.core.app.model.response.StudentClassSectionResponse;
import org.sma.jpa.model.master.ClassMaster;
import org.sma.jpa.model.master.SectionMaster;
import org.sma.jpa.model.school.AcademicYear;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.studentmgmt.StudentProfile;
import org.sma.jpa.model.student.StudentClassSectionMapping;
import org.sma.jpa.repository.master.ClassMasterRepository;
import org.sma.jpa.repository.master.SectionMasterRepository;
import org.sma.jpa.repository.school.AcademicYearRepository;
import org.sma.jpa.repository.school.SchoolProfileRepository;
import org.sma.jpa.repository.student.StudentClassSectionMappingRepository;
import org.sma.jpa.repository.studentmgmt.StudentProfileRepository;
import org.sma.platform.core.exception.SmaException;
import org.sma.platform.core.service.ServiceRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Student Class Section Business Service
 * Manages student assignments to classes and sections
 */
@Component
public class StudentClassSectionBusinessService {

    @Autowired
    private StudentClassSectionMappingRepository mappingRepository;

    @Autowired
    private StudentProfileRepository studentRepository;

    @Autowired
    private SchoolProfileRepository schoolProfileRepository;

    @Autowired
    private AcademicYearRepository academicYearRepository;

    @Autowired
    private ClassMasterRepository classMasterRepository;

    @Autowired
    private SectionMasterRepository sectionMasterRepository;

    /**
     * Assign student to a class and section
     */
    @Transactional
    public StudentClassSectionResponse assignStudentToClassSection(
            ServiceRequestContext context,
            StudentClassSectionRequest request) throws SmaException {

        // Validate required fields
        validateRequest(request);

        // Fetch entities
        StudentProfile student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new SmaException("Student not found with id: " + request.getStudentId()));

        SchoolProfile school = schoolProfileRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new SmaException("School not found with id: " + request.getSchoolId()));

        AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                .orElseThrow(() -> new SmaException("Academic year not found with id: " + request.getAcademicYearId()));

        ClassMaster classMaster = classMasterRepository.findById(request.getClassId())
                .orElseThrow(() -> new SmaException("Class not found with id: " + request.getClassId()));

        SectionMaster section = sectionMasterRepository.findById(request.getSectionId())
                .orElseThrow(() -> new SmaException("Section not found with id: " + request.getSectionId()));

        // Check if student already has an active mapping for this academic year
        Optional<StudentClassSectionMapping> existingMapping = 
                mappingRepository.findActiveByStudentAndAcademicYear(request.getStudentId(), request.getAcademicYearId());

        if (existingMapping.isPresent()) {
            throw new SmaException("Student is already assigned to a class/section for this academic year. " +
                    "Please update the existing assignment instead.");
        }

        // Check section capacity
        Long currentCount = mappingRepository.countByClassAndSection(request.getClassId(), request.getSectionId());
        if (section.getCapacity() != null && currentCount >= section.getCapacity()) {
            throw new SmaException("Section capacity exceeded. Current: " + currentCount + ", Max: " + section.getCapacity());
        }

        // Create new mapping
        StudentClassSectionMapping mapping = new StudentClassSectionMapping();
        mapping.setStudent(student);
        mapping.setSchool(school);
        mapping.setAcademicYear(academicYear);
        mapping.setClassMaster(classMaster);
        mapping.setSection(section);
        mapping.setEnrollmentDate(request.getEnrollmentDate() != null ? request.getEnrollmentDate() : LocalDate.now());
        mapping.setRollNumber(request.getRollNumber());
        mapping.setIsActive(true);
        mapping.setRemarks(request.getRemarks());

        StudentClassSectionMapping savedMapping = mappingRepository.save(mapping);
        return convertToResponse(savedMapping);
    }

    /**
     * Update student's class and section assignment
     */
    @Transactional
    public StudentClassSectionResponse updateStudentClassSection(
            ServiceRequestContext context,
            Long mappingId,
            StudentClassSectionRequest request) throws SmaException {

        StudentClassSectionMapping existingMapping = mappingRepository.findById(mappingId)
                .orElseThrow(() -> new SmaException("Mapping not found with id: " + mappingId));

        // Fetch new entities if they're being changed
        if (request.getClassId() != null) {
            ClassMaster classMaster = classMasterRepository.findById(request.getClassId())
                    .orElseThrow(() -> new SmaException("Class not found with id: " + request.getClassId()));
            existingMapping.setClassMaster(classMaster);
        }

        if (request.getSectionId() != null) {
            SectionMaster section = sectionMasterRepository.findById(request.getSectionId())
                    .orElseThrow(() -> new SmaException("Section not found with id: " + request.getSectionId()));
            
            // Check section capacity if changing section
            if (!existingMapping.getSection().getId().equals(section.getId())) {
                Long currentCount = mappingRepository.countByClassAndSection(
                        existingMapping.getClassMaster().getId(), 
                        request.getSectionId());
                if (section.getCapacity() != null && currentCount >= section.getCapacity()) {
                    throw new SmaException("Section capacity exceeded. Current: " + currentCount + ", Max: " + section.getCapacity());
                }
            }
            
            existingMapping.setSection(section);
        }

        if (request.getRollNumber() != null) {
            existingMapping.setRollNumber(request.getRollNumber());
        }

        if (request.getRemarks() != null) {
            existingMapping.setRemarks(request.getRemarks());
        }

        StudentClassSectionMapping updatedMapping = mappingRepository.save(existingMapping);
        return convertToResponse(updatedMapping);
    }

    /**
     * Get students by class and section
     */
    public List<StudentClassSectionResponse> getStudentsByClassAndSection(
            ServiceRequestContext context,
            Long academicYearId,
            Long classId,
            Long sectionId) throws SmaException {

        List<StudentClassSectionMapping> mappings;
        
        if (sectionId != null) {
            mappings = mappingRepository.findByAcademicYearAndClassAndSection(academicYearId, classId, sectionId);
        } else {
            mappings = mappingRepository.findByAcademicYearAndClass(academicYearId, classId);
        }

        return mappings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get student's current class and section
     */
    public StudentClassSectionResponse getStudentCurrentAssignment(
            ServiceRequestContext context,
            Long studentId,
            Long academicYearId) throws SmaException {

        StudentClassSectionMapping mapping = mappingRepository
                .findActiveByStudentAndAcademicYear(studentId, academicYearId)
                .orElseThrow(() -> new SmaException("No active assignment found for student in this academic year"));

        return convertToResponse(mapping);
    }

    /**
     * Get student's enrollment history
     */
    public List<StudentClassSectionResponse> getStudentHistory(
            ServiceRequestContext context,
            Long studentId) throws SmaException {

        List<StudentClassSectionMapping> mappings = mappingRepository.findStudentHistory(studentId);
        return mappings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Deactivate student's current assignment (for transfers/withdrawals)
     */
    @Transactional
    public void deactivateStudentAssignment(
            ServiceRequestContext context,
            Long mappingId) throws SmaException {

        StudentClassSectionMapping mapping = mappingRepository.findById(mappingId)
                .orElseThrow(() -> new SmaException("Mapping not found with id: " + mappingId));

        mapping.setIsActive(false);
        mappingRepository.save(mapping);
    }

    /**
     * Validate request
     */
    private void validateRequest(StudentClassSectionRequest request) throws SmaException {
        if (request.getStudentId() == null) {
            throw new SmaException("Student ID is mandatory");
        }
        if (request.getSchoolId() == null) {
            throw new SmaException("School ID is mandatory");
        }
        if (request.getAcademicYearId() == null) {
            throw new SmaException("Academic Year ID is mandatory");
        }
        if (request.getClassId() == null) {
            throw new SmaException("Class ID is mandatory");
        }
        if (request.getSectionId() == null) {
            throw new SmaException("Section ID is mandatory");
        }
    }

    /**
     * Convert entity to response DTO
     */
    private StudentClassSectionResponse convertToResponse(StudentClassSectionMapping mapping) {
        StudentClassSectionResponse response = new StudentClassSectionResponse();
        
        response.setMappingId(mapping.getId());
        response.setStudentId(mapping.getStudent().getId());
        response.setAdmissionNumber(mapping.getStudent().getAdmissionNo());
        
        // Build student name
        StudentProfile student = mapping.getStudent();
        StringBuilder fullName = new StringBuilder();
        if (student.getFirstName() != null) {
            fullName.append(student.getFirstName());
        }
        if (student.getMiddleName() != null && !student.getMiddleName().isEmpty()) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(student.getMiddleName());
        }
        if (student.getLastName() != null) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(student.getLastName());
        }
        response.setStudentName(fullName.toString());
        response.setFirstName(student.getFirstName());
        response.setMiddleName(student.getMiddleName());
        response.setLastName(student.getLastName());
        
        response.setSchoolId(mapping.getSchool().getId());
        response.setAcademicYearId(mapping.getAcademicYear().getId());
        response.setAcademicYearName(mapping.getAcademicYear().getYearName());
        response.setClassId(mapping.getClassMaster().getId());
        response.setClassName(mapping.getClassMaster().getClassName());
        response.setClassCode(mapping.getClassMaster().getClassCode());
        response.setSectionId(mapping.getSection().getId());
        response.setSectionName(mapping.getSection().getSectionName());
        response.setSectionCode(mapping.getSection().getSectionCode());
        response.setEnrollmentDate(mapping.getEnrollmentDate());
        response.setRollNumber(mapping.getRollNumber());
        response.setIsActive(mapping.getIsActive());
        response.setRemarks(mapping.getRemarks());
        
        return response;
    }
}
