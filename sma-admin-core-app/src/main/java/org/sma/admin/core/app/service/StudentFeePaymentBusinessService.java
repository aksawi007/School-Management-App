package org.sma.admin.core.app.service;

import org.sma.admin.core.app.exception.SmaAdminException;
import org.sma.admin.core.app.model.request.StudentFeePaymentRequest;
import org.sma.admin.core.app.model.response.StudentFeePaymentResponse;
import org.sma.admin.core.app.model.response.PendingPaymentResponse;
import org.sma.jpa.model.fee.FeeInstallment;
import org.sma.jpa.model.fee.StudentFeePayment;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.studentmgmt.StudentProfile;
import org.sma.jpa.model.student.StudentClassSectionMapping;
import org.sma.jpa.repository.fee.FeeInstallmentRepository;
import org.sma.jpa.repository.fee.StudentFeePaymentRepository;
import org.sma.jpa.repository.school.SchoolProfileRepository;
import org.sma.jpa.repository.studentmgmt.StudentProfileRepository;
import org.sma.jpa.repository.student.StudentClassSectionMappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Business Service for Student Fee Payment Management
 */
@Service
@Transactional
public class StudentFeePaymentBusinessService {

    private static final Logger logger = LoggerFactory.getLogger(StudentFeePaymentBusinessService.class);

    @Autowired
    private StudentFeePaymentRepository studentFeePaymentRepository;

    @Autowired
    private FeeInstallmentRepository feeInstallmentRepository;

    @Autowired
    private SchoolProfileRepository schoolProfileRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private StudentClassSectionMappingRepository studentClassSectionMappingRepository;

    /**
     * Record student fee payment
     */
    public StudentFeePaymentResponse recordPayment(Long schoolId, StudentFeePaymentRequest request) {
        logger.info("Recording payment for student: {}, installment: {}", 
            request.getStudentId(), request.getFeeInstallmentId());

        SchoolProfile school = schoolProfileRepository.findById(schoolId)
            .orElseThrow(() -> new SmaAdminException("School not found with ID: " + schoolId));

        StudentProfile student = studentProfileRepository.findById(request.getStudentId())
            .orElseThrow(() -> new SmaAdminException("Student not found with ID: " + request.getStudentId()));

        FeeInstallment installment = feeInstallmentRepository.findById(request.getFeeInstallmentId())
            .orElseThrow(() -> new SmaAdminException("Fee installment not found with ID: " + request.getFeeInstallmentId()));

        StudentFeePayment payment = new StudentFeePayment();
        payment.setSchool(school);
        payment.setStudent(student);
        payment.setFeeInstallment(installment);
        payment.setAmountPaid(request.getAmountPaid());
        payment.setDiscountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO);
        payment.setPaidOn(request.getPaidOn());
        payment.setPaymentRef(request.getPaymentRef());
        payment.setRemarks(request.getRemarks());

        StudentFeePayment saved = studentFeePaymentRepository.save(payment);
        logger.info("Payment recorded successfully with ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    /**
     * Get payment by ID
     */
    @Transactional(readOnly = true)
    public StudentFeePaymentResponse getPaymentById(Long paymentId) {
        StudentFeePayment payment = studentFeePaymentRepository.findById(paymentId)
            .orElseThrow(() -> new SmaAdminException("Payment not found with ID: " + paymentId));

        return mapToResponse(payment);
    }

    /**
     * List payments with filters
     * @param schoolId School ID
     * @param studentId Student ID (optional)
     * @param installmentId Installment ID (optional)
     */
    @Transactional(readOnly = true)
    public List<StudentFeePaymentResponse> listPayments(Long schoolId, Long studentId, Long installmentId) {
        logger.info("Fetching payments for school: {}, student: {}, installment: {}", 
            schoolId, studentId, installmentId);

        SchoolProfile school = schoolProfileRepository.findById(schoolId)
            .orElseThrow(() -> new SmaAdminException("School not found with ID: " + schoolId));

        List<StudentFeePayment> payments;

        if (studentId != null && installmentId != null) {
            // Both filters
            payments = studentFeePaymentRepository.findByStudentIdAndInstallmentId(studentId, installmentId);
        } else if (studentId != null) {
            // Student filter only
            StudentProfile student = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new SmaAdminException("Student not found with ID: " + studentId));
            payments = studentFeePaymentRepository.findBySchoolAndStudent(school, student);
        } else if (installmentId != null) {
            // Installment filter only
            payments = studentFeePaymentRepository.findByFeeInstallmentId(installmentId);
        } else {
            // No specific filter, get all for school
            payments = studentFeePaymentRepository.findAll().stream()
                .filter(p -> p.getSchool().getId().equals(schoolId))
                .collect(Collectors.toList());
        }

        return payments.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Delete payment
     */
    public void deletePayment(Long paymentId) {
        logger.info("Deleting payment: {}", paymentId);

        StudentFeePayment payment = studentFeePaymentRepository.findById(paymentId)
            .orElseThrow(() -> new SmaAdminException("Payment not found with ID: " + paymentId));

        studentFeePaymentRepository.delete(payment);
        logger.info("Payment deleted successfully: {}", paymentId);
    }

    /**
     * Map entity to response DTO
     */
    private StudentFeePaymentResponse mapToResponse(StudentFeePayment payment) {
        StudentFeePaymentResponse response = new StudentFeePaymentResponse();
        response.setId(payment.getId());
        response.setSchoolId(payment.getSchool().getId());
        response.setStudentId(payment.getStudent().getId());
        response.setStudentName(payment.getStudent().getFirstName() + " " + payment.getStudent().getLastName());
        response.setFeeInstallmentId(payment.getFeeInstallment().getId());
        response.setInstallmentName(payment.getFeeInstallment().getInstallmentName());
        response.setAmountPaid(payment.getAmountPaid());
        response.setDiscountAmount(payment.getDiscountAmount());
        response.setPaidOn(payment.getPaidOn());
        response.setPaymentRef(payment.getPaymentRef());
        response.setRemarks(payment.getRemarks());
        return response;
    }

    /**
     * Get pending payments for students in a class/section
     * @param schoolId School ID
     * @param academicYearId Academic Year ID
     * @param classId Class ID (required)
     * @param sectionId Section ID (optional)
     * @return List of pending payments
     */
    @Transactional(readOnly = true)
    public List<PendingPaymentResponse> getPendingPayments(Long schoolId, Long academicYearId, 
                                                           Long classId, Long sectionId) {
        logger.info("Fetching pending payments for school: {}, academicYear: {}, class: {}, section: {}", 
            schoolId, academicYearId, classId, sectionId);

        // Get all students in the class/section
        List<StudentClassSectionMapping> studentMappings;
        if (sectionId != null) {
            studentMappings = studentClassSectionMappingRepository.findByClassAndSection(classId, sectionId);
        } else {
            studentMappings = studentClassSectionMappingRepository.findByClass(classId);
        }

        if (studentMappings.isEmpty()) {
            logger.info("No students found for the given class/section");
            return new ArrayList<>();
        }

        // Get all overdue installments for the academic year
        LocalDate today = LocalDate.now();
        List<FeeInstallment> overdueInstallments = feeInstallmentRepository.findAll().stream()
            .filter(installment -> {
                // Check if installment belongs to the academic year
                boolean belongsToAcademicYear = installment.getFeePlan() != null 
                    && installment.getFeePlan().getAcademicYear() != null
                    && installment.getFeePlan().getAcademicYear().getId().equals(academicYearId);
                
                // Check if due date has passed
                boolean isDueDate = installment.getDueDate() != null 
                    && installment.getDueDate().isBefore(today);
                
                return belongsToAcademicYear && isDueDate;
            })
            .collect(Collectors.toList());

        if (overdueInstallments.isEmpty()) {
            logger.info("No overdue installments found for academic year: {}", academicYearId);
            return new ArrayList<>();
        }

        logger.info("Found {} students and {} overdue installments", 
            studentMappings.size(), overdueInstallments.size());

        // Build pending payments list
        List<PendingPaymentResponse> pendingPayments = new ArrayList<>();

        for (StudentClassSectionMapping mapping : studentMappings) {
            StudentProfile student = mapping.getStudent();
            
            for (FeeInstallment installment : overdueInstallments) {
                // Check if payment exists for this student and installment
                List<StudentFeePayment> existingPayments = studentFeePaymentRepository
                    .findByStudentIdAndInstallmentId(student.getId(), installment.getId());

                if (existingPayments.isEmpty()) {
                    // No payment found - this is a pending payment
                    PendingPaymentResponse pending = new PendingPaymentResponse();
                    pending.setStudentId(student.getId());
                    pending.setStudentName(student.getFirstName() + " " + student.getLastName());
                    pending.setAdmissionNo(student.getAdmissionNo());
                    pending.setRollNumber(mapping.getRollNumber());
                    pending.setClassName(mapping.getClassMaster().getClassName());
                    pending.setSectionName(mapping.getSection() != null ? mapping.getSection().getSectionName() : "");
                    pending.setFeeInstallmentId(installment.getId());
                    pending.setInstallmentName(installment.getInstallmentName());
                    pending.setFeeCategoryName(installment.getFeePlan().getCategory().getCategoryName());
                    pending.setAmountDue(installment.getAmountDue());
                    pending.setDueDate(installment.getDueDate());
                    
                    // Calculate days past due
                    long daysPastDue = ChronoUnit.DAYS.between(installment.getDueDate(), today);
                    pending.setDaysPastDue((int) daysPastDue);
                    
                    pendingPayments.add(pending);
                }
            }
        }

        logger.info("Found {} pending payments", pendingPayments.size());
        return pendingPayments;
    }
}
