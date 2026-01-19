package org.sma.jpa.model.master;

import org.sma.jpa.model.BaseEntity;
import org.sma.jpa.model.school.SchoolProfile;

import javax.persistence.*;

/**
 * Entity for Department - Master Data
 */
@Entity
@Table(name = "department_master", schema = "sma_admin",
        uniqueConstraints = @UniqueConstraint(columnNames = {"school_id", "department_code"}))
public class DepartmentMaster extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private SchoolProfile school;

    @Column(name = "department_code", nullable = false, length = 20)
    private String departmentCode;

    @Column(name = "department_name", nullable = false, length = 150)
    private String departmentName;

    @Column(name = "department_type", length = 50)
    private String departmentType; // ACADEMIC, ADMINISTRATION, SUPPORT

    @Column(name = "hod_name", length = 150)
    private String hodName; // Head of Department

    @Column(name = "hod_email", length = 100)
    private String hodEmail;

    @Column(name = "hod_phone", length = 20)
    private String hodPhone;

    @Column(name = "description", length = 500)
    private String description;

    // Getters and Setters
    public SchoolProfile getSchool() {
        return school;
    }

    public void setSchool(SchoolProfile school) {
        this.school = school;
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

    public String getHodName() {
        return hodName;
    }

    public void setHodName(String hodName) {
        this.hodName = hodName;
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
