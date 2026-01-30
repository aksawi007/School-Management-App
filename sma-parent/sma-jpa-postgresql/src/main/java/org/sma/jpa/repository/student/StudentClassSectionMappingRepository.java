package org.sma.jpa.repository.student;


import org.sma.jpa.model.student.StudentClassSectionMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for StudentClassSectionMapping
 */
@Repository
public interface StudentClassSectionMappingRepository extends JpaRepository<StudentClassSectionMapping, Long> {

    /**
     * Find active mapping for a student in a specific academic year
     */
    @Query("SELECT scsm FROM StudentClassSectionMapping scsm WHERE scsm.student.id = :studentId " +
            "AND scsm.academicYear.id = :academicYearId AND scsm.isActive = true")
    Optional<StudentClassSectionMapping> findActiveByStudentAndAcademicYear(
            @Param("studentId") Long studentId,
            @Param("academicYearId") Long academicYearId);

    /**
     * Find all students in a specific class and section
     */
    @Query("SELECT scsm FROM StudentClassSectionMapping scsm WHERE scsm.classMaster.id = :classId " +
            "AND scsm.section.id = :sectionId AND scsm.isActive = true")
    List<StudentClassSectionMapping> findByClassAndSection(
            @Param("classId") Long classId,
            @Param("sectionId") Long sectionId);

    /**
     * Find all students in a specific class (all sections)
     */
    @Query("SELECT scsm FROM StudentClassSectionMapping scsm WHERE scsm.classMaster.id = :classId " +
            "AND scsm.isActive = true")
    List<StudentClassSectionMapping> findByClass(@Param("classId") Long classId);

    /**
     * Find all students in a specific academic year
     */
    @Query("SELECT scsm FROM StudentClassSectionMapping scsm WHERE scsm.academicYear.id = :academicYearId " +
            "AND scsm.isActive = true")
    List<StudentClassSectionMapping> findByAcademicYear(@Param("academicYearId") Long academicYearId);

    /**
     * Find student's complete enrollment history
     */
    @Query("SELECT scsm FROM StudentClassSectionMapping scsm WHERE scsm.student.id = :studentId " +
            "ORDER BY scsm.academicYear.yearName DESC, scsm.enrollmentDate DESC")
    List<StudentClassSectionMapping> findStudentHistory(@Param("studentId") Long studentId);

    /**
     * Count students in a class/section
     */
    @Query("SELECT COUNT(scsm) FROM StudentClassSectionMapping scsm WHERE scsm.classMaster.id = :classId " +
            "AND scsm.section.id = :sectionId AND scsm.isActive = true")
    Long countByClassAndSection(@Param("classId") Long classId, @Param("sectionId") Long sectionId);

    /**
     * Find students in a class and section for a specific academic year
     */
    @Query("SELECT scsm FROM StudentClassSectionMapping scsm WHERE scsm.academicYear.id = :academicYearId " +
            "AND scsm.classMaster.id = :classId AND scsm.section.id = :sectionId AND scsm.isActive = true " +
            "ORDER BY scsm.rollNumber ASC, scsm.student.firstName ASC")
    List<StudentClassSectionMapping> findByAcademicYearAndClassAndSection(
            @Param("academicYearId") Long academicYearId,
            @Param("classId") Long classId,
            @Param("sectionId") Long sectionId);

    /**
     * Find all active mappings by academic year and class
     */
    @Query("SELECT scsm FROM StudentClassSectionMapping scsm WHERE scsm.academicYear.id = :academicYearId " +
            "AND scsm.classMaster.id = :classId AND scsm.isActive = true " +
            "ORDER BY scsm.section.sectionCode ASC, scsm.rollNumber ASC")
    List<StudentClassSectionMapping> findByAcademicYearAndClass(
            @Param("academicYearId") Long academicYearId,
            @Param("classId") Long classId);
}
