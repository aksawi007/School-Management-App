package org.sma.jpa.repository.fee;

import org.sma.jpa.model.fee.StudentFeePayment;
import org.sma.jpa.model.fee.FeeInstallment;
import org.sma.jpa.model.studentmgmt.StudentProfile;
import org.sma.jpa.model.school.SchoolProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for StudentFeePayment entity
 */
@Repository
public interface StudentFeePaymentRepository extends JpaRepository<StudentFeePayment, Long> {
    
    List<StudentFeePayment> findByStudentAndFeeInstallment(StudentProfile student, FeeInstallment feeInstallment);
    
    List<StudentFeePayment> findByStudent(StudentProfile student);
    
    List<StudentFeePayment> findBySchoolAndStudent(SchoolProfile school, StudentProfile student);
    
    @Query("SELECT sfp FROM StudentFeePayment sfp WHERE sfp.feeInstallment.id = :installmentId")
    List<StudentFeePayment> findByFeeInstallmentId(@Param("installmentId") Long installmentId);
    
    @Query("SELECT sfp FROM StudentFeePayment sfp WHERE sfp.student.id = :studentId AND sfp.feeInstallment.id = :installmentId")
    List<StudentFeePayment> findByStudentIdAndInstallmentId(@Param("studentId") Long studentId, @Param("installmentId") Long installmentId);
}
