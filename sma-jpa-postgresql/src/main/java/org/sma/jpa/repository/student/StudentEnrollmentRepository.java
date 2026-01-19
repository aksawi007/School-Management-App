package org.sma.jpa.repository.student;

import org.sma.jpa.model.school.AcademicYear;
import org.sma.jpa.model.master.ClassMaster;
import org.sma.jpa.model.master.SectionMaster;
import org.sma.jpa.model.student.Student;
import org.sma.jpa.model.student.StudentEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for StudentEnrollment entity
 */
@Repository
public interface StudentEnrollmentRepository extends JpaRepository<StudentEnrollment, Long> {
    
    Optional<StudentEnrollment> findByStudentAndAcademicYear(Student student, AcademicYear academicYear);
    
    List<StudentEnrollment> findByAcademicYearAndClassMasterAndSection(AcademicYear academicYear, ClassMaster classMaster, SectionMaster section);
    
    List<StudentEnrollment> findByStudentAndIsActiveTrue(Student student);
}
