package org.sma.student.mngt.app.service;

import org.sma.student.mngt.app.dto.*;
import org.sma.jpa.model.studentmgmt.*;
import org.sma.jpa.repository.studentmgmt.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    @Autowired
    private StudentProfileRepository StudentProfileRepository;

    @Autowired
    private GuardianRepository guardianRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Transactional
    public StudentProfile createStudent(CreateStudentRequest request, String createdBy) {
        // Check if admission number already exists
        if (request.getAdmissionNo() != null) {
            StudentProfileRepository.findBySchoolIdAndAdmissionNo(request.getSchoolId(), request.getAdmissionNo())
                    .ifPresent(s -> {
                        throw new RuntimeException("Admission number already exists: " + request.getAdmissionNo());
                    });
        } else {
            // Auto-generate admission number
            request.setAdmissionNo(generateAdmissionNumber(request.getSchoolId()));
        }

        // Create StudentProfile
        StudentProfile StudentProfile = new StudentProfile();
        StudentProfile.setSchoolId(request.getSchoolId());
        StudentProfile.setAdmissionNo(request.getAdmissionNo());
        StudentProfile.setFirstName(request.getFirstName());
        StudentProfile.setMiddleName(request.getMiddleName());
        StudentProfile.setLastName(request.getLastName());
        StudentProfile.setGender(request.getGender());
        StudentProfile.setDateOfBirth(request.getDateOfBirth());
        StudentProfile.setPhone(request.getPhone());
        StudentProfile.setEmail(request.getEmail());
        StudentProfile.setBloodGroup(request.getBloodGroup());
        StudentProfile.setReligion(request.getReligion());
        StudentProfile.setCaste(request.getCaste());
        StudentProfile.setNationality(request.getNationality());
        StudentProfile.setMotherTongue(request.getMotherTongue());
        StudentProfile.setAadharNo(request.getAadharNo());
        StudentProfile.setStatus(request.getStatus());
        StudentProfile.setAdmissionDate(request.getAdmissionDate());
        StudentProfile.setPhotoUrl(request.getPhotoUrl());
        StudentProfile.setRemarks(request.getRemarks());
        StudentProfile.setMedicalConditions(request.getMedicalConditions());
        StudentProfile.setAllergies(request.getAllergies());
        StudentProfile.setCreatedBy(createdBy);

        StudentProfile = StudentProfileRepository.save(StudentProfile);

        // Save guardians
        if (request.getGuardians() != null) {
            for (GuardianDto guardianDto : request.getGuardians()) {
                Guardian guardian = new Guardian();
                guardian.setStudentId(StudentProfile.getId());
                guardian.setRelation(guardianDto.getRelation());
                guardian.setName(guardianDto.getName());
                guardian.setPhone(guardianDto.getPhone());
                guardian.setAlternatePhone(guardianDto.getAlternatePhone());
                guardian.setEmail(guardianDto.getEmail());
                guardian.setOccupation(guardianDto.getOccupation());
                guardian.setAnnualIncome(guardianDto.getAnnualIncome());
                guardian.setEducation(guardianDto.getEducation());
                guardian.setIsPrimary(guardianDto.getIsPrimary() != null ? guardianDto.getIsPrimary() : false);
                guardian.setAadharNo(guardianDto.getAadharNo());
                guardian.setPanNo(guardianDto.getPanNo());
                guardian.setPhotoUrl(guardianDto.getPhotoUrl());
                guardian.setCreatedBy(createdBy);
                guardianRepository.save(guardian);
            }
        }

        // Save addresses
        if (request.getAddresses() != null) {
            for (AddressDto addressDto : request.getAddresses()) {
                Address address = new Address();
                address.setStudentId(StudentProfile.getId());
                address.setAddressType(addressDto.getAddressType());
                address.setLine1(addressDto.getLine1());
                address.setLine2(addressDto.getLine2());
                address.setCity(addressDto.getCity());
                address.setState(addressDto.getState());
                address.setPincode(addressDto.getPincode());
                address.setCountry(addressDto.getCountry());
                address.setLandmark(addressDto.getLandmark());
                address.setCreatedBy(createdBy);
                addressRepository.save(address);
            }
        }

        return StudentProfile;
    }

    public StudentDetailsResponse getStudentDetails(Long schoolId, Long studentId) {
        StudentProfile StudentProfile = StudentProfileRepository.findByIdAndSchoolIdAndIsDeletedFalse(studentId, schoolId)
                .orElseThrow(() -> new RuntimeException("StudentProfile not found"));

        StudentDetailsResponse response = mapToDetailsResponse(StudentProfile);

        // Load guardians
        List<Guardian> guardians = guardianRepository.findByStudentIdAndIsDeletedFalse(studentId);
        response.setGuardians(guardians.stream().map(this::mapToGuardianDto).collect(Collectors.toList()));

        // Load addresses
        List<Address> addresses = addressRepository.findByStudentIdAndIsDeletedFalse(studentId);
        response.setAddresses(addresses.stream().map(this::mapToAddressDto).collect(Collectors.toList()));

        // Load current enrollment
        enrollmentRepository.findByStudentIdAndStatusAndIsDeletedFalse(studentId, "ACTIVE")
                .ifPresent(enrollment -> response.setCurrentEnrollment(mapToEnrollmentDto(enrollment)));

        // Load enrollment history
        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdOrderByStartDateDesc(studentId);
        response.setEnrollmentHistory(enrollments.stream().map(this::mapToEnrollmentDto).collect(Collectors.toList()));

        return response;
    }

    public Page<StudentProfile> searchStudents(Long schoolId, String searchTerm, String status, Pageable pageable) {
        if (searchTerm != null && !searchTerm.isEmpty()) {
            return StudentProfileRepository.searchStudents(schoolId, searchTerm, pageable);
        } else if (status != null) {
            return StudentProfileRepository.findBySchoolIdAndStatusAndIsDeletedFalse(schoolId, status, pageable);
        } else {
            return StudentProfileRepository.findBySchoolIdAndIsDeletedFalse(schoolId, pageable);
        }
    }

    @Transactional
    public StudentProfile updateStudentStatus(Long schoolId, Long studentId, String status, String updatedBy) {
        StudentProfile StudentProfile = StudentProfileRepository.findByIdAndSchoolIdAndIsDeletedFalse(studentId, schoolId)
                .orElseThrow(() -> new RuntimeException("StudentProfile not found"));

        StudentProfile.setStatus(status);
        StudentProfile.setUpdatedBy(updatedBy);
        return StudentProfileRepository.save(StudentProfile);
    }

    @Transactional
    public StudentProfile updateStudent(Long schoolId, Long studentId, CreateStudentRequest request, String updatedBy) {
        StudentProfile StudentProfile = StudentProfileRepository.findByIdAndSchoolIdAndIsDeletedFalse(studentId, schoolId)
                .orElseThrow(() -> new RuntimeException("StudentProfile not found"));

        // If admission number is provided and different, ensure uniqueness
        if (request.getAdmissionNo() != null && !request.getAdmissionNo().equals(StudentProfile.getAdmissionNo())) {
            StudentProfileRepository.findBySchoolIdAndAdmissionNo(request.getSchoolId(), request.getAdmissionNo())
                    .ifPresent(s -> { throw new RuntimeException("Admission number already exists: " + request.getAdmissionNo()); });
        }

        // Update fields
        StudentProfile.setAdmissionNo(request.getAdmissionNo() != null ? request.getAdmissionNo() : StudentProfile.getAdmissionNo());
        StudentProfile.setFirstName(request.getFirstName());
        StudentProfile.setMiddleName(request.getMiddleName());
        StudentProfile.setLastName(request.getLastName());
        StudentProfile.setGender(request.getGender());
        StudentProfile.setDateOfBirth(request.getDateOfBirth());
        StudentProfile.setPhone(request.getPhone());
        StudentProfile.setEmail(request.getEmail());
        StudentProfile.setBloodGroup(request.getBloodGroup());
        StudentProfile.setReligion(request.getReligion());
        StudentProfile.setCaste(request.getCaste());
        StudentProfile.setNationality(request.getNationality());
        StudentProfile.setMotherTongue(request.getMotherTongue());
        StudentProfile.setAadharNo(request.getAadharNo());
        StudentProfile.setStatus(request.getStatus());
        StudentProfile.setAdmissionDate(request.getAdmissionDate());
        StudentProfile.setPhotoUrl(request.getPhotoUrl());
        StudentProfile.setRemarks(request.getRemarks());
        StudentProfile.setMedicalConditions(request.getMedicalConditions());
        StudentProfile.setAllergies(request.getAllergies());
        StudentProfile.setUpdatedBy(updatedBy);

        StudentProfile = StudentProfileRepository.save(StudentProfile);

        // Replace guardians: remove existing and add provided
        List<Guardian> existingGuardians = guardianRepository.findByStudentIdAndIsDeletedFalse(studentId);
        if (existingGuardians != null) {
            for (Guardian g : existingGuardians) {
                guardianRepository.delete(g);
            }
        }

        if (request.getGuardians() != null) {
            for (GuardianDto guardianDto : request.getGuardians()) {
                Guardian guardian = new Guardian();
                guardian.setStudentId(StudentProfile.getId());
                guardian.setRelation(guardianDto.getRelation());
                guardian.setName(guardianDto.getName());
                guardian.setPhone(guardianDto.getPhone());
                guardian.setAlternatePhone(guardianDto.getAlternatePhone());
                guardian.setEmail(guardianDto.getEmail());
                guardian.setOccupation(guardianDto.getOccupation());
                guardian.setAnnualIncome(guardianDto.getAnnualIncome());
                guardian.setEducation(guardianDto.getEducation());
                guardian.setIsPrimary(guardianDto.getIsPrimary() != null ? guardianDto.getIsPrimary() : false);
                guardian.setAadharNo(guardianDto.getAadharNo());
                guardian.setPanNo(guardianDto.getPanNo());
                guardian.setPhotoUrl(guardianDto.getPhotoUrl());
                guardian.setCreatedBy(updatedBy);
                guardianRepository.save(guardian);
            }
        }

        // Replace addresses
        List<Address> existingAddresses = addressRepository.findByStudentIdAndIsDeletedFalse(studentId);
        if (existingAddresses != null) {
            for (Address a : existingAddresses) {
                addressRepository.delete(a);
            }
        }

        if (request.getAddresses() != null) {
            for (AddressDto addressDto : request.getAddresses()) {
                Address address = new Address();
                address.setStudentId(StudentProfile.getId());
                address.setAddressType(addressDto.getAddressType());
                address.setLine1(addressDto.getLine1());
                address.setLine2(addressDto.getLine2());
                address.setCity(addressDto.getCity());
                address.setState(addressDto.getState());
                address.setPincode(addressDto.getPincode());
                address.setCountry(addressDto.getCountry());
                address.setLandmark(addressDto.getLandmark());
                address.setCreatedBy(updatedBy);
                addressRepository.save(address);
            }
        }

        return StudentProfile;
    }

    private String generateAdmissionNumber(Long schoolId) {
        // Simple implementation - can be enhanced with custom logic
        long count = StudentProfileRepository.count();
        return "ADM" + LocalDate.now().getYear() + String.format("%06d", count + 1);
    }

    private StudentDetailsResponse mapToDetailsResponse(StudentProfile StudentProfile) {
        StudentDetailsResponse response = new StudentDetailsResponse();
        response.setId(StudentProfile.getId());
        response.setSchoolId(StudentProfile.getSchoolId());
        response.setStatus(StudentProfile.getStatus());
        response.setAdmissionNo(StudentProfile.getAdmissionNo());
        response.setFirstName(StudentProfile.getFirstName());
        response.setMiddleName(StudentProfile.getMiddleName());
        response.setLastName(StudentProfile.getLastName());
        response.setFullName(StudentProfile.getFirstName() + " " + 
                (StudentProfile.getMiddleName() != null ? StudentProfile.getMiddleName() + " " : "") + 
                StudentProfile.getLastName());
        response.setGender(StudentProfile.getGender());
        response.setDateOfBirth(StudentProfile.getDateOfBirth());
        response.setPhone(StudentProfile.getPhone());
        response.setEmail(StudentProfile.getEmail());
        response.setBloodGroup(StudentProfile.getBloodGroup());
        response.setReligion(StudentProfile.getReligion());
        response.setCaste(StudentProfile.getCaste());
        response.setNationality(StudentProfile.getNationality());
        response.setMotherTongue(StudentProfile.getMotherTongue());
        response.setAadharNo(StudentProfile.getAadharNo());
        response.setStatus(StudentProfile.getStatus());
        response.setAdmissionDate(StudentProfile.getAdmissionDate());
        response.setPhotoUrl(StudentProfile.getPhotoUrl());
        response.setRemarks(StudentProfile.getRemarks());
        response.setMedicalConditions(StudentProfile.getMedicalConditions());
        response.setAllergies(StudentProfile.getAllergies());
        return response;
    }

    private GuardianDto mapToGuardianDto(Guardian guardian) {
        GuardianDto dto = new GuardianDto();
        dto.setId(guardian.getId());
        dto.setRelation(guardian.getRelation());
        dto.setName(guardian.getName());
        dto.setPhone(guardian.getPhone());
        dto.setAlternatePhone(guardian.getAlternatePhone());
        dto.setEmail(guardian.getEmail());
        dto.setOccupation(guardian.getOccupation());
        dto.setAnnualIncome(guardian.getAnnualIncome());
        dto.setEducation(guardian.getEducation());
        dto.setIsPrimary(guardian.getIsPrimary());
        dto.setAadharNo(guardian.getAadharNo());
        dto.setPanNo(guardian.getPanNo());
        dto.setPhotoUrl(guardian.getPhotoUrl());
        return dto;
    }

    private AddressDto mapToAddressDto(Address address) {
        AddressDto dto = new AddressDto();
        dto.setId(address.getId());
        dto.setAddressType(address.getAddressType());
        dto.setLine1(address.getLine1());
        dto.setLine2(address.getLine2());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setPincode(address.getPincode());
        dto.setCountry(address.getCountry());
        dto.setLandmark(address.getLandmark());
        return dto;
    }

    private EnrollmentDto mapToEnrollmentDto(Enrollment enrollment) {
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


