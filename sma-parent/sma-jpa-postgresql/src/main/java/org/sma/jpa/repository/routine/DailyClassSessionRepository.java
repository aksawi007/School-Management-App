package org.sma.jpa.repository.routine;

import org.sma.jpa.model.routine.DailyClassSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyClassSessionRepository extends JpaRepository<DailyClassSession, Long> {

    @Query("SELECT dcs FROM DailyClassSession dcs " +
           "WHERE dcs.school.id = :schoolId " +
           "AND dcs.classMaster.id = :classId " +
           "AND dcs.section.id = :sectionId " +
           "AND dcs.sessionDate = :sessionDate " +
           "ORDER BY dcs.timeSlot.displayOrder")
    List<DailyClassSession> findDaySchedule(
            @Param("schoolId") Long schoolId,
            @Param("classId") Long classId,
            @Param("sectionId") Long sectionId,
            @Param("sessionDate") LocalDate sessionDate);

    @Query("SELECT dcs FROM DailyClassSession dcs " +
           "WHERE dcs.sessionDate = :sessionDate " +
           "AND (dcs.teacherOverride.id = :teacherId OR dcs.actualTeacher.id = :teacherId) " +
           "ORDER BY dcs.timeSlot.displayOrder")
    List<DailyClassSession> findTeacherScheduleForDate(
            @Param("teacherId") Long teacherId,
            @Param("sessionDate") LocalDate sessionDate);

    @Query("SELECT dcs FROM DailyClassSession dcs " +
           "WHERE dcs.classMaster.id = :classId " +
           "AND dcs.section.id = :sectionId " +
           "AND dcs.sessionDate = :sessionDate " +
           "AND dcs.timeSlot.id = :timeSlotId")
    Optional<DailyClassSession> findByClassDateAndSlot(
            @Param("classId") Long classId,
            @Param("sectionId") Long sectionId,
            @Param("sessionDate") LocalDate sessionDate,
            @Param("timeSlotId") Long timeSlotId);

    @Query("SELECT dcs FROM DailyClassSession dcs " +
           "WHERE dcs.sessionDate BETWEEN :startDate AND :endDate " +
           "AND dcs.classMaster.id = :classId " +
           "AND dcs.section.id = :sectionId " +
           "ORDER BY dcs.sessionDate, dcs.timeSlot.displayOrder")
    List<DailyClassSession> findSessionsInDateRange(
            @Param("classId") Long classId,
            @Param("sectionId") Long sectionId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(dcs) FROM DailyClassSession dcs " +
           "WHERE dcs.sessionDate = :sessionDate " +
           "AND dcs.actualTeacher.id = :teacherId")
    long countTeacherSessionsForDate(@Param("teacherId") Long teacherId, @Param("sessionDate") LocalDate sessionDate);

    @Query("SELECT dcs FROM DailyClassSession dcs " +
           "WHERE dcs.id = :sessionId " +
           "AND dcs.school.id = :schoolId")
    Optional<DailyClassSession> findByIdAndSchool(@Param("sessionId") Long sessionId, @Param("schoolId") Long schoolId);
}
