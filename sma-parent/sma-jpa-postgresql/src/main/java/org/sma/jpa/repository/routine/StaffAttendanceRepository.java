package org.sma.jpa.repository.routine;

import org.sma.jpa.model.routine.StaffAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StaffAttendanceRepository extends JpaRepository<StaffAttendance, Long> {

    @Query("SELECT sa FROM StaffAttendance sa WHERE sa.classSession.id = :sessionId")
    List<StaffAttendance> findBySessionId(@Param("sessionId") Long sessionId);

    @Query("SELECT sa FROM StaffAttendance sa " +
           "WHERE sa.classSession.id = :sessionId " +
           "AND sa.staff.id = :staffId")
    Optional<StaffAttendance> findBySessionAndStaff(
            @Param("sessionId") Long sessionId,
            @Param("staffId") Long staffId);

    @Query("SELECT sa FROM StaffAttendance sa " +
           "JOIN sa.classSession dcs " +
           "WHERE dcs.sessionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY dcs.sessionDate")
    List<StaffAttendance> findStaffAttendanceHistory(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
