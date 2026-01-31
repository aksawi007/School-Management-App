package org.sma.jpa.repository.routine;

import org.sma.jpa.model.routine.ClassRoutineMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRoutineMasterRepository extends JpaRepository<ClassRoutineMaster, Long> {

    @Query("SELECT crm FROM ClassRoutineMaster crm " +
           "WHERE crm.school.id = :schoolId " +
           "AND crm.academicYear.id = :academicYearId " +
           "AND crm.classMaster.id = :classId " +
           "AND crm.section.id = :sectionId " +
           "AND crm.isActive = true " +
           "ORDER BY crm.dayOfWeek, crm.timeSlot.displayOrder")
    List<ClassRoutineMaster> findWeeklyRoutine(
            @Param("schoolId") Long schoolId,
            @Param("academicYearId") Long academicYearId,
            @Param("classId") Long classId,
            @Param("sectionId") Long sectionId);

    @Query("SELECT crm FROM ClassRoutineMaster crm " +
           "WHERE crm.school.id = :schoolId " +
           "AND crm.academicYear.id = :academicYearId " +
           "AND crm.classMaster.id = :classId " +
           "AND crm.section.id = :sectionId " +
           "AND crm.dayOfWeek = :dayOfWeek " +
           "AND crm.isActive = true " +
           "ORDER BY crm.timeSlot.displayOrder")
    List<ClassRoutineMaster> findDailyRoutine(
            @Param("schoolId") Long schoolId,
            @Param("academicYearId") Long academicYearId,
            @Param("classId") Long classId,
            @Param("sectionId") Long sectionId,
            @Param("dayOfWeek") String dayOfWeek);

    @Query("SELECT crm FROM ClassRoutineMaster crm " +
           "WHERE crm.teacher.id = :teacherId " +
           "AND crm.dayOfWeek = :dayOfWeek " +
           "AND crm.isActive = true " +
           "ORDER BY crm.timeSlot.displayOrder")
    List<ClassRoutineMaster> findTeacherRoutineForDay(
            @Param("teacherId") Long teacherId,
            @Param("dayOfWeek") String dayOfWeek);

    @Query("SELECT crm FROM ClassRoutineMaster crm " +
           "WHERE crm.school.id = :schoolId " +
           "AND crm.academicYear.id = :academicYearId " +
           "AND crm.classMaster.id = :classId " +
           "AND crm.section.id = :sectionId " +
           "AND crm.dayOfWeek = :dayOfWeek " +
           "AND crm.timeSlot.id = :timeSlotId " +
           "AND crm.isActive = true")
    Optional<ClassRoutineMaster> findByUniqueConstraint(
            @Param("schoolId") Long schoolId,
            @Param("academicYearId") Long academicYearId,
            @Param("classId") Long classId,
            @Param("sectionId") Long sectionId,
            @Param("dayOfWeek") String dayOfWeek,
            @Param("timeSlotId") Long timeSlotId);

    @Query("SELECT crm FROM ClassRoutineMaster crm " +
           "WHERE crm.school.id = :schoolId " +
           "AND crm.academicYear.id = :academicYearId " +
           "AND crm.teacher.id = :teacherId " +
           "AND crm.timeSlot.id = :timeSlotId " +
           "AND crm.isActive = true")
    List<ClassRoutineMaster> findConflictingRoutines(
            @Param("schoolId") Long schoolId,
            @Param("teacherId") Long teacherId,
            @Param("timeSlotId") Long timeSlotId,
            @Param("academicYearId") Long academicYearId);
}
