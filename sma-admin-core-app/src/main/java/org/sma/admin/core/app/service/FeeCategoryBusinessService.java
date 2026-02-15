package org.sma.admin.core.app.service;

import org.sma.admin.core.app.exception.SmaAdminException;
import org.sma.admin.core.app.model.request.FeeCategoryRequest;
import org.sma.admin.core.app.model.response.FeeCategoryResponse;
import org.sma.jpa.model.fee.FeeCategory;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.repository.fee.FeeCategoryRepository;
import org.sma.jpa.repository.school.SchoolProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Business Service for Fee Category Management
 * Handles core fee category operations applicable throughout every year
 */
@Service
@Transactional
public class FeeCategoryBusinessService {

    private static final Logger logger = LoggerFactory.getLogger(FeeCategoryBusinessService.class);

    @Autowired
    private FeeCategoryRepository feeCategoryRepository;

    @Autowired
    private SchoolProfileRepository schoolProfileRepository;

    /**
     * Create a new fee category
     */
    public FeeCategoryResponse createFeeCategory(Long schoolId, FeeCategoryRequest request) {
        logger.info("Creating fee category for school: {}, code: {}", schoolId, request.getCategoryCode());

        SchoolProfile school = schoolProfileRepository.findById(schoolId)
            .orElseThrow(() -> new SmaAdminException("School not found with ID: " + schoolId));

        // Check if category code already exists
        feeCategoryRepository.findBySchoolAndCategoryCode(school, request.getCategoryCode())
            .ifPresent(existing -> {
                throw new SmaAdminException("Fee category with code '" + request.getCategoryCode() + "' already exists");
            });

        FeeCategory feeCategory = new FeeCategory();
        feeCategory.setSchool(school);
        feeCategory.setCategoryCode(request.getCategoryCode());
        feeCategory.setCategoryName(request.getCategoryName());
        feeCategory.setCategoryType(request.getCategoryType());
        feeCategory.setIsMandatory(request.getIsMandatory());
        feeCategory.setIsRefundable(request.getIsRefundable());
        feeCategory.setDisplayOrder(request.getDisplayOrder());
        feeCategory.setDescription(request.getDescription());
        feeCategory.setFeeApplicability(request.getFeeApplicability());
        feeCategory.setPaymentFrequency(request.getPaymentFrequency());
        feeCategory.setIsActive(true);

        FeeCategory saved = feeCategoryRepository.save(feeCategory);
        logger.info("Fee category created successfully with ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    /**
     * Update fee category
     */
    public FeeCategoryResponse updateFeeCategory(Long categoryId, FeeCategoryRequest request) {
        logger.info("Updating fee category: {}", categoryId);

        FeeCategory feeCategory = feeCategoryRepository.findById(categoryId)
            .orElseThrow(() -> new SmaAdminException("Fee category not found with ID: " + categoryId));

        // Check if updating to a code that already exists
        if (!feeCategory.getCategoryCode().equals(request.getCategoryCode())) {
            feeCategoryRepository.findBySchoolAndCategoryCode(feeCategory.getSchool(), request.getCategoryCode())
                .ifPresent(existing -> {
                    throw new SmaAdminException("Fee category with code '" + request.getCategoryCode() + "' already exists");
                });
        }

        feeCategory.setCategoryCode(request.getCategoryCode());
        feeCategory.setCategoryName(request.getCategoryName());
        feeCategory.setCategoryType(request.getCategoryType());
        feeCategory.setIsMandatory(request.getIsMandatory());
        feeCategory.setIsRefundable(request.getIsRefundable());
        feeCategory.setDisplayOrder(request.getDisplayOrder());
        feeCategory.setDescription(request.getDescription());
        feeCategory.setFeeApplicability(request.getFeeApplicability());
        feeCategory.setPaymentFrequency(request.getPaymentFrequency());

        FeeCategory updated = feeCategoryRepository.save(feeCategory);
        logger.info("Fee category updated successfully: {}", categoryId);

        return mapToResponse(updated);
    }

    /**
     * Get fee category by ID
     */
    @Transactional(readOnly = true)
    public FeeCategoryResponse getFeeCategoryById(Long categoryId) {
        FeeCategory feeCategory = feeCategoryRepository.findById(categoryId)
            .orElseThrow(() -> new SmaAdminException("Fee category not found with ID: " + categoryId));

        return mapToResponse(feeCategory);
    }

    /**
     * Get all active fee categories for a school
     */
    @Transactional(readOnly = true)
    public List<FeeCategoryResponse> getAllActiveFeeCategories(Long schoolId) {
        logger.info("Fetching all active fee categories for school: {}", schoolId);

        SchoolProfile school = schoolProfileRepository.findById(schoolId)
            .orElseThrow(() -> new SmaAdminException("School not found with ID: " + schoolId));

        List<FeeCategory> categories = feeCategoryRepository.findBySchoolAndIsActiveTrue(school);

        return categories.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get fee categories by type
     */
    @Transactional(readOnly = true)
    public List<FeeCategoryResponse> getFeeCategoriesByType(Long schoolId, String categoryType) {
        logger.info("Fetching fee categories for school: {}, type: {}", schoolId, categoryType);

        SchoolProfile school = schoolProfileRepository.findById(schoolId)
            .orElseThrow(() -> new SmaAdminException("School not found with ID: " + schoolId));

        List<FeeCategory> categories = feeCategoryRepository.findBySchoolAndCategoryType(school, categoryType);

        return categories.stream()
            .filter(FeeCategory::getIsActive)
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Deactivate fee category (soft delete)
     */
    public void deactivateFeeCategory(Long categoryId) {
        logger.info("Deactivating fee category: {}", categoryId);

        FeeCategory feeCategory = feeCategoryRepository.findById(categoryId)
            .orElseThrow(() -> new SmaAdminException("Fee category not found with ID: " + categoryId));

        feeCategory.setIsActive(false);
        feeCategoryRepository.save(feeCategory);

        logger.info("Fee category deactivated successfully: {}", categoryId);
    }

    /**
     * Activate fee category
     */
    public void activateFeeCategory(Long categoryId) {
        logger.info("Activating fee category: {}", categoryId);

        FeeCategory feeCategory = feeCategoryRepository.findById(categoryId)
            .orElseThrow(() -> new SmaAdminException("Fee category not found with ID: " + categoryId));

        feeCategory.setIsActive(true);
        feeCategoryRepository.save(feeCategory);

        logger.info("Fee category activated successfully: {}", categoryId);
    }

    /**
     * Map entity to response DTO
     */
    private FeeCategoryResponse mapToResponse(FeeCategory feeCategory) {
        FeeCategoryResponse response = new FeeCategoryResponse();
        response.setId(feeCategory.getId());
        response.setCategoryCode(feeCategory.getCategoryCode());
        response.setCategoryName(feeCategory.getCategoryName());
        response.setCategoryType(feeCategory.getCategoryType());
        response.setIsMandatory(feeCategory.getIsMandatory());
        response.setIsRefundable(feeCategory.getIsRefundable());
        response.setDisplayOrder(feeCategory.getDisplayOrder());
        response.setDescription(feeCategory.getDescription());
        response.setIsActive(feeCategory.getIsActive());
        response.setCreatedDate(feeCategory.getCreatedAt() != null ? feeCategory.getCreatedAt().toLocalDate() : null);
        response.setFeeApplicability(feeCategory.getFeeApplicability());
        response.setPaymentFrequency(feeCategory.getPaymentFrequency());
        return response;
    }
}
