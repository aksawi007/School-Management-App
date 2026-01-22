package org.sma.jpa.repository.school;

import org.sma.jpa.model.school.SchoolProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for SchoolProfile entity
 */
@Repository
public interface SchoolProfileRepository extends JpaRepository<SchoolProfile, Long> {
    
    Optional<SchoolProfile> findBySchoolCode(String schoolCode);
    
    Optional<SchoolProfile> findBySchoolCodeAndIsActiveTrue(String schoolCode);
}


