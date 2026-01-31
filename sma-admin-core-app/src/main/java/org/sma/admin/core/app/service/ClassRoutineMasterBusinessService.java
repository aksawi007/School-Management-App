package org.sma.admin.core.app.service;

import org.sma.admin.core.app.model.request.ClassRoutineMasterRequest;
import org.sma.jpa.model.master.ClassMaster;
import org.sma.jpa.model.master.SectionMaster;
import org.sma.jpa.model.master.SubjectMaster;
import org.sma.jpa.model.school.AcademicYear;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.staff.Staff;
import org.sma.jpa.model.routine.ClassRoutineMaster;
import org.sma.jpa.model.routine.RoutineTimeSlot;
import org.sma.jpa.repository.master.ClassMasterRepository;
import org.sma.jpa.repository.master.SectionMasterRepository;
import org.sma.jpa.repository.master.SubjectMasterRepository;
import org.sma.jpa.repository.school.AcademicYearRepository;
import org.sma.jpa.repository.school.SchoolProfileRepository;
import org.sma.jpa.repository.staff.StaffRepository;
import org.sma.jpa.repository.routine.ClassRoutineMasterRepository;
import org.sma.jpa.repository.routine.RoutineTimeSlotRepository;
import org.sma.platform.core.exception.SmaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Service
public class ClassRoutineMasterBusinessService {

    @Autowired
    private ClassRoutineMasterRepository classRoutineMasterRepository;

    @Autowired
    private SchoolProfileRepository schoolProfileRepository;

    @Autowired
    private AcademicYearRepository academicYearRepository;

    @Autowired
    private ClassMasterRepository classMasterRepository;

    @Autowired
    private SectionMasterRepository sectionMasterRepository;

    @Autowired
    private RoutineTimeSlotRepository routineTimeSlotRepository;

    @Autowired
    private SubjectMasterRepository subjectMasterRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Transactional
    public ClassRoutineMaster createOrUpdateRoutine(ClassRoutineMasterRequest request) throws SmaException {
        // Validate all required entities
        SchoolProfile school = schoolProfileRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new SmaException("School not found"));
        
        AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                .orElseThrow(() -> new SmaException("Academic year not found"));
        
        ClassMaster classMaster = classMasterRepository.findById(request.getClassId())
                .orElseThrow(() -> new SmaException("Class not found"));
        
        SectionMaster section = sectionMasterRepository.findById(request.getSectionId())
                .orElseThrow(() -> new SmaException("Section not found"));
        
        RoutineTimeSlot timeSlot = routineTimeSlotRepository.findById(request.getTimeSlotId())
                .orElseThrow(() -> new SmaException("Time slot not found"));
        
        SubjectMaster subject = subjectMasterRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new SmaException("Subject not found"));
        
        Staff teacher = staffRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new SmaException("Teacher not found"));

        // Check if routine already exists (update scenario)
        Optional<ClassRoutineMaster> existingOpt = classRoutineMasterRepository.findByUniqueConstraint(
                request.getSchoolId(), 
                request.getAcademicYearId(), 
                request.getClassId(), 
                request.getSectionId(), 
                request.getDayOfWeek(), 
                request.getTimeSlotId());

        ClassRoutineMaster routine;
        if (existingOpt.isPresent()) {
            // Update existing
            routine = existingOpt.get();
            routine.setSubject(subject);
            routine.setTeacher(teacher);
            routine.setRemarks(request.getRemarks());
        } else {
            // Create new
            routine = new ClassRoutineMaster();
            routine.setSchool(school);
            routine.setAcademicYear(academicYear);
            routine.setClassMaster(classMaster);
            routine.setSection(section);
            routine.setDayOfWeek(request.getDayOfWeek());
            routine.setTimeSlot(timeSlot);
            routine.setSubject(subject);
            routine.setTeacher(teacher);
            routine.setRemarks(request.getRemarks());
        }

        return classRoutineMasterRepository.save(routine);
    }

    public List<ClassRoutineMaster> getWeeklyRoutine(Long schoolId, Long academicYearId, 
                                                     Long classId, Long sectionId) {
        return classRoutineMasterRepository.findWeeklyRoutine(schoolId, academicYearId, classId, sectionId);
    }

    public List<ClassRoutineMaster> getDailyRoutine(Long schoolId, Long academicYearId, 
                                                    Long classId, Long sectionId, String dayOfWeek) {
        return classRoutineMasterRepository.findDailyRoutine(schoolId, academicYearId, classId, sectionId, dayOfWeek);
    }

    public List<ClassRoutineMaster> getTeacherRoutine(Long schoolId, Long academicYearId, 
                                                      Long teacherId, String dayOfWeek) {
        return classRoutineMasterRepository.findTeacherRoutineForDay(teacherId, dayOfWeek);
    }

    @Transactional
    public void deleteRoutineEntry(Long routineId) throws SmaException {
        if (!classRoutineMasterRepository.existsById(routineId)) {
            throw new SmaException("Routine entry not found");
        }
        classRoutineMasterRepository.deleteById(routineId);
    }

    public ClassRoutineMaster getRoutineById(Long routineId) throws SmaException {
        return classRoutineMasterRepository.findById(routineId)
                .orElseThrow(() -> new SmaException("Routine entry not found"));
    }

    public Map<String, Object> checkTeacherAvailability(Long schoolId, Long teacherId, Long timeSlotId,
                                                         Long academicYearId, Long classId, Long sectionId) {
        Map<String, Object> result = new HashMap<>();
        
        // Find all routine entries for this teacher in the same time slot across all classes
        List<ClassRoutineMaster> conflictingRoutines = classRoutineMasterRepository
                .findConflictingRoutines(schoolId, teacherId, timeSlotId, academicYearId);
        
        // Filter out the current class to allow updating existing entries
        conflictingRoutines.removeIf(routine -> 
            routine.getClassMaster().getId().equals(classId) && 
            routine.getSection().getId().equals(sectionId)
        );
        
        boolean available = conflictingRoutines.isEmpty();
        result.put("available", available);
        result.put("conflictingRoutines", conflictingRoutines);
        
        return result;
    }
}
