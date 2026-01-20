package org.sma.jpa.repository.fee;

import org.sma.jpa.model.fee.FeeCategory;
import org.sma.jpa.model.school.SchoolProfile;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for FeeCategory entity
 */
@Repository
public interface FeeCategoryRepository extends JpaRepository<FeeCategory, UUID> {
    
    List<FeeCategory> findBySchoolAndIsActiveTrue(SchoolProfile school);
    
    Optional<FeeCategory> findBySchoolAndCategoryCode(SchoolProfile school, String categoryCode);
    
    List<FeeCategory> findBySchoolAndCategoryType(SchoolProfile school, String categoryType);
}


