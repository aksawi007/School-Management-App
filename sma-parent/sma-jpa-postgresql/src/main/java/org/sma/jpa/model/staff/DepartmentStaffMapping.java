package org.sma.jpa.model.staff;

import org.sma.jpa.model.BaseEntity;
import org.sma.jpa.model.master.DepartmentMaster;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Entity for Department-Staff Mapping (Many-to-Many relationship)
 */
@Entity
@Table(name = "department_staff_mapping", schema = "sma_admin",
        uniqueConstraints = @UniqueConstraint(columnNames = {"department_id", "staff_id"}))
public class DepartmentStaffMapping extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private DepartmentMaster department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @Column(name = "role_in_department", length = 100)
    private String roleInDepartment; // e.g., "HOD", "Assistant Professor", "Lecturer"

    @Column(name = "assignment_date")
    private LocalDate assignmentDate;

    @Column(name = "is_primary_department")
    private Boolean isPrimaryDepartment = false;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "remarks", length = 500)
    private String remarks;

    // Constructors
    public DepartmentStaffMapping() {
    }

    // Getters and Setters
    public DepartmentMaster getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentMaster department) {
        this.department = department;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public String getRoleInDepartment() {
        return roleInDepartment;
    }

    public void setRoleInDepartment(String roleInDepartment) {
        this.roleInDepartment = roleInDepartment;
    }

    public LocalDate getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(LocalDate assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public Boolean getIsPrimaryDepartment() {
        return isPrimaryDepartment;
    }

    public void setIsPrimaryDepartment(Boolean isPrimaryDepartment) {
        this.isPrimaryDepartment = isPrimaryDepartment;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
