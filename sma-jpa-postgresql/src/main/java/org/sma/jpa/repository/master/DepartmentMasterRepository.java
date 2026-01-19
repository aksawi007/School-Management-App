package org.sma.jpa.repository.master;

import org.sma.jpa.model.master.DepartmentMaster;
import org.sma.jpa.model.school.SchoolProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for DepartmentMaster entity
 */
@Repository
public interface DepartmentMasterRepository extends JpaRepository<DepartmentMaster, Long> {
    
    List<DepartmentMaster> findBySchoolAndIsActiveTrue(SchoolProfile school);
    
    Optional<DepartmentMaster> findBySchoolAndDepartmentCode(SchoolProfile school, String departmentCode);
}
