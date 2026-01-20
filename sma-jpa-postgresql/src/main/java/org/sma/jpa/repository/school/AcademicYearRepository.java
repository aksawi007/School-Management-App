package org.sma.jpa.repository.school;

import org.sma.jpa.model.school.AcademicYear;
import org.sma.jpa.model.school.SchoolProfile;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for AcademicYear entity
 */
@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, UUID> {
    
    List<AcademicYear> findBySchoolAndIsActiveTrue(SchoolProfile school);
    
    Optional<AcademicYear> findBySchoolAndYearCode(SchoolProfile school, String yearCode);
    
    Optional<AcademicYear> findBySchoolAndIsCurrentTrue(SchoolProfile school);
}


