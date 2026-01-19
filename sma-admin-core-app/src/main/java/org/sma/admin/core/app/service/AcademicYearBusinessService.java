package org.sma.admin.core.app.service;

import org.sma.admin.core.app.model.request.AcademicYearRequest;
import org.sma.admin.core.app.model.response.AcademicYearResponse;
import org.sma.platform.core.exception.SmaException;
import org.sma.platform.core.service.ServiceRequestContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Academic Year Business Service
 * Handles business logic for academic year management
 */
@Component
public class AcademicYearBusinessService {

    // TODO: Autowire repository when available
    // @Autowired
    // AcademicYearRepository academicYearRepository;

    /**
     * Create new academic year
     * 
     * @param context Service request context
     * @param request Academic year request
     * @return Created academic year response
     * @throws SmaException if validation fails or year already exists
     */
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
        // TODO: Replace with actual repository call
        // AcademicYearModel existingYear = academicYearRepository.findByYearName(request.getYearName());
        // if (existingYear != null) {
        //     throw new SmaException("Academic year already exists: " + request.getYearName());
        // }

        // If setting as current year, unset all other current years
        if (request.isCurrentYear()) {
            // TODO: Update all existing years to non-current
            // academicYearRepository.updateAllToNonCurrent();
        }

        // Create academic year entity
        // TODO: Replace with actual entity creation and save
        // AcademicYearModel yearModel = new AcademicYearModel();
        // yearModel.setYearName(request.getYearName());
        // yearModel.setStartDate(request.getStartDate());
        // yearModel.setEndDate(request.getEndDate());
        // yearModel.setCurrentYear(request.isCurrentYear());
        // yearModel.setDescription(request.getDescription());
        // 
        // AcademicYearModel savedYear = academicYearRepository.save(yearModel);

        // Build response
        AcademicYearResponse response = new AcademicYearResponse();
        response.setYearName(request.getYearName());
        response.setStartDate(request.getStartDate());
        response.setEndDate(request.getEndDate());
        response.setCurrentYear(request.isCurrentYear());
        response.setDescription(request.getDescription());
        
        return response;
    }

    /**
     * Get academic year by ID
     */
    public AcademicYearResponse getAcademicYear(ServiceRequestContext context, Long yearId) throws SmaException {
        // TODO: Replace with actual repository call
        // AcademicYearModel year = academicYearRepository.findById(yearId)
        //         .orElseThrow(() -> new SmaException("Academic year not found with id: " + yearId));
        // 
        // return convertToResponse(year);
        
        throw new SmaException("Academic year not found with id: " + yearId);
    }

    /**
     * Get all academic years
     */
    public List<AcademicYearResponse> getAllAcademicYears(ServiceRequestContext context) throws SmaException {
        // TODO: Replace with actual repository call
        // List<AcademicYearModel> years = academicYearRepository.findAll();
        // return years.stream()
        //         .map(this::convertToResponse)
        //         .collect(Collectors.toList());
        
        return new ArrayList<>();
    }

    /**
     * Get current academic year
     */
    public AcademicYearResponse getCurrentAcademicYear(ServiceRequestContext context) throws SmaException {
        // TODO: Replace with actual repository call
        // AcademicYearModel currentYear = academicYearRepository.findByCurrentYearTrue();
        // if (currentYear == null) {
        //     throw new SmaException("No current academic year set");
        // }
        // return convertToResponse(currentYear);
        
        throw new SmaException("No current academic year set");
    }

    /**
     * Update academic year
     */
    public AcademicYearResponse updateAcademicYear(ServiceRequestContext context, 
                                                   Long yearId, 
                                                   AcademicYearRequest request) throws SmaException {
        // TODO: Get existing year from repository
        // AcademicYearModel existingYear = academicYearRepository.findById(yearId)
        //         .orElseThrow(() -> new SmaException("Academic year not found with id: " + yearId));

        // If setting as current year, unset all other current years
        if (request.isCurrentYear()) {
            // TODO: Update all existing years to non-current
            // academicYearRepository.updateAllToNonCurrentExcept(yearId);
        }

        // Update fields
        // existingYear.setYearName(request.getYearName());
        // existingYear.setStartDate(request.getStartDate());
        // existingYear.setEndDate(request.getEndDate());
        // existingYear.setCurrentYear(request.isCurrentYear());
        // existingYear.setDescription(request.getDescription());
        // 
        // AcademicYearModel updatedYear = academicYearRepository.save(existingYear);
        // return convertToResponse(updatedYear);
        
        AcademicYearResponse response = new AcademicYearResponse();
        response.setYearId(yearId);
        response.setYearName(request.getYearName());
        return response;
    }

    /**
     * Delete academic year
     */
    public void deleteAcademicYear(ServiceRequestContext context, Long yearId) throws SmaException {
        // TODO: Get year from repository
        // AcademicYearModel year = academicYearRepository.findById(yearId)
        //         .orElseThrow(() -> new SmaException("Academic year not found with id: " + yearId));

        // Check if it's current year - don't allow deletion
        // if (year.isCurrentYear()) {
        //     throw new SmaException("Cannot delete current academic year");
        // }

        // Delete year
        // academicYearRepository.delete(year);
    }

    /**
     * Set academic year as current
     */
    public AcademicYearResponse setCurrentAcademicYear(ServiceRequestContext context, Long yearId) throws SmaException {
        // TODO: Get year from repository
        // AcademicYearModel year = academicYearRepository.findById(yearId)
        //         .orElseThrow(() -> new SmaException("Academic year not found with id: " + yearId));

        // Unset all other current years
        // academicYearRepository.updateAllToNonCurrentExcept(yearId);

        // Set this year as current
        // year.setCurrentYear(true);
        // AcademicYearModel updatedYear = academicYearRepository.save(year);
        // return convertToResponse(updatedYear);
        
        AcademicYearResponse response = new AcademicYearResponse();
        response.setYearId(yearId);
        response.setCurrentYear(true);
        return response;
    }
}
