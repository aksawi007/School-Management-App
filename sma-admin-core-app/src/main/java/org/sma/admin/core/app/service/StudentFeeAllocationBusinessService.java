package org.sma.admin.core.app.service;

import org.sma.admin.core.app.exception.SmaAdminException;
import org.sma.admin.core.app.model.request.StudentFeeAllocationRequest;
import org.sma.admin.core.app.model.response.StudentFeeAllocationResponse;
import org.sma.jpa.model.fee.FeeCategory;
import org.sma.jpa.model.fee.FeeStructure;
import org.sma.jpa.model.fee.StudentFeeAllocation;
import org.sma.jpa.model.school.AcademicYear;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.studentmgmt.StudentProfile;
import org.sma.jpa.repository.fee.FeeCategoryRepository;
import org.sma.jpa.repository.fee.FeeStructureRepository;
import org.sma.jpa.repository.fee.StudentFeeAllocationRepository;
import org.sma.jpa.repository.school.AcademicYearRepository;
import org.sma.jpa.repository.school.SchoolProfileRepository;
import org.sma.jpa.repository.studentmgmt.StudentProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Business Service for Student Fee Allocation
 * Handles manual fee allocation to students by admin
 */
@Service
@Transactional
public class StudentFeeAllocationBusinessService {

    private static final Logger logger = LoggerFactory.getLogger(StudentFeeAllocationBusinessService.class);

    @Autowired
    private StudentFeeAllocationRepository studentFeeAllocationRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private FeeCategoryRepository feeCategoryRepository;

    @Autowired
    private FeeStructureRepository feeStructureRepository;

    @Autowired
    private SchoolProfileRepository schoolProfileRepository;

    @Autowired
    private AcademicYearRepository academicYearRepository;

    /**
     * Manually allocate fee to a student
     */
    public StudentFeeAllocationResponse allocateFeeToStudent(
            Long schoolId, Long academicYearId, StudentFeeAllocationRequest request, Long allocatedBy) {
        
        logger.info("Allocating fee to student: {}, category: {}", request.getStudentId(), request.getFeeCategoryId());

        // Fetch required entities
        SchoolProfile school = schoolProfileRepository.findById(schoolId)
            .orElseThrow(() -> new SmaAdminException("School not found with ID: " + schoolId));

        AcademicYear academicYear = academicYearRepository.findById(academicYearId)
            .orElseThrow(() -> new SmaAdminException("Academic year not found with ID: " + academicYearId));

        StudentProfile student = studentProfileRepository.findById(request.getStudentId())
            .orElseThrow(() -> new SmaAdminException("Student not found with ID: " + request.getStudentId()));

        FeeCategory feeCategory = feeCategoryRepository.findById(request.getFeeCategoryId())
            .orElseThrow(() -> new SmaAdminException("Fee category not found with ID: " + request.getFeeCategoryId()));

        // Optional fee structure
        FeeStructure feeStructure = null;
        if (request.getFeeStructureId() != null) {
            feeStructure = feeStructureRepository.findById(request.getFeeStructureId())
                .orElseThrow(() -> new SmaAdminException("Fee structure not found with ID: " + request.getFeeStructureId()));
        }

        // Validate duration and month/quarter compatibility
        validateDurationAndPeriod(request);

        // Create allocation
        StudentFeeAllocation allocation = new StudentFeeAllocation();
        allocation.setSchool(school);
        allocation.setAcademicYear(academicYear);
        allocation.setStudent(student);
        allocation.setFeeCategory(feeCategory);
        allocation.setFeeStructure(feeStructure);
        allocation.setAllocationCode(generateAllocationCode(school, academicYear));
        allocation.setFeeAmount(request.getFeeAmount());
        allocation.setDurationType(request.getDurationType());
        allocation.setApplicableMonth(request.getApplicableMonth());
        allocation.setQuarter(request.getQuarter());
        allocation.setDueDate(request.getDueDate());
        allocation.setAllocationDate(LocalDate.now());
        allocation.setAllocationStatus("PENDING");
        allocation.setPaidAmount(BigDecimal.ZERO);
        
        // Calculate pending amount considering discount
        BigDecimal discountAmount = request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal pendingAmount = request.getFeeAmount().subtract(discountAmount);
        allocation.setPendingAmount(pendingAmount);
        allocation.setDiscountAmount(discountAmount);
        allocation.setDiscountReason(request.getDiscountReason());
        
        allocation.setPaymentDeadline(request.getPaymentDeadline());
        allocation.setIsMandatory(request.getIsMandatory());
        allocation.setAutoGenerated(false); // Manual allocation
        allocation.setRemarks(request.getRemarks());
        allocation.setAllocatedBy(allocatedBy);

        StudentFeeAllocation saved = studentFeeAllocationRepository.save(allocation);
        logger.info("Fee allocated successfully with code: {}", saved.getAllocationCode());

        return mapToResponse(saved);
    }

    /**
     * Get fee allocations for a student in an academic year
     */
    @Transactional(readOnly = true)
    public List<StudentFeeAllocationResponse> getStudentFeeAllocations(Long studentId, Long academicYearId) {
        logger.info("Fetching fee allocations for student: {}, academic year: {}", studentId, academicYearId);

        StudentProfile student = studentProfileRepository.findById(studentId)
            .orElseThrow(() -> new SmaAdminException("Student not found with ID: " + studentId));

        AcademicYear academicYear = academicYearRepository.findById(academicYearId)
            .orElseThrow(() -> new SmaAdminException("Academic year not found with ID: " + academicYearId));

        List<StudentFeeAllocation> allocations = studentFeeAllocationRepository
            .findByStudentAndAcademicYear(student, academicYear);

        return allocations.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get pending fee allocations for a student
     */
    @Transactional(readOnly = true)
    public List<StudentFeeAllocationResponse> getPendingFeeAllocations(Long studentId) {
        logger.info("Fetching pending fee allocations for student: {}", studentId);

        StudentProfile student = studentProfileRepository.findById(studentId)
            .orElseThrow(() -> new SmaAdminException("Student not found with ID: " + studentId));

        List<StudentFeeAllocation> allocations = studentFeeAllocationRepository
            .findByStudentAndAllocationStatus(student, "PENDING");

        return allocations.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get overdue fee allocations for school
     */
    @Transactional(readOnly = true)
    public List<StudentFeeAllocationResponse> getOverdueFeeAllocations(Long schoolId, Long academicYearId) {
        logger.info("Fetching overdue fee allocations for school: {}, academic year: {}", schoolId, academicYearId);

        SchoolProfile school = schoolProfileRepository.findById(schoolId)
            .orElseThrow(() -> new SmaAdminException("School not found with ID: " + schoolId));

        AcademicYear academicYear = academicYearRepository.findById(academicYearId)
            .orElseThrow(() -> new SmaAdminException("Academic year not found with ID: " + academicYearId));

        List<StudentFeeAllocation> allocations = studentFeeAllocationRepository
            .findOverdueFees(school, academicYear, LocalDate.now());

        return allocations.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get fee allocations by category for a specific month
     */
    @Transactional(readOnly = true)
    public List<StudentFeeAllocationResponse> getFeeAllocationsByMonth(
            Long schoolId, Long academicYearId, String month) {
        
        logger.info("Fetching fee allocations for school: {}, year: {}, month: {}", schoolId, academicYearId, month);

        SchoolProfile school = schoolProfileRepository.findById(schoolId)
            .orElseThrow(() -> new SmaAdminException("School not found with ID: " + schoolId));

        AcademicYear academicYear = academicYearRepository.findById(academicYearId)
            .orElseThrow(() -> new SmaAdminException("Academic year not found with ID: " + academicYearId));

        List<StudentFeeAllocation> allocations = studentFeeAllocationRepository
            .findBySchoolAndAcademicYearAndApplicableMonth(school, academicYear, month);

        return allocations.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Calculate total pending amount for a student
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalPendingAmount(Long studentId, Long academicYearId) {
        StudentProfile student = studentProfileRepository.findById(studentId)
            .orElseThrow(() -> new SmaAdminException("Student not found with ID: " + studentId));

        AcademicYear academicYear = academicYearRepository.findById(academicYearId)
            .orElseThrow(() -> new SmaAdminException("Academic year not found with ID: " + academicYearId));

        return studentFeeAllocationRepository.calculateTotalPendingAmount(student, academicYear);
    }

    /**
     * Update fee allocation status (for payment processing)
     */
    public StudentFeeAllocationResponse updateAllocationStatus(
            Long allocationId, String status, BigDecimal paidAmount) {
        
        logger.info("Updating allocation status: {}, new status: {}", allocationId, status);

        StudentFeeAllocation allocation = studentFeeAllocationRepository.findById(allocationId)
            .orElseThrow(() -> new SmaAdminException("Fee allocation not found with ID: " + allocationId));

        allocation.setAllocationStatus(status);
        
        if (paidAmount != null) {
            allocation.setPaidAmount(allocation.getPaidAmount().add(paidAmount));
            BigDecimal newPending = allocation.getFeeAmount()
                .subtract(allocation.getDiscountAmount())
                .subtract(allocation.getPaidAmount());
            allocation.setPendingAmount(newPending.max(BigDecimal.ZERO));

            // Auto-update status based on pending amount
            if (allocation.getPendingAmount().compareTo(BigDecimal.ZERO) == 0) {
                allocation.setAllocationStatus("PAID");
            } else if (allocation.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
                allocation.setAllocationStatus("PARTIALLY_PAID");
            }
        }

        StudentFeeAllocation updated = studentFeeAllocationRepository.save(allocation);
        return mapToResponse(updated);
    }

    /**
     * Cancel fee allocation
     */
    public void cancelFeeAllocation(Long allocationId, String reason) {
        logger.info("Cancelling fee allocation: {}", allocationId);

        StudentFeeAllocation allocation = studentFeeAllocationRepository.findById(allocationId)
            .orElseThrow(() -> new SmaAdminException("Fee allocation not found with ID: " + allocationId));

        if (allocation.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new SmaAdminException("Cannot cancel allocation with payment already made");
        }

        allocation.setAllocationStatus("CANCELLED");
        allocation.setRemarks(allocation.getRemarks() + " | CANCELLED: " + reason);
        studentFeeAllocationRepository.save(allocation);

        logger.info("Fee allocation cancelled: {}", allocationId);
    }

    /**
     * Validate duration type and period compatibility
     */
    private void validateDurationAndPeriod(StudentFeeAllocationRequest request) {
        String durationType = request.getDurationType();

        if ("MONTHLY".equals(durationType) && request.getApplicableMonth() == null) {
            throw new SmaAdminException("Applicable month is required for MONTHLY duration");
        }

        if ("QUARTERLY".equals(durationType) && request.getQuarter() == null) {
            throw new SmaAdminException("Quarter is required for QUARTERLY duration");
        }

        if (("HALF_YEARLY".equals(durationType) || "ANNUAL".equals(durationType) || "ONE_TIME".equals(durationType))
                && request.getApplicableMonth() != null) {
            throw new SmaAdminException("Applicable month should not be specified for " + durationType + " duration");
        }
    }

    /**
     * Generate unique allocation code
     */
    private String generateAllocationCode(SchoolProfile school, AcademicYear academicYear) {
        String yearCode = academicYear.getYearName().substring(0, 4);
        long count = studentFeeAllocationRepository.count() + 1;
        return String.format("SFA-%s-%s-%06d", school.getSchoolCode(), yearCode, count);
    }

    /**
     * Map entity to response DTO
     */
    private StudentFeeAllocationResponse mapToResponse(StudentFeeAllocation allocation) {
        StudentFeeAllocationResponse response = new StudentFeeAllocationResponse();
        response.setId(allocation.getId());
        response.setAllocationCode(allocation.getAllocationCode());
        response.setStudentId(allocation.getStudent().getId());
        response.setStudentName(allocation.getStudent().getFirstName() + " " + allocation.getStudent().getLastName());
        response.setStudentRollNumber(allocation.getStudent().getAdmissionNo());
        response.setFeeCategoryId(allocation.getFeeCategory().getId());
        response.setFeeCategoryName(allocation.getFeeCategory().getCategoryName());
        response.setFeeCategoryType(allocation.getFeeCategory().getCategoryType());
        
        if (allocation.getFeeStructure() != null) {
            response.setFeeStructureId(allocation.getFeeStructure().getId());
            response.setFeeStructureName(allocation.getFeeStructure().getStructureName());
        }
        
        response.setFeeAmount(allocation.getFeeAmount());
        response.setDurationType(allocation.getDurationType());
        response.setApplicableMonth(allocation.getApplicableMonth());
        response.setQuarter(allocation.getQuarter());
        response.setDueDate(allocation.getDueDate());
        response.setAllocationDate(allocation.getAllocationDate());
        response.setAllocationStatus(allocation.getAllocationStatus());
        response.setPaidAmount(allocation.getPaidAmount());
        response.setPendingAmount(allocation.getPendingAmount());
        response.setDiscountAmount(allocation.getDiscountAmount());
        response.setDiscountReason(allocation.getDiscountReason());
        response.setLateFeeAmount(allocation.getLateFeeAmount());
        response.setLateFeeAppliedDate(allocation.getLateFeeAppliedDate());
        response.setPaymentDeadline(allocation.getPaymentDeadline());
        response.setIsMandatory(allocation.getIsMandatory());
        response.setAutoGenerated(allocation.getAutoGenerated());
        response.setRemarks(allocation.getRemarks());
        response.setAcademicYear(allocation.getAcademicYear().getYearName());
        response.setCreatedDate(allocation.getCreatedAt() != null ? allocation.getCreatedAt().toLocalDate() : null);
        
        return response;
    }
}
