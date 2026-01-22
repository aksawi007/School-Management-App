package org.sma.admin.core.app.service;

import org.sma.admin.core.app.model.request.AcademicYearRequest;
import org.sma.admin.core.app.model.response.AcademicYearResponse;
import org.sma.jpa.model.school.AcademicYear;
import org.sma.jpa.repository.school.AcademicYearRepository;
import org.sma.platform.core.exception.SmaException;
import org.sma.platform.core.service.ServiceRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Academic Year Business Service
 * Handles business logic for academic year management
 */
@Component
public class AcademicYearBusinessService {

    @Autowired
    private AcademicYearRepository academicYearRepository;

    /**
     * Create new academic year
     * 
     * @param context Service request context
     * @param request Academic year request
     * @return Created academic year response
     * @throws SmaException if validation fails or year already exists
     */
    @Transactional
    public AcademicYearResponse createAcademicYear(ServiceRequestContext context, 
                                                   AcademicYearRequest request) throws SmaException {
        // Validate required fields
        if (request.getYearName() == null || request.getYearName().isEmpty()) {
            throw new SmaException("Academic year name is mandatory");
        }
        if (request.getStartDate() == null || request.getStartDate().isEmpty()) {
            throw new SmaException("Start date is mandatory");
        }
        if (request.getEndDate() == null || request.getEndDate().isEmpty()) {
            throw new SmaException("End date is mandatory");
        }

        // Check if year name already exists
        Optional<AcademicYear> existingYear = academicYearRepository.findByYearName(request.getYearName());
        if (existingYear.isPresent()) {
            throw new SmaException("Academic year already exists: " + request.getYearName());
        }

        // If setting as current year, unset all other current years
        if (request.isCurrentYear()) {
            academicYearRepository.updateAllToNonCurrent();
        }

        // Create academic year entity
        AcademicYear academicYear = new AcademicYear();
        academicYear.setYearName(request.getYearName());
        academicYear.setYearCode(request.getYearName()); // Use yearName as yearCode for now
        academicYear.setStartDate(LocalDate.parse(request.getStartDate()));
        academicYear.setEndDate(LocalDate.parse(request.getEndDate()));
        academicYear.setIsCurrent(request.isCurrentYear());
        academicYear.setDescription(request.getDescription());
        academicYear.setStatus("ACTIVE");
        
        AcademicYear savedYear = academicYearRepository.save(academicYear);

        return convertToResponse(savedYear);
    }

    /**
     * Get academic year by ID
     */
    public AcademicYearResponse getAcademicYear(ServiceRequestContext context, Long yearId) throws SmaException {
        AcademicYear year = academicYearRepository.findById(yearId)
                .orElseThrow(() -> new SmaException("Academic year not found with id: " + yearId));
        
        return convertToResponse(year);
    }

    /**
     * Get all academic years
     */
    public List<AcademicYearResponse> getAllAcademicYears(ServiceRequestContext context) throws SmaException {
        List<AcademicYear> years = academicYearRepository.findAll();
        return years.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get current academic year
     */
    public AcademicYearResponse getCurrentAcademicYear(ServiceRequestContext context) throws SmaException {
        List<AcademicYear> allYears = academicYearRepository.findAll();
        AcademicYear currentYear = allYears.stream()
                .filter(AcademicYear::getIsCurrent)
                .findFirst()
                .orElseThrow(() -> new SmaException("No current academic year set"));
        
        return convertToResponse(currentYear);
    }

    /**
     * Update academic year
     */
    @Transactional
    public AcademicYearResponse updateAcademicYear(ServiceRequestContext context, 
                                                   Long yearId, 
                                                   AcademicYearRequest request) throws SmaException {
        AcademicYear existingYear = academicYearRepository.findById(yearId)
                .orElseThrow(() -> new SmaException("Academic year not found with id: " + yearId));

        // If setting as current year, unset all other current years
        if (request.isCurrentYear()) {
            academicYearRepository.updateAllToNonCurrentExcept(yearId);
        }

        // Update fields
        existingYear.setYearName(request.getYearName());
        existingYear.setYearCode(request.getYearName());
        existingYear.setStartDate(LocalDate.parse(request.getStartDate()));
        existingYear.setEndDate(LocalDate.parse(request.getEndDate()));
        existingYear.setIsCurrent(request.isCurrentYear());
        existingYear.setDescription(request.getDescription());
        
        AcademicYear updatedYear = academicYearRepository.save(existingYear);
        return convertToResponse(updatedYear);
    }

    /**
     * Delete academic year
     */
    public void deleteAcademicYear(ServiceRequestContext context, Long yearId) throws SmaException {
        AcademicYear year = academicYearRepository.findById(yearId)
                .orElseThrow(() -> new SmaException("Academic year not found with id: " + yearId));

        // Check if it's current year - don't allow deletion
        if (year.getIsCurrent()) {
            throw new SmaException("Cannot delete current academic year");
        }

        academicYearRepository.delete(year);
    }

    /**
     * Set academic year as current
     */
    @Transactional
    public AcademicYearResponse setCurrentAcademicYear(ServiceRequestContext context, Long yearId) throws SmaException {
        AcademicYear year = academicYearRepository.findById(yearId)
                .orElseThrow(() -> new SmaException("Academic year not found with id: " + yearId));

        // Unset all other current years
        academicYearRepository.updateAllToNonCurrentExcept(yearId);

        // Set this year as current
        year.setIsCurrent(true);
        AcademicYear updatedYear = academicYearRepository.save(year);
        return convertToResponse(updatedYear);
    }
    
    /**
     * Convert AcademicYear entity to response DTO
     */
    private AcademicYearResponse convertToResponse(AcademicYear year) {
        AcademicYearResponse response = new AcademicYearResponse();
        response.setYearId(year.getId());
        response.setYearName(year.getYearName());
        response.setStartDate(year.getStartDate().toString());
        response.setEndDate(year.getEndDate().toString());
        response.setCurrentYear(year.getIsCurrent());
        response.setDescription(year.getDescription());
        return response;
    }
}
