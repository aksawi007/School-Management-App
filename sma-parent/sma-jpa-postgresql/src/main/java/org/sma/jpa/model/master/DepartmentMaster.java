package org.sma.jpa.model.master;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.sma.jpa.model.BaseEntity;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.staff.Staff;

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
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private SchoolProfile school;

    @Column(name = "department_code", nullable = false, length = 20)
    private String departmentCode;

    @Column(name = "department_name", nullable = false, length = 150)
    private String departmentName;

    @Column(name = "department_type", length = 50)
    private String departmentType; // ACADEMIC, ADMINISTRATION, SUPPORT

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hod_staff_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Staff headOfDepartment; // Head of Department

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

    public Staff getHeadOfDepartment() {
        return headOfDepartment;
    }

    public void setHeadOfDepartment(Staff headOfDepartment) {
        this.headOfDepartment = headOfDepartment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
