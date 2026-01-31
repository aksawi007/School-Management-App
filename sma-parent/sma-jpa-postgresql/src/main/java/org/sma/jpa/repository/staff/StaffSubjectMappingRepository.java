package org.sma.jpa.repository.staff;

import org.sma.jpa.model.staff.StaffSubjectMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for StaffSubjectMapping
 */
@Repository
public interface StaffSubjectMappingRepository extends JpaRepository<StaffSubjectMapping, Long> {

    /**
     * Find all staff-subject mappings for a school
     */
    @Query("SELECT ssm FROM StaffSubjectMapping ssm " +
           "WHERE ssm.school.id = :schoolId " +
           "AND ssm.isActive = true")
    List<StaffSubjectMapping> findBySchoolId(@Param("schoolId") Long schoolId);

    /**
     * Find all subjects a staff member can teach
     */
    @Query("SELECT ssm FROM StaffSubjectMapping ssm " +
           "WHERE ssm.school.id = :schoolId " +
           "AND ssm.staff.id = :staffId " +
           "AND ssm.isActive = true")
    List<StaffSubjectMapping> findByStaffId(@Param("schoolId") Long schoolId, 
                                            @Param("staffId") Long staffId);

    /**
     * Find all qualified teachers for a specific subject
     */
    @Query("SELECT ssm FROM StaffSubjectMapping ssm " +
           "WHERE ssm.school.id = :schoolId " +
           "AND ssm.subject.id = :subjectId " +
           "AND ssm.isActive = true " +
           "ORDER BY ssm.proficiencyLevel DESC, ssm.staff.firstName ASC")
    List<StaffSubjectMapping> findQualifiedTeachersForSubject(@Param("schoolId") Long schoolId,
                                                               @Param("subjectId") Long subjectId);

    /**
     * Find specific mapping
     */
    @Query("SELECT ssm FROM StaffSubjectMapping ssm " +
           "WHERE ssm.school.id = :schoolId " +
           "AND ssm.staff.id = :staffId " +
           "AND ssm.subject.id = :subjectId")
    Optional<StaffSubjectMapping> findByStaffAndSubject(@Param("schoolId") Long schoolId,
                                                         @Param("staffId") Long staffId,
                                                         @Param("subjectId") Long subjectId);

    /**
     * Check if staff can teach subject
     */
    @Query("SELECT COUNT(ssm) > 0 FROM StaffSubjectMapping ssm " +
           "WHERE ssm.school.id = :schoolId " +
           "AND ssm.staff.id = :staffId " +
           "AND ssm.subject.id = :subjectId " +
           "AND ssm.isActive = true")
    boolean isStaffQualifiedForSubject(@Param("schoolId") Long schoolId,
                                       @Param("staffId") Long staffId,
                                       @Param("subjectId") Long subjectId);

    /**
     * Find qualified teachers for subject and class level
     */
    @Query("SELECT ssm FROM StaffSubjectMapping ssm " +
           "JOIN ssm.subject sub " +
           "JOIN sub.classMaster cm " +
           "WHERE ssm.school.id = :schoolId " +
           "AND ssm.subject.id = :subjectId " +
           "AND cm.id = :classId " +
           "AND ssm.isActive = true " +
           "ORDER BY ssm.proficiencyLevel DESC")
    List<StaffSubjectMapping> findQualifiedTeachersForClassSubject(@Param("schoolId") Long schoolId,
                                                                    @Param("classId") Long classId,
                                                                    @Param("subjectId") Long subjectId);
}
