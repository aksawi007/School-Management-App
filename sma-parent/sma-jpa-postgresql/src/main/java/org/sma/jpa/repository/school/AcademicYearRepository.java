package org.sma.jpa.repository.school;

import org.sma.jpa.model.school.AcademicYear;
import org.sma.jpa.model.school.SchoolProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for AcademicYear entity
 */
@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    
    List<AcademicYear> findBySchoolAndIsActiveTrue(SchoolProfile school);
    
    Optional<AcademicYear> findBySchoolAndYearCode(SchoolProfile school, String yearCode);
    
    Optional<AcademicYear> findBySchoolAndIsCurrentTrue(SchoolProfile school);
    
    Optional<AcademicYear> findByYearName(String yearName);
    
    @Query("SELECT a FROM AcademicYear a ORDER BY a.endDate DESC, a.startDate DESC")
    List<AcademicYear> findAllOrderByEndDateDesc();
    
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE AcademicYear a SET a.isCurrent = false WHERE a.isCurrent = true AND a.school.id = :schoolId")
    void updateAllToNonCurrentForSchool(@Param("schoolId") Long schoolId);
    
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE AcademicYear a SET a.isCurrent = false WHERE a.isCurrent = true AND a.id != :yearId AND a.school.id = :schoolId")
    void updateAllToNonCurrentExceptForSchool(@Param("yearId") Long yearId, @Param("schoolId") Long schoolId);
}


