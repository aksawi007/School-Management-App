package org.sma.admin.core.app.service;

import org.sma.admin.core.app.model.request.DailyClassSessionRequest;
import org.sma.jpa.model.master.ClassMaster;
import org.sma.jpa.model.master.SectionMaster;
import org.sma.jpa.model.master.SubjectMaster;
import org.sma.jpa.model.school.AcademicYear;
import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.staff.Staff;
import org.sma.jpa.model.routine.ClassRoutineMaster;
import org.sma.jpa.model.routine.DailyClassSession;
import org.sma.jpa.model.routine.RoutineTimeSlot;
import org.sma.jpa.repository.master.ClassMasterRepository;
import org.sma.jpa.repository.master.SectionMasterRepository;
import org.sma.jpa.repository.master.SubjectMasterRepository;
import org.sma.jpa.repository.school.AcademicYearRepository;
import org.sma.jpa.repository.school.SchoolProfileRepository;
import org.sma.jpa.repository.staff.StaffRepository;
import org.sma.jpa.repository.routine.ClassRoutineMasterRepository;
import org.sma.jpa.repository.routine.DailyClassSessionRepository;
import org.sma.jpa.repository.routine.RoutineTimeSlotRepository;
import org.sma.platform.core.exception.SmaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class DailyClassSessionBusinessService {

    @Autowired
    private DailyClassSessionRepository dailyClassSessionRepository;

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

    /**
     * Get complete day schedule by merging master routine with daily overrides
     * This is the core space-efficient logic
     */
    public List<Object> getDayCompleteSchedule(Long schoolId, Long academicYearId, 
                                               Long classId, Long sectionId, LocalDate date) {
        // Get day of week
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        String dayName = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase();

        // Get master routine for this day
        List<ClassRoutineMaster> masterRoutine = classRoutineMasterRepository.findDailyRoutine(
                schoolId, academicYearId, classId, sectionId, dayName);

        // Get any daily overrides for this date
        List<DailyClassSession> dailySessions = dailyClassSessionRepository.findDaySchedule(
                schoolId, classId, sectionId, date);

        // Merge logic: For each master entry, check if daily override exists
        List<Object> completeSchedule = new ArrayList<>();
        
        for (ClassRoutineMaster master : masterRoutine) {
            // Find matching daily session
            Optional<DailyClassSession> dailyOpt = dailySessions.stream()
                    .filter(ds -> ds.getRoutineMaster().getId().equals(master.getId()))
                    .findFirst();

            if (dailyOpt.isPresent()) {
                // Use daily session (has overrides or attendance)
                completeSchedule.add(dailyOpt.get());
            } else {
                // Use master routine (no changes for this day)
                completeSchedule.add(master);
            }
        }

        return completeSchedule;
    }

    @Transactional
    public DailyClassSession createOrUpdateSession(DailyClassSessionRequest request) throws SmaException {
        // Validate all entities
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
        
        ClassRoutineMaster routineMaster = classRoutineMasterRepository.findById(request.getRoutineMasterId())
                .orElseThrow(() -> new SmaException("Routine master not found"));

        // Check if session already exists
        Optional<DailyClassSession> existingOpt = dailyClassSessionRepository.findByClassDateAndSlot(
                request.getSchoolId(), 
                request.getClassId(), 
                request.getSessionDate(), 
                request.getTimeSlotId());

        DailyClassSession session;
        if (existingOpt.isPresent()) {
            // Update existing
            session = existingOpt.get();
        } else {
            // Create new
            session = new DailyClassSession();
            session.setSchool(school);
            session.setAcademicYear(academicYear);
            session.setClassMaster(classMaster);
            session.setSection(section);
            session.setSessionDate(request.getSessionDate());
            session.setTimeSlot(timeSlot);
            session.setRoutineMaster(routineMaster);
        }

        // Update fields (overrides only if different from master)
        if (request.getSubjectOverride() != null) {
            SubjectMaster subjectOverride = subjectMasterRepository.findById(request.getSubjectOverride())
                    .orElseThrow(() -> new SmaException("Subject override not found"));
            session.setSubjectOverride(subjectOverride);
        }

        if (request.getTeacherOverride() != null) {
            Staff teacherOverride = staffRepository.findById(request.getTeacherOverride())
                    .orElseThrow(() -> new SmaException("Teacher override not found"));
            session.setTeacherOverride(teacherOverride);
        }

        if (request.getActualTeacherId() != null) {
            Staff actualTeacher = staffRepository.findById(request.getActualTeacherId())
                    .orElseThrow(() -> new SmaException("Actual teacher not found"));
            session.setActualTeacher(actualTeacher);
        }

        session.setSessionStatus(request.getSessionStatus());
        session.setRemarks(request.getRemarks());

        return dailyClassSessionRepository.save(session);
    }

    public List<DailyClassSession> getTeacherScheduleForDate(Long schoolId, Long academicYearId, 
                                                             Long teacherId, LocalDate date) {
        return dailyClassSessionRepository.findTeacherScheduleForDate(teacherId, date);
    }

    public List<DailyClassSession> getSessionsInDateRange(Long schoolId, Long academicYearId, 
                                                          Long classId, Long sectionId, 
                                                          LocalDate startDate, LocalDate endDate) {
        return dailyClassSessionRepository.findSessionsInDateRange(
                schoolId, classId, startDate, endDate);
    }

    @Transactional
    public DailyClassSession updateSessionStatus(Long sessionId, String status) throws SmaException {
        DailyClassSession session = dailyClassSessionRepository.findById(sessionId)
                .orElseThrow(() -> new SmaException("Session not found"));
        
        session.setSessionStatus(status);
        return dailyClassSessionRepository.save(session);
    }

    @Transactional
    public DailyClassSession markAttendanceCompleted(Long sessionId) throws SmaException {
        DailyClassSession session = dailyClassSessionRepository.findById(sessionId)
                .orElseThrow(() -> new SmaException("Session not found"));
        
        session.setIsAttendanceMarked(true);
        return dailyClassSessionRepository.save(session);
    }

    public DailyClassSession getSessionById(Long sessionId) throws SmaException {
        return dailyClassSessionRepository.findById(sessionId)
                .orElseThrow(() -> new SmaException("Session not found"));
    }
}
