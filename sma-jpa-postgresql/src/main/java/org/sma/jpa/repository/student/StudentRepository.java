package org.sma.jpa.repository.student;

import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Student entity
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    Optional<Student> findBySchoolAndAdmissionNumber(SchoolProfile school, String admissionNumber);
    
    List<Student> findBySchoolAndStudentStatus(SchoolProfile school, String studentStatus);
    
    List<Student> findBySchoolAndIsActiveTrue(SchoolProfile school);
}
