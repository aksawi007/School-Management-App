package org.sma.jpa.repository.staff;

import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.staff.Staff;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Staff entity
 */
@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID> {
    
    Optional<Staff> findBySchoolAndEmployeeCode(SchoolProfile school, String employeeCode);
    
    List<Staff> findBySchoolAndStaffType(SchoolProfile school, String staffType);
    
    List<Staff> findBySchoolAndStaffStatusAndIsActiveTrue(SchoolProfile school, String staffStatus);
}


