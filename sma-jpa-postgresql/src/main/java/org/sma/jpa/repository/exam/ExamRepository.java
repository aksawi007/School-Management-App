package org.sma.jpa.repository.exam;

import org.sma.jpa.model.school.AcademicYear;
import org.sma.jpa.model.exam.Exam;
import org.sma.jpa.model.school.SchoolProfile;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Exam entity
 */
@Repository
public interface ExamRepository extends JpaRepository<Exam, UUID> {
    
    List<Exam> findBySchoolAndAcademicYear(SchoolProfile school, AcademicYear academicYear);
    
    Optional<Exam> findBySchoolAndAcademicYearAndExamCode(SchoolProfile school, AcademicYear academicYear, String examCode);
    
    List<Exam> findBySchoolAndAcademicYearAndExamStatus(SchoolProfile school, AcademicYear academicYear, String examStatus);
}


