package org.sma.student.mngt.app.service;

import org.sma.student.mngt.app.dto.EnrollStudentRequest;
import org.sma.student.mngt.app.dto.EnrollmentDto;
import org.sma.jpa.model.studentmgmt.Enrollment;
import org.sma.jpa.model.studentmgmt.StudentProfile;
import org.sma.jpa.repository.studentmgmt.EnrollmentRepository;
import org.sma.jpa.repository.studentmgmt.StudentProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentProfileRepository StudentProfileRepository;

    @Transactional
    public Enrollment enrollStudent(UUID schoolId, EnrollStudentRequest request, String createdBy) {
        // Validate StudentProfile exists and belongs to school
        StudentProfile StudentProfile = StudentProfileRepository.findByIdAndSchoolIdAndIsDeletedFalse(request.getStudentId(), schoolId)
                .orElseThrow(() -> new RuntimeException("StudentProfile not found"));

        // Check if StudentProfile already has active enrollment for this academic year
        enrollmentRepository.findByStudentIdAndAcademicYearIdAndStatusAndIsDeletedFalse(
                        request.getStudentId(), request.getAcademicYearId(), "ACTIVE")
                .ifPresent(e -> {
                    throw new RuntimeException("StudentProfile already has active enrollment for this academic year");
                });

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(request.getStudentId());
        enrollment.setSchoolId(schoolId);
        enrollment.setAcademicYearId(request.getAcademicYearId());
        enrollment.setClassId(request.getClassId());
        enrollment.setSectionId(request.getSectionId());
        enrollment.setRollNo(request.getRollNo());
        enrollment.setStartDate(request.getStartDate());
        enrollment.setStatus("ACTIVE");
        enrollment.setRemarks(request.getRemarks());
        enrollment.setCreatedBy(createdBy);

        return enrollmentRepository.save(enrollment);
    }

    public List<EnrollmentDto> getStudentEnrollmentHistory(UUID studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdOrderByStartDateDesc(studentId);
        return enrollments.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public Enrollment promoteStudent(UUID schoolId, UUID studentId, UUID newAcademicYearId, 
                                      UUID newClassId, UUID newSectionId, String promotedBy) {
        // End current enrollment
        Enrollment currentEnrollment = enrollmentRepository
                .findByStudentIdAndStatusAndIsDeletedFalse(studentId, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("No active enrollment found for StudentProfile"));

        currentEnrollment.setStatus("ENDED");
        currentEnrollment.setEndDate(LocalDate.now());
        currentEnrollment.setEndReason("PROMOTION");
        currentEnrollment.setUpdatedBy(promotedBy);
        enrollmentRepository.save(currentEnrollment);

        // Create new enrollment
        Enrollment newEnrollment = new Enrollment();
        newEnrollment.setStudentId(studentId);
        newEnrollment.setSchoolId(schoolId);
        newEnrollment.setAcademicYearId(newAcademicYearId);
        newEnrollment.setClassId(newClassId);
        newEnrollment.setSectionId(newSectionId);
        newEnrollment.setStartDate(LocalDate.now());
        newEnrollment.setStatus("ACTIVE");
        newEnrollment.setRemarks("Promoted from previous class");
        newEnrollment.setCreatedBy(promotedBy);

        return enrollmentRepository.save(newEnrollment);
    }

    @Transactional
    public void withdrawStudent(UUID studentId, String reason, String withdrawnBy) {
        Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndStatusAndIsDeletedFalse(studentId, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("No active enrollment found for StudentProfile"));

        enrollment.setStatus("ENDED");
        enrollment.setEndDate(LocalDate.now());
        enrollment.setEndReason(reason != null ? reason : "WITHDRAWAL");
        enrollment.setUpdatedBy(withdrawnBy);
        enrollmentRepository.save(enrollment);

        // Update StudentProfile status
        StudentProfileRepository.findById(studentId).ifPresent(StudentProfile -> {
            StudentProfile.setStatus("WITHDRAWN");
            StudentProfile.setUpdatedBy(withdrawnBy);
            StudentProfileRepository.save(StudentProfile);
        });
    }

    private EnrollmentDto mapToDto(Enrollment enrollment) {
        EnrollmentDto dto = new EnrollmentDto();
        dto.setId(enrollment.getId());
        dto.setStudentId(enrollment.getStudentId());
        dto.setSchoolId(enrollment.getSchoolId());
        dto.setAcademicYearId(enrollment.getAcademicYearId());
        dto.setClassId(enrollment.getClassId());
        dto.setSectionId(enrollment.getSectionId());
        dto.setRollNo(enrollment.getRollNo());
        dto.setStartDate(enrollment.getStartDate());
        dto.setEndDate(enrollment.getEndDate());
        dto.setStatus(enrollment.getStatus());
        dto.setEndReason(enrollment.getEndReason());
        dto.setRemarks(enrollment.getRemarks());
        return dto;
    }
}


