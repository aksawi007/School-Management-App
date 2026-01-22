package org.sma.admin.core.app.service;

import org.sma.admin.core.app.model.request.SchoolProfileRequest;
import org.sma.admin.core.app.model.response.SchoolProfileResponse;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.repository.school.SchoolProfileRepository;
import org.sma.platform.core.exception.SmaException;
import org.sma.platform.core.service.ServiceRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * School Setup Business Service
 * Handles business logic for school profile management
 */
@Component
public class SchoolSetupBusinessService {

    @Autowired
    private SchoolProfileRepository schoolProfileRepository;

    /**
     * Create new school profile
     * 
     * @param context Service request context
     * @param request School profile request
     * @return Created school profile response
     * @throws SmaException if validation fails or school already exists
     */
    public SchoolProfileResponse createSchoolProfile(ServiceRequestContext context, 
                                                     SchoolProfileRequest request) throws SmaException {
        // Validate required fields
        if (request.getSchoolName() == null || request.getSchoolName().isEmpty()) {
            throw new SmaException("School name is mandatory");
        }
        if (request.getSchoolCode() == null || request.getSchoolCode().isEmpty()) {
            throw new SmaException("School code is mandatory");
        }

        // Check if school code already exists
        Optional<SchoolProfile> existingSchool = schoolProfileRepository.findBySchoolCode(request.getSchoolCode());
        if (existingSchool.isPresent()) {
            throw new SmaException("School with code already exists: " + request.getSchoolCode());
        }

        // Create school profile entity
        SchoolProfile schoolProfile = new SchoolProfile();
        schoolProfile.setSchoolName(request.getSchoolName());
        schoolProfile.setSchoolCode(request.getSchoolCode());
        schoolProfile.setAddressLine1(request.getAddress());
        schoolProfile.setCity(request.getCity());
        schoolProfile.setState(request.getState());
        schoolProfile.setCountry(request.getCountry());
        schoolProfile.setPostalCode(request.getPincode());
        schoolProfile.setPhoneNumber(request.getPhone());
        schoolProfile.setEmail(request.getEmail());
        schoolProfile.setWebsite(request.getWebsite());
        schoolProfile.setPrincipalName(request.getPrincipalName());
        schoolProfile.setAffiliationNumber(request.getAffiliationNumber());
        schoolProfile.setAffiliationBoard(request.getBoard());
        schoolProfile.setRegistrationStatus("ACTIVE");
        
        // Convert established year to LocalDate if provided
        if (request.getEstablishedYear() != null && !request.getEstablishedYear().isEmpty()) {
            try {
                int year = Integer.parseInt(request.getEstablishedYear());
                schoolProfile.setEstablishedDate(LocalDate.of(year, 1, 1));
            } catch (NumberFormatException e) {
                // Ignore invalid year format
            }
        }
        
        // Save school profile
        SchoolProfile savedSchool = schoolProfileRepository.save(schoolProfile);

        // Build response
        return convertToResponse(savedSchool);
    }

    /**
     * Get school profile by ID
     */
    public SchoolProfileResponse getSchoolProfile(ServiceRequestContext context, Long schoolId) throws SmaException {
        // TODO: Replace with actual repository call
        // SchoolProfileModel school = schoolProfileRepository.findById(schoolId)
        //         .orElseThrow(() -> new SmaException("School not found with id: " + schoolId));
        // 
        // return convertToResponse(school);
        
        throw new SmaException("School not found with id: " + schoolId);
    }

    /**
     * Get all schools
     */
    public List<SchoolProfileResponse> getAllSchools(ServiceRequestContext context) throws SmaException {
        // TODO: Replace with actual repository call
        // List<SchoolProfileModel> schools = schoolProfileRepository.findAll();
        // return schools.stream()
        //         .map(this::convertToResponse)
        //         .collect(Collectors.toList());
        
        return new ArrayList<>();
    }

    /**
     * Update school profile
     */
    public SchoolProfileResponse updateSchoolProfile(ServiceRequestContext context, 
                                                     Long schoolId, 
                                                     SchoolProfileRequest request) throws SmaException {
        // TODO: Get existing school from repository
        // SchoolProfileModel existingSchool = schoolProfileRepository.findById(schoolId)
        //         .orElseThrow(() -> new SmaException("School not found with id: " + schoolId));

        // Update fields
        // existingSchool.setSchoolName(request.getSchoolName());
        // existingSchool.setAddress(request.getAddress());
        // ... update other fields
        // 
        // SchoolProfileModel updatedSchool = schoolProfileRepository.save(existingSchool);
        // return convertToResponse(updatedSchool);
        
        SchoolProfileResponse response = new SchoolProfileResponse();
        response.setSchoolId(schoolId);
        response.setSchoolName(request.getSchoolName());
        return response;
    }

    /**
     * Delete school profile
     */
    public void deleteSchoolProfile(ServiceRequestContext context, Long schoolId) throws SmaException {
        // TODO: Get school from repository
        // SchoolProfileModel school = schoolProfileRepository.findById(schoolId)
        //         .orElseThrow(() -> new SmaException("School not found with id: " + schoolId));

        // Delete school
        // schoolProfileRepository.delete(school);
    }

    /**
     * Get school by code
     */
    public SchoolProfileResponse getSchoolByCode(ServiceRequestContext context, String schoolCode) throws SmaException {
        SchoolProfile school = schoolProfileRepository.findBySchoolCode(schoolCode)
                .orElseThrow(() -> new SmaException("School not found with code: " + schoolCode));
        return convertToResponse(school);
    }

    /**
     * Convert SchoolProfile entity to response DTO
     */
    private SchoolProfileResponse convertToResponse(SchoolProfile school) {
        SchoolProfileResponse response = new SchoolProfileResponse();
        response.setSchoolId(school.getId());
        response.setSchoolName(school.getSchoolName());
        response.setSchoolCode(school.getSchoolCode());
        response.setAddress(school.getAddressLine1());
        response.setCity(school.getCity());
        response.setState(school.getState());
        response.setCountry(school.getCountry());
        response.setPincode(school.getPostalCode());
        response.setPhone(school.getPhoneNumber());
        response.setEmail(school.getEmail());
        response.setWebsite(school.getWebsite());
        response.setPrincipalName(school.getPrincipalName());
        response.setAffiliationNumber(school.getAffiliationNumber());
        response.setBoard(school.getAffiliationBoard());
        
        if (school.getEstablishedDate() != null) {
            response.setEstablishedYear(String.valueOf(school.getEstablishedDate().getYear()));
        }
        
        return response;
    }
}
