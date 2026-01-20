package org.sma.jpa.model.master;

import org.sma.jpa.model.BaseEntity;
import org.sma.jpa.model.school.SchoolProfile;

import javax.persistence.*;

/**
 * Entity for Section - Master Data
 */
@Entity
@Table(name = "section_master", schema = "sma_admin",
        uniqueConstraints = @UniqueConstraint(columnNames = {"school_id", "class_id", "section_code"}))
public class SectionMaster extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private SchoolProfile school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassMaster classMaster;

    @Column(name = "section_code", nullable = false, length = 10)
    private String sectionCode; // e.g., "A", "B", "C"

    @Column(name = "section_name", nullable = false, length = 100)
    private String sectionName;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "room_number", length = 50)
    private String roomNumber;

    @Column(name = "description", length = 500)
    private String description;

    // Getters and Setters
    public SchoolProfile getSchool() {
        return school;
    }

    public void setSchool(SchoolProfile school) {
        this.school = school;
    }

    public ClassMaster getClassMaster() {
        return classMaster;
    }

    public void setClassMaster(ClassMaster classMaster) {
        this.classMaster = classMaster;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public void setSectionCode(String sectionCode) {
        this.sectionCode = sectionCode;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
