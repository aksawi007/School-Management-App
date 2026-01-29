package org.sma.admin.core.app.model.response;

/**
 * Response model for Department Master
 */
public class DepartmentResponse {

    private Long departmentId;
    private Long schoolId;
    private String departmentCode;
    private String departmentName;
    private String departmentType;
    private Long hodStaffId;
    private String hodEmployeeCode;
    private String hodFullName;
    private String hodEmail;
    private String hodPhone;
    private String description;

    // Constructors
    public DepartmentResponse() {
    }

    // Getters and Setters
    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

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

    public String getHodEmployeeCode() {
        return hodEmployeeCode;
    }

    public void setHodEmployeeCode(String hodEmployeeCode) {
        this.hodEmployeeCode = hodEmployeeCode;
    }

    public String getHodFullName() {
        return hodFullName;
    }

    public void setHodFullName(String hodFullName) {
        this.hodFullName = hodFullName;
    }

    public String getHodEmail() {
        return hodEmail;
    }

    public void setHodEmail(String hodEmail) {
        this.hodEmail = hodEmail;
    }

    public String getHodPhone() {
        return hodPhone;
    }

    public void setHodPhone(String hodPhone) {
        this.hodPhone = hodPhone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
