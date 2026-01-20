package org.sma.jpa.repository.master;

import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.master.SubjectMaster;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for SubjectMaster entity
 */
@Repository
public interface SubjectMasterRepository extends JpaRepository<SubjectMaster, UUID> {
    
    List<SubjectMaster> findBySchoolAndIsActiveTrue(SchoolProfile school);
    
    Optional<SubjectMaster> findBySchoolAndSubjectCode(SchoolProfile school, String subjectCode);
    
    List<SubjectMaster> findBySchoolAndSubjectTypeAndIsActiveTrue(SchoolProfile school, String subjectType);
}


