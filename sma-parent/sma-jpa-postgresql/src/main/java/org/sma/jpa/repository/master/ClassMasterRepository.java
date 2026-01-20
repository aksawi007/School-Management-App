package org.sma.jpa.repository.master;

import org.sma.jpa.model.master.ClassMaster;
import org.sma.jpa.model.school.SchoolProfile;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ClassMaster entity
 */
@Repository
public interface ClassMasterRepository extends JpaRepository<ClassMaster, UUID> {
    
    List<ClassMaster> findBySchoolAndIsActiveTrue(SchoolProfile school);
    
    Optional<ClassMaster> findBySchoolAndClassCode(SchoolProfile school, String classCode);
}


