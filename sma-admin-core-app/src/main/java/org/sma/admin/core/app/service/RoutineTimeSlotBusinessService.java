package org.sma.admin.core.app.service;

import org.sma.admin.core.app.model.request.RoutineTimeSlotRequest;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.routine.RoutineTimeSlot;
import org.sma.jpa.repository.school.SchoolProfileRepository;
import org.sma.jpa.repository.routine.RoutineTimeSlotRepository;
import org.sma.platform.core.exception.SmaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class RoutineTimeSlotBusinessService {

    @Autowired
    private RoutineTimeSlotRepository routineTimeSlotRepository;

    @Autowired
    private SchoolProfileRepository schoolProfileRepository;

    @Transactional
    public RoutineTimeSlot createTimeSlot(RoutineTimeSlotRequest request) throws SmaException {
        // Validate school
        SchoolProfile school = schoolProfileRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new SmaException("School not found with ID: " + request.getSchoolId()));

        // Validate time range
        if (request.getStartTime().isAfter(request.getEndTime()) || 
            request.getStartTime().equals(request.getEndTime())) {
            throw new SmaException("Start time must be before end time");
        }

        // Check for time overlap with existing slots
        List<RoutineTimeSlot> existingSlots = routineTimeSlotRepository
                .findBySchoolIdAndIsActiveTrue(request.getSchoolId());
        
        for (RoutineTimeSlot existing : existingSlots) {
            if (timeSlotsOverlap(request.getStartTime(), request.getEndTime(), 
                               existing.getStartTime(), existing.getEndTime())) {
                throw new SmaException("Time slot overlaps with existing slot: " + existing.getSlotName());
            }
        }

        // Create new time slot
        RoutineTimeSlot timeSlot = new RoutineTimeSlot();
        timeSlot.setSchool(school);
        timeSlot.setSlotName(request.getSlotName());
        timeSlot.setStartTime(request.getStartTime());
        timeSlot.setEndTime(request.getEndTime());
        timeSlot.setDisplayOrder(request.getDisplayOrder());
        timeSlot.setSlotType(request.getSlotType());
        timeSlot.setIsActive(true);

        return routineTimeSlotRepository.save(timeSlot);
    }

    @Transactional
    public RoutineTimeSlot updateTimeSlot(Long slotId, RoutineTimeSlotRequest request) throws SmaException {
        RoutineTimeSlot timeSlot = routineTimeSlotRepository.findById(slotId)
                .orElseThrow(() -> new SmaException("Time slot not found with ID: " + slotId));

        // Validate time range
        if (request.getStartTime().isAfter(request.getEndTime()) || 
            request.getStartTime().equals(request.getEndTime())) {
            throw new SmaException("Start time must be before end time");
        }

        // Check for time overlap (excluding current slot)
        List<RoutineTimeSlot> existingSlots = routineTimeSlotRepository
                .findBySchoolIdAndIsActiveTrue(request.getSchoolId());
        
        for (RoutineTimeSlot existing : existingSlots) {
            if (!existing.getId().equals(slotId) && 
                timeSlotsOverlap(request.getStartTime(), request.getEndTime(), 
                               existing.getStartTime(), existing.getEndTime())) {
                throw new SmaException("Time slot overlaps with existing slot: " + existing.getSlotName());
            }
        }

        // Update fields
        timeSlot.setSlotName(request.getSlotName());
        timeSlot.setStartTime(request.getStartTime());
        timeSlot.setEndTime(request.getEndTime());
        timeSlot.setDisplayOrder(request.getDisplayOrder());
        timeSlot.setSlotType(request.getSlotType());

        return routineTimeSlotRepository.save(timeSlot);
    }

    @Transactional
    public void deleteTimeSlot(Long slotId) throws SmaException {
        RoutineTimeSlot timeSlot = routineTimeSlotRepository.findById(slotId)
                .orElseThrow(() -> new SmaException("Time slot not found with ID: " + slotId));
        
        // Soft delete
        timeSlot.setIsActive(false);
        routineTimeSlotRepository.save(timeSlot);
    }

    public List<RoutineTimeSlot> getActiveTimeSlots(Long schoolId) {
        return routineTimeSlotRepository.findBySchoolIdAndIsActiveTrue(schoolId);
    }

    public List<RoutineTimeSlot> getTimeSlotsByType(Long schoolId, String slotType) {
        return routineTimeSlotRepository.findBySchoolIdAndSlotType(schoolId, slotType);
    }

    public RoutineTimeSlot getTimeSlotById(Long slotId) throws SmaException {
        return routineTimeSlotRepository.findById(slotId)
                .orElseThrow(() -> new SmaException("Time slot not found with ID: " + slotId));
    }

    private boolean timeSlotsOverlap(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return !start1.isAfter(end2) && !end1.isBefore(start2);
    }
}
