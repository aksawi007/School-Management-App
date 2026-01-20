package org.sma.jpa.repository.fee;

import org.sma.jpa.model.school.AcademicYear;
import org.sma.jpa.model.fee.FeeStructure;
import org.sma.jpa.model.school.SchoolProfile;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for FeeStructure entity
 */
@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, UUID> {
    
    List<FeeStructure> findBySchoolAndAcademicYear(SchoolProfile school, AcademicYear academicYear);
    
    Optional<FeeStructure> findBySchoolAndAcademicYearAndStructureCode(SchoolProfile school, AcademicYear academicYear, String structureCode);
    
    List<FeeStructure> findBySchoolAndAcademicYearAndStructureStatus(SchoolProfile school, AcademicYear academicYear, String structureStatus);
}


