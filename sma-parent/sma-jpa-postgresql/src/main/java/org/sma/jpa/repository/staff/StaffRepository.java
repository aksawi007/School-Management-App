package org.sma.jpa.repository.staff;

import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.staff.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Staff entity
 */
@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    
    Optional<Staff> findBySchoolAndEmployeeCode(SchoolProfile school, String employeeCode);
    
    List<Staff> findBySchoolAndStaffType(SchoolProfile school, String staffType);
    
    List<Staff> findBySchoolAndStaffStatusAndIsActiveTrue(SchoolProfile school, String staffStatus);
    
    List<Staff> findBySchoolAndStaffTypeAndStaffStatusAndIsActiveTrue(SchoolProfile school, String staffType, String staffStatus);
    
    List<Staff> findBySchoolIdAndIsActiveTrue(Long schoolId);
    
    List<Staff> findBySchoolIdAndStaffTypeAndIsActiveTrue(Long schoolId, String staffType);
}


