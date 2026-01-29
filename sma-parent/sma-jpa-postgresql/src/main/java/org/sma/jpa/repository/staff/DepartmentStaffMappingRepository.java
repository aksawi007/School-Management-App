package org.sma.jpa.repository.staff;

import org.sma.jpa.model.staff.DepartmentStaffMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for DepartmentStaffMapping
 */
@Repository
public interface DepartmentStaffMappingRepository extends JpaRepository<DepartmentStaffMapping, Long> {

    /**
     * Find all staff mappings for a department
     */
    @Query("SELECT dsm FROM DepartmentStaffMapping dsm WHERE dsm.department.id = :departmentId AND dsm.isActive = true")
    List<DepartmentStaffMapping> findByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * Find all department mappings for a staff member
     */
    @Query("SELECT dsm FROM DepartmentStaffMapping dsm WHERE dsm.staff.id = :staffId AND dsm.isActive = true")
    List<DepartmentStaffMapping> findByStaffId(@Param("staffId") Long staffId);

    /**
     * Find specific mapping by department and staff
     */
    @Query("SELECT dsm FROM DepartmentStaffMapping dsm WHERE dsm.department.id = :departmentId AND dsm.staff.id = :staffId AND dsm.isActive = true")
    Optional<DepartmentStaffMapping> findByDepartmentIdAndStaffId(
            @Param("departmentId") Long departmentId,
            @Param("staffId") Long staffId);

    /**
     * Find all staff in a school's departments
     */
    @Query("SELECT dsm FROM DepartmentStaffMapping dsm WHERE dsm.department.school.id = :schoolId AND dsm.isActive = true")
    List<DepartmentStaffMapping> findBySchoolId(@Param("schoolId") Long schoolId);

    /**
     * Find primary department for a staff member
     */
    @Query("SELECT dsm FROM DepartmentStaffMapping dsm WHERE dsm.staff.id = :staffId AND dsm.isPrimaryDepartment = true AND dsm.isActive = true")
    Optional<DepartmentStaffMapping> findPrimaryDepartmentByStaffId(@Param("staffId") Long staffId);
}
