package org.sma.jpa.repository.fee;

import org.sma.jpa.model.fee.FeePlan;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.school.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for FeePlan entity
 */
@Repository
public interface FeePlanRepository extends JpaRepository<FeePlan, Long> {
    
    List<FeePlan> findBySchoolAndAcademicYear(SchoolProfile school, AcademicYear academicYear);
    
    List<FeePlan> findBySchoolAndAcademicYearAndStatus(SchoolProfile school, AcademicYear academicYear, String status);
    
    Optional<FeePlan> findBySchoolAndAcademicYearAndCategory_Id(SchoolProfile school, AcademicYear academicYear, Long categoryId);
}
