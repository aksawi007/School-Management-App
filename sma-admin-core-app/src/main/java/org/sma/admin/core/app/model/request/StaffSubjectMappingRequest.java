package org.sma.admin.core.app.model.request;

/**
 * Request model for creating/updating staff-subject mapping
 */
public class StaffSubjectMappingRequest {
    private Long schoolId;
    private Long staffId;
    private Long subjectId;
    private String proficiencyLevel; // EXPERT, QUALIFIED, SUBSTITUTE
    private Boolean canTeachPrimary = false;
    private Boolean canTeachSecondary = false;
    private Boolean canTeachHigherSecondary = false;
    private String remarks;

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

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
