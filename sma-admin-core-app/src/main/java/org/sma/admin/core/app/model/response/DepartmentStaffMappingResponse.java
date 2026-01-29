package org.sma.admin.core.app.model.response;

import java.time.LocalDate;

/**
 * Response model for Department-Staff Mapping
 */
public class DepartmentStaffMappingResponse {

    private Long mappingId;
    private Long departmentId;
    private String departmentCode;
    private String departmentName;
    private Long staffId;
    private String employeeCode;
    private String staffName;
    private String roleInDepartment;
    private LocalDate assignmentDate;
    private Boolean isPrimaryDepartment;
    private Boolean isActive;
    private String remarks;

    // Constructors
    public DepartmentStaffMappingResponse() {
    }

    // Getters and Setters
    public Long getMappingId() {
        return mappingId;
    }

    public void setMappingId(Long mappingId) {
        this.mappingId = mappingId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
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
