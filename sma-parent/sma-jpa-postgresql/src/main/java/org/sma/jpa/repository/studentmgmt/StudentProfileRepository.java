package org.sma.jpa.repository.studentmgmt;

import org.sma.jpa.model.studentmgmt.StudentProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    Optional<StudentProfile> findBySchoolIdAndAdmissionNo(Long schoolId, String admissionNo);

    Optional<StudentProfile> findByIdAndSchoolIdAndIsDeletedFalse(Long id, Long schoolId);

    Page<StudentProfile> findBySchoolIdAndIsDeletedFalse(Long schoolId, Pageable pageable);

    Page<StudentProfile> findBySchoolIdAndStatusAndIsDeletedFalse(Long schoolId, String status, Pageable pageable);

    @Query("SELECT s FROM StudentProfile s WHERE s.schoolId = :schoolId " +
           "AND s.isDeleted = false " +
           "AND (LOWER(s.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(s.admissionNo) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<StudentProfile> searchStudents(@Param("schoolId") Long schoolId,
                                        @Param("searchTerm") String searchTerm,
                                        Pageable pageable);

    List<StudentProfile> findBySchoolIdAndStatusAndIsDeletedFalse(Long schoolId, String status);

    @Query("SELECT COUNT(s) FROM StudentProfile s WHERE s.schoolId = :schoolId AND s.status = :status AND s.isDeleted = false")
    long countBySchoolIdAndStatus(@Param("schoolId") Long schoolId, @Param("status") String status);
}

