package org.sma.admin.core.app.model.response;

/**
 * Response model for staff-subject mapping
 */
public class StaffSubjectMappingResponse {
    private Long id;
    private Long schoolId;
    private Long staffId;
    private String staffName;
    private String employeeCode;
    private Long subjectId;
    private String subjectName;
    private String subjectCode;
    private String proficiencyLevel;
    private Boolean canTeachPrimary;
    private Boolean canTeachSecondary;
    private Boolean canTeachHigherSecondary;
    private Boolean isActive;
    private String remarks;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(String proficiencyLevel) {
        this.proficiencyLevel = proficiencyLevel;
    }

    public Boolean getCanTeachPrimary() {
        return canTeachPrimary;
    }

    public void setCanTeachPrimary(Boolean canTeachPrimary) {
        this.canTeachPrimary = canTeachPrimary;
    }

    public Boolean getCanTeachSecondary() {
        return canTeachSecondary;
    }

    public void setCanTeachSecondary(Boolean canTeachSecondary) {
        this.canTeachSecondary = canTeachSecondary;
    }

    public Boolean getCanTeachHigherSecondary() {
        return canTeachHigherSecondary;
    }

    public void setCanTeachHigherSecondary(Boolean canTeachHigherSecondary) {
        this.canTeachHigherSecondary = canTeachHigherSecondary;
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
