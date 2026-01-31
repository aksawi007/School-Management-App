package org.sma.admin.core.app.controller;

import org.sma.admin.core.app.model.request.RoutineTimeSlotRequest;
import org.sma.admin.core.app.service.RoutineTimeSlotBusinessService;
import org.sma.jpa.model.routine.RoutineTimeSlot;
import org.sma.platform.core.exception.SmaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schools/{schoolId}/routine/time-slots")
@CrossOrigin
public class RoutineTimeSlotController {

    @Autowired
    private RoutineTimeSlotBusinessService routineTimeSlotService;

    @PostMapping
    public ResponseEntity<?> createTimeSlot(@PathVariable Long schoolId, 
                                           @RequestBody RoutineTimeSlotRequest request) {
        try {
            request.setSchoolId(schoolId);
            RoutineTimeSlot created = routineTimeSlotService.createTimeSlot(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (SmaException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("error", "Business Rule Violation");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/{slotId}")
    public ResponseEntity<?> updateTimeSlot(@PathVariable Long schoolId,
                                           @PathVariable Long slotId,
                                           @RequestBody RoutineTimeSlotRequest request) {
        try {
            request.setSchoolId(schoolId);
            RoutineTimeSlot updated = routineTimeSlotService.updateTimeSlot(slotId, request);
            return ResponseEntity.ok(updated);
        } catch (SmaException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("error", "Business Rule Violation");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/{slotId}")
    public ResponseEntity<?> deleteTimeSlot(@PathVariable Long schoolId, @PathVariable Long slotId) {
        try {
            routineTimeSlotService.deleteTimeSlot(slotId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Time slot deleted successfully");
            return ResponseEntity.ok(response);
        } catch (SmaException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("error", "Business Rule Violation");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping
    public ResponseEntity<List<RoutineTimeSlot>> getActiveTimeSlots(@PathVariable Long schoolId) {
        List<RoutineTimeSlot> slots = routineTimeSlotService.getActiveTimeSlots(schoolId);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/type/{slotType}")
    public ResponseEntity<List<RoutineTimeSlot>> getTimeSlotsByType(@PathVariable Long schoolId,
                                                                     @PathVariable String slotType) {
        List<RoutineTimeSlot> slots = routineTimeSlotService.getTimeSlotsByType(schoolId, slotType);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/{slotId}")
    public ResponseEntity<?> getTimeSlotById(@PathVariable Long schoolId, @PathVariable Long slotId) {
        try {
            RoutineTimeSlot slot = routineTimeSlotService.getTimeSlotById(slotId);
            return ResponseEntity.ok(slot);
        } catch (SmaException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("error", "Not Found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}
