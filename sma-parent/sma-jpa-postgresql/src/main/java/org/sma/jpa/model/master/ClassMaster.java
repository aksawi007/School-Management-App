package org.sma.jpa.model.master;

import org.sma.jpa.model.BaseEntity;
import org.sma.jpa.model.school.SchoolProfile;

import javax.persistence.*;

/**
 * Entity for Class/Grade - Master Data
 */
@Entity
@Table(name = "class_master", schema = "sma_admin",
        uniqueConstraints = @UniqueConstraint(columnNames = {"school_id", "class_code"}))
public class ClassMaster extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private SchoolProfile school;

    @Column(name = "class_code", nullable = false, length = 20)
    private String classCode; // e.g., "1", "2", "10", "11", "12"

    @Column(name = "class_name", nullable = false, length = 100)
    private String className; // e.g., "Class 1", "Class 10", "Grade 11"

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "description", length = 500)
    private String description;

    // Getters and Setters
    public SchoolProfile getSchool() {
        return school;
    }

    public void setSchool(SchoolProfile school) {
        this.school = school;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
