package org.sma.admin.core.app.service;

import org.sma.admin.core.app.exception.SmaAdminException;
import org.sma.admin.core.app.model.request.FeePlanRequest;
import org.sma.admin.core.app.model.response.FeePlanResponse;
import org.sma.jpa.model.school.AcademicYear;
import org.sma.jpa.model.fee.FeeCategory;
import org.sma.jpa.model.fee.FeeInstallment;
import org.sma.jpa.model.fee.FeePlan;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.repository.school.AcademicYearRepository;
import org.sma.jpa.repository.fee.FeeCategoryRepository;
import org.sma.jpa.repository.fee.FeeInstallmentRepository;
import org.sma.jpa.repository.fee.FeePlanRepository;
import org.sma.jpa.repository.school.SchoolProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Unified Business Service for Fee Plan Management
 * Handles both FeePlan and FeeInstallment operations
 */
@Service
@Transactional
public class FeePlanBusinessService {

    private static final Logger logger = LoggerFactory.getLogger(FeePlanBusinessService.class);

    @Autowired
    private FeePlanRepository feePlanRepository;

    @Autowired
    private FeeInstallmentRepository feeInstallmentRepository;

    @Autowired
    private SchoolProfileRepository schoolProfileRepository;

    @Autowired
    private AcademicYearRepository academicYearRepository;

    @Autowired
    private FeeCategoryRepository feeCategoryRepository;

    /**
     * Create fee plan with installments
     */
    public FeePlanResponse createFeePlan(Long schoolId, FeePlanRequest request) {
        logger.info("Creating fee plan for school: {}, year: {}, category: {}", 
            schoolId, request.getAcademicYearId(), request.getCategoryId());

        SchoolProfile school = schoolProfileRepository.findById(schoolId)
            .orElseThrow(() -> new SmaAdminException("School not found with ID: " + schoolId));

        AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
            .orElseThrow(() -> new SmaAdminException("Academic year not found with ID: " + request.getAcademicYearId()));

        FeeCategory category = feeCategoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new SmaAdminException("Fee category not found with ID: " + request.getCategoryId()));

        // Check if plan already exists
        feePlanRepository.findBySchoolAndAcademicYearAndCategory_Id(school, academicYear, request.getCategoryId())
            .ifPresent(existing -> {
                throw new SmaAdminException("Fee plan already exists for this academic year and category");
            });

        // Create fee plan
        FeePlan feePlan = new FeePlan();
        feePlan.setSchool(school);
        feePlan.setAcademicYear(academicYear);
        feePlan.setCategory(category);
        feePlan.setTotalAmount(request.getTotalAmount());
        feePlan.setFrequency(request.getFrequency());
        feePlan.setInstallmentsCount(request.getInstallmentsCount());
        feePlan.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");

        FeePlan savedPlan = feePlanRepository.save(feePlan);

        // Create installments if provided
        if (request.getInstallments() != null && !request.getInstallments().isEmpty()) {
            for (FeePlanRequest.InstallmentDetail detail : request.getInstallments()) {
                FeeInstallment installment = new FeeInstallment();
                installment.setFeePlan(savedPlan);
                installment.setInstallmentNo(detail.getInstallmentNo());
                installment.setInstallmentName(detail.getInstallmentName());
                installment.setPeriodStartDate(detail.getPeriodStartDate());
                installment.setPeriodEndDate(detail.getPeriodEndDate());
                installment.setAmountDue(detail.getAmountDue());
                installment.setDueDate(detail.getDueDate());
                installment.setStatus("ACTIVE");
                feeInstallmentRepository.save(installment);
            }
        }

        logger.info("Fee plan created successfully with ID: {}", savedPlan.getId());
        return mapToResponse(savedPlan, true);
    }

    /**
     * Update fee plan
     */
    public FeePlanResponse updateFeePlan(Long planId, FeePlanRequest request) {
        logger.info("Updating fee plan: {}", planId);

        FeePlan feePlan = feePlanRepository.findById(planId)
            .orElseThrow(() -> new SmaAdminException("Fee plan not found with ID: " + planId));

        feePlan.setTotalAmount(request.getTotalAmount());
        feePlan.setFrequency(request.getFrequency());
        feePlan.setInstallmentsCount(request.getInstallmentsCount());
        
        if (request.getStatus() != null) {
            feePlan.setStatus(request.getStatus());
        }

        FeePlan updated = feePlanRepository.save(feePlan);

        // Update installments - update existing or create new ones
        if (request.getInstallments() != null && !request.getInstallments().isEmpty()) {
            logger.info("Processing {} installments for fee plan {}", request.getInstallments().size(), planId);
            
            // Get existing installments
            List<FeeInstallment> existingInstallments = feeInstallmentRepository.findByFeePlanOrderByInstallmentNo(updated);
            logger.info("Found {} existing installments in database", existingInstallments.size());
            
            // Track which installment numbers are in the request
            Set<Integer> requestedInstallmentNos = request.getInstallments().stream()
                .map(FeePlanRequest.InstallmentDetail::getInstallmentNo)
                .collect(Collectors.toSet());

            // Delete installments that are no longer in the request
            for (FeeInstallment existing : existingInstallments) {
                if (!requestedInstallmentNos.contains(existing.getInstallmentNo())) {
                    logger.info("Deleting installment no {} (ID: {}) as it's not in the request", 
                        existing.getInstallmentNo(), existing.getId());
                    feeInstallmentRepository.delete(existing);
                }
            }

            // Update or create installments
            for (FeePlanRequest.InstallmentDetail detail : request.getInstallments()) {
                // Find existing installment by installmentNo
                Optional<FeeInstallment> existingOpt = feeInstallmentRepository
                    .findByFeePlanAndInstallmentNo(updated, detail.getInstallmentNo());

                FeeInstallment installment;
                if (existingOpt.isPresent()) {
                    // Update existing installment
                    installment = existingOpt.get();
                    logger.info("Updating existing installment no {} (ID: {})", detail.getInstallmentNo(), installment.getId());
                } else {
                    // Create new installment
                    installment = new FeeInstallment();
                    installment.setFeePlan(updated);
                    installment.setInstallmentNo(detail.getInstallmentNo());
                    installment.setStatus("ACTIVE");
                    logger.info("Creating new installment no {}", detail.getInstallmentNo());
                }

                // Set/update all fields
                installment.setInstallmentName(detail.getInstallmentName());
                installment.setPeriodStartDate(detail.getPeriodStartDate());
                installment.setPeriodEndDate(detail.getPeriodEndDate());
                installment.setAmountDue(detail.getAmountDue());
                installment.setDueDate(detail.getDueDate());
                
                FeeInstallment saved = feeInstallmentRepository.save(installment);
                logger.info("Saved installment: ID={}, No={}, Name='{}', Amount={}, DueDate={}", 
                    saved.getId(), saved.getInstallmentNo(), saved.getInstallmentName(), 
                    saved.getAmountDue(), saved.getDueDate());
            }
        }

        logger.info("Fee plan updated successfully: {}", planId);

        return mapToResponse(updated, true);
    }

    /**
     * Get fee plan by ID
     */
    @Transactional(readOnly = true)
    public FeePlanResponse getFeePlanById(Long planId, boolean includeInstallments) {
        FeePlan feePlan = feePlanRepository.findById(planId)
            .orElseThrow(() -> new SmaAdminException("Fee plan not found with ID: " + planId));

        return mapToResponse(feePlan, includeInstallments);
    }

    /**
     * List fee plans with filters
     * @param schoolId School ID
     * @param academicYearId Academic Year ID (optional)
     * @param categoryId Category ID (optional)
     * @param status Status filter (optional)
     * @param includeInstallments Include installment details
     */
    @Transactional(readOnly = true)
    public List<FeePlanResponse> listFeePlans(Long schoolId, Long academicYearId, Long categoryId, 
                                              String status, boolean includeInstallments) {
        logger.info("Fetching fee plans for school: {}, year: {}, category: {}, status: {}", 
            schoolId, academicYearId, categoryId, status);

        SchoolProfile school = schoolProfileRepository.findById(schoolId)
            .orElseThrow(() -> new SmaAdminException("School not found with ID: " + schoolId));

        List<FeePlan> plans;

        if (academicYearId != null) {
            AcademicYear academicYear = academicYearRepository.findById(academicYearId)
                .orElseThrow(() -> new SmaAdminException("Academic year not found with ID: " + academicYearId));
            
            if (status != null && !status.equalsIgnoreCase("ALL")) {
                plans = feePlanRepository.findBySchoolAndAcademicYearAndStatus(school, academicYear, status);
            } else {
                plans = feePlanRepository.findBySchoolAndAcademicYear(school, academicYear);
            }
        } else {
            plans = feePlanRepository.findAll().stream()
                .filter(p -> p.getSchool().getId().equals(schoolId))
                .collect(Collectors.toList());
        }

        // Apply category filter if provided
        if (categoryId != null) {
            plans = plans.stream()
                .filter(p -> p.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());
        }

        // Apply status filter if not already applied
        if (academicYearId == null && status != null && !status.equalsIgnoreCase("ALL")) {
            plans = plans.stream()
                .filter(p -> p.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
        }

        return plans.stream()
            .map(p -> mapToResponse(p, includeInstallments))
            .collect(Collectors.toList());
    }

    /**
     * Delete fee plan
     */
    public void deleteFeePlan(Long planId) {
        logger.info("Deleting fee plan: {}", planId);

        FeePlan feePlan = feePlanRepository.findById(planId)
            .orElseThrow(() -> new SmaAdminException("Fee plan not found with ID: " + planId));

        feePlanRepository.delete(feePlan);
        logger.info("Fee plan deleted successfully: {}", planId);
    }

    /**
     * Map entity to response DTO
     */
    private FeePlanResponse mapToResponse(FeePlan feePlan, boolean includeInstallments) {
        FeePlanResponse response = new FeePlanResponse();
        response.setId(feePlan.getId());
        response.setSchoolId(feePlan.getSchool().getId());
        response.setAcademicYearId(feePlan.getAcademicYear().getId());
        response.setAcademicYearName(feePlan.getAcademicYear().getYearName());
        response.setCategoryId(feePlan.getCategory().getId());
        response.setCategoryName(feePlan.getCategory().getCategoryName());
        response.setCategoryCode(feePlan.getCategory().getCategoryCode());
        response.setTotalAmount(feePlan.getTotalAmount());
        response.setFrequency(feePlan.getFrequency());
        response.setInstallmentsCount(feePlan.getInstallmentsCount());
        response.setStatus(feePlan.getStatus());

        if (includeInstallments) {
            List<FeeInstallment> installments = feeInstallmentRepository.findByFeePlanOrderByInstallmentNo(feePlan);
            List<FeePlanResponse.InstallmentResponse> installmentResponses = installments.stream()
                .map(this::mapInstallmentToResponse)
                .collect(Collectors.toList());
            response.setInstallments(installmentResponses);
        }

        return response;
    }

    private FeePlanResponse.InstallmentResponse mapInstallmentToResponse(FeeInstallment installment) {
        FeePlanResponse.InstallmentResponse response = new FeePlanResponse.InstallmentResponse();
        response.setId(installment.getId());
        response.setInstallmentNo(installment.getInstallmentNo());
        response.setInstallmentName(installment.getInstallmentName());
        response.setPeriodStartDate(installment.getPeriodStartDate());
        response.setPeriodEndDate(installment.getPeriodEndDate());
        response.setAmountDue(installment.getAmountDue());
        response.setDueDate(installment.getDueDate());
        response.setStatus(installment.getStatus());
        return response;
    }
}
