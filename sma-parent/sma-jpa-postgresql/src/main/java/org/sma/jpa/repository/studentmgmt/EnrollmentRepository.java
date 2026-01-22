package org.sma.jpa.repository.studentmgmt;

import org.sma.jpa.model.studentmgmt.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentIdOrderByStartDateDesc(Long studentId);

    Optional<Enrollment> findByStudentIdAndStatusAndIsDeletedFalse(Long studentId, String status);

    Optional<Enrollment> findByStudentIdAndAcademicYearIdAndStatusAndIsDeletedFalse(
            Long studentId, Long academicYearId, String status);

    List<Enrollment> findBySchoolIdAndAcademicYearIdAndClassIdAndIsDeletedFalse(
            Long schoolId, Long academicYearId, Long classId);

    List<Enrollment> findBySchoolIdAndAcademicYearIdAndClassIdAndSectionIdAndStatusAndIsDeletedFalse(
            Long schoolId, Long academicYearId, Long classId, Long sectionId, String status);

    @Query("SELECT e FROM Enrollment e WHERE e.schoolId = :schoolId " +
           "AND e.academicYearId = :academicYearId " +
           "AND (:classId IS NULL OR e.classId = :classId) " +
           "AND (:sectionId IS NULL OR e.sectionId = :sectionId) " +
           "AND e.status = :status " +
           "AND e.isDeleted = false")
    List<Enrollment> findEnrollmentsByFilters(@Param("schoolId") Long schoolId,
                                               @Param("academicYearId") Long academicYearId,
                                               @Param("classId") Long classId,
                                               @Param("sectionId") Long sectionId,
                                               @Param("status") String status);

    long countBySchoolIdAndAcademicYearIdAndStatusAndIsDeletedFalse(
            Long schoolId, Long academicYearId, String status);
}

