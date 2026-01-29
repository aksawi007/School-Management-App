package org.sma.admin.core.app.model.request;

import java.time.LocalDate;

/**
 * Request model for Department-Staff Mapping
 */
public class DepartmentStaffMappingRequest {

    private Long departmentId;
    private Long staffId;
    private String roleInDepartment;
    private LocalDate assignmentDate;
    private Boolean isPrimaryDepartment;
    private String remarks;

    // Constructors
    public DepartmentStaffMappingRequest() {
    }

    // Getters and Setters
    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
