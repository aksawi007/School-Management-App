package org.sma.admin.core.app.model.request;

/**
 * Request model for Department Master
 */
public class DepartmentRequest {

    private Long schoolId;
    private String departmentCode;
    private String departmentName;
    private String departmentType;
    private Long hodStaffId;
    private String description;

    // Constructors
    public DepartmentRequest() {
    }

    // Getters and Setters
    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
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

    public String getDepartmentType() {
        return departmentType;
    }

    public void setDepartmentType(String departmentType) {
        this.departmentType = departmentType;
    }

    public Long getHodStaffId() {
        return hodStaffId;
    }

    public void setHodStaffId(Long hodStaffId) {
        this.hodStaffId = hodStaffId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
