package org.sma.jpa.model.routine;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.sma.jpa.model.BaseEntity;
import org.sma.jpa.model.school.SchoolProfile;

import javax.persistence.*;
import java.time.LocalTime;

/**
 * Routine Time Slot - Master time slots for class schedule
 * Reusable across all classes and sections
 */
@Entity
@Table(name = "routine_time_slot", schema = "sma_admin",
        indexes = {
            @Index(name = "idx_time_slot_school", columnList = "school_id"),
            @Index(name = "idx_time_slot_order", columnList = "school_id, display_order")
        })
public class RoutineTimeSlot extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private SchoolProfile school;

    @Column(name = "slot_name", nullable = false, length = 50)
    private String slotName;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "slot_type", length = 20)
    private String slotType; // TEACHING, BREAK, LUNCH, ASSEMBLY

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Constructors
    public RoutineTimeSlot() {}

    // Getters and Setters
    public SchoolProfile getSchool() {
        return school;
    }

    public void setSchool(SchoolProfile school) {
        this.school = school;
    }

    public String getSlotName() {
        return slotName;
    }

    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getSlotType() {
        return slotType;
    }

    public void setSlotType(String slotType) {
        this.slotType = slotType;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
