package org.sma.admin.core.app.controller;

import org.sma.admin.core.app.model.request.ClassRoutineMasterRequest;
import org.sma.admin.core.app.service.ClassRoutineMasterBusinessService;
import org.sma.jpa.model.routine.ClassRoutineMaster;
import org.sma.platform.core.exception.SmaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schools/{schoolId}/routine/master")
@CrossOrigin
public class ClassRoutineMasterController {

    @Autowired
    private ClassRoutineMasterBusinessService classRoutineMasterService;

    @PostMapping
    public ResponseEntity<?> createOrUpdateRoutine(@PathVariable Long schoolId,
                                                   @RequestBody ClassRoutineMasterRequest request) {
        try {
            request.setSchoolId(schoolId);
            ClassRoutineMaster routine = classRoutineMasterService.createOrUpdateRoutine(request);
            return ResponseEntity.ok(routine);
        } catch (SmaException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("error", "Business Rule Violation");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/weekly")
    public ResponseEntity<List<ClassRoutineMaster>> getWeeklyRoutine(
            @PathVariable Long schoolId,
            @RequestParam Long academicYearId,
            @RequestParam Long classId,
            @RequestParam Long sectionId) {
        List<ClassRoutineMaster> routine = classRoutineMasterService.getWeeklyRoutine(
                schoolId, academicYearId, classId, sectionId);
        return ResponseEntity.ok(routine);
    }

    @GetMapping("/daily")
    public ResponseEntity<List<ClassRoutineMaster>> getDailyRoutine(
            @PathVariable Long schoolId,
            @RequestParam Long academicYearId,
            @RequestParam Long classId,
            @RequestParam Long sectionId,
            @RequestParam String dayOfWeek) {
        List<ClassRoutineMaster> routine = classRoutineMasterService.getDailyRoutine(
                schoolId, academicYearId, classId, sectionId, dayOfWeek);
        return ResponseEntity.ok(routine);
    }

    @GetMapping("/teacher")
    public ResponseEntity<List<ClassRoutineMaster>> getTeacherRoutine(
            @PathVariable Long schoolId,
            @RequestParam Long academicYearId,
            @RequestParam Long teacherId,
            @RequestParam String dayOfWeek) {
        List<ClassRoutineMaster> routine = classRoutineMasterService.getTeacherRoutine(
                schoolId, academicYearId, teacherId, dayOfWeek);
        return ResponseEntity.ok(routine);
    }

    @DeleteMapping("/{routineId}")
    public ResponseEntity<?> deleteRoutineEntry(@PathVariable Long schoolId, 
                                                @PathVariable Long routineId) {
        try {
            classRoutineMasterService.deleteRoutineEntry(routineId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Routine entry deleted successfully");
            return ResponseEntity.ok(response);
        } catch (SmaException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("error", "Business Rule Violation");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/{routineId}")
    public ResponseEntity<?> getRoutineById(@PathVariable Long schoolId, 
                                           @PathVariable Long routineId) {
        try {
            ClassRoutineMaster routine = classRoutineMasterService.getRoutineById(routineId);
            return ResponseEntity.ok(routine);
        } catch (SmaException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("error", "Not Found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}
