package org.sma.jpa.repository.studentmgmt;

import org.sma.jpa.model.studentmgmt.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

    List<Enrollment> findByStudentIdOrderByStartDateDesc(UUID studentId);

    Optional<Enrollment> findByStudentIdAndStatusAndIsDeletedFalse(UUID studentId, String status);

    Optional<Enrollment> findByStudentIdAndAcademicYearIdAndStatusAndIsDeletedFalse(
            UUID studentId, UUID academicYearId, String status);

    List<Enrollment> findBySchoolIdAndAcademicYearIdAndClassIdAndIsDeletedFalse(
            UUID schoolId, UUID academicYearId, UUID classId);

    List<Enrollment> findBySchoolIdAndAcademicYearIdAndClassIdAndSectionIdAndStatusAndIsDeletedFalse(
            UUID schoolId, UUID academicYearId, UUID classId, UUID sectionId, String status);

    @Query("SELECT e FROM Enrollment e WHERE e.schoolId = :schoolId " +
           "AND e.academicYearId = :academicYearId " +
           "AND (:classId IS NULL OR e.classId = :classId) " +
           "AND (:sectionId IS NULL OR e.sectionId = :sectionId) " +
           "AND e.status = :status " +
           "AND e.isDeleted = false")
    List<Enrollment> findEnrollmentsByFilters(@Param("schoolId") UUID schoolId,
                                               @Param("academicYearId") UUID academicYearId,
                                               @Param("classId") UUID classId,
                                               @Param("sectionId") UUID sectionId,
                                               @Param("status") String status);

    long countBySchoolIdAndAcademicYearIdAndStatusAndIsDeletedFalse(
            UUID schoolId, UUID academicYearId, String status);
}

