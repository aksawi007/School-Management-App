package org.sma.jpa.repository.master;

import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.master.SubjectMaster;
import org.sma.jpa.model.master.ClassMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for SubjectMaster entity
 */
@Repository
public interface SubjectMasterRepository extends JpaRepository<SubjectMaster, Long> {
    
    List<SubjectMaster> findBySchoolAndIsActiveTrue(SchoolProfile school);
    
    List<SubjectMaster> findBySchoolAndClassMasterAndIsActiveTrue(SchoolProfile school, ClassMaster classMaster);
    
    Optional<SubjectMaster> findBySchoolAndClassMasterAndSubjectCode(SchoolProfile school, ClassMaster classMaster, String subjectCode);
    
    List<SubjectMaster> findBySchoolAndSubjectTypeAndIsActiveTrue(SchoolProfile school, String subjectType);
}


