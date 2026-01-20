package org.sma.jpa.repository.master;

import org.sma.jpa.model.master.ClassMaster;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.master.SectionMaster;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for SectionMaster entity
 */
@Repository
public interface SectionMasterRepository extends JpaRepository<SectionMaster, UUID> {
    
    List<SectionMaster> findBySchoolAndClassMasterAndIsActiveTrue(SchoolProfile school, ClassMaster classMaster);
    
    Optional<SectionMaster> findBySchoolAndClassMasterAndSectionCode(SchoolProfile school, ClassMaster classMaster, String sectionCode);
}


