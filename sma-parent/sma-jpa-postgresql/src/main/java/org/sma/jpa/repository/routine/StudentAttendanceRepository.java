package org.sma.jpa.repository.routine;

import org.sma.jpa.model.routine.StudentAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentAttendanceRepository extends JpaRepository<StudentAttendance, Long> {

    @Query("SELECT sa FROM StudentAttendance sa WHERE sa.classSession.id = :sessionId")
    List<StudentAttendance> findBySessionId(@Param("sessionId") Long sessionId);

    @Query("SELECT sa FROM StudentAttendance sa " +
           "WHERE sa.classSession.id = :sessionId " +
           "AND sa.student.id = :studentId")
    Optional<StudentAttendance> findBySessionAndStudent(
            @Param("sessionId") Long sessionId,
            @Param("studentId") Long studentId);

    @Query("SELECT sa FROM StudentAttendance sa " +
           "JOIN sa.classSession dcs " +
           "WHERE sa.student.id = :studentId " +
           "AND dcs.sessionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY dcs.sessionDate, dcs.timeSlot.displayOrder")
    List<StudentAttendance> findStudentAttendanceHistory(
            @Param("studentId") Long studentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT sa FROM StudentAttendance sa " +
           "JOIN sa.classSession dcs " +
           "WHERE dcs.classMaster.id = :classId " +
           "AND dcs.section.id = :sectionId " +
           "AND dcs.sessionDate = :sessionDate " +
           "ORDER BY sa.student.id")
    List<StudentAttendance> findClassAttendanceForDate(
            @Param("classId") Long classId,
            @Param("sectionId") Long sectionId,
            @Param("sessionDate") LocalDate sessionDate);

    @Query("SELECT COUNT(sa) FROM StudentAttendance sa " +
           "JOIN sa.classSession dcs " +
           "WHERE sa.student.id = :studentId " +
           "AND dcs.sessionDate BETWEEN :startDate AND :endDate " +
           "AND sa.attendanceStatus = :status")
    long countStudentAttendanceByStatus(
            @Param("studentId") Long studentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") String status);

    @Query("SELECT sa.attendanceStatus, COUNT(sa) FROM StudentAttendance sa " +
           "JOIN sa.classSession dcs " +
           "WHERE dcs.classMaster.id = :classId " +
           "AND dcs.section.id = :sectionId " +
           "AND dcs.sessionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY sa.attendanceStatus")
    List<Object[]> getAttendanceSummaryForClass(
            @Param("classId") Long classId,
            @Param("sectionId") Long sectionId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
