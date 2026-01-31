package org.sma.jpa.repository.routine;

import org.sma.jpa.model.routine.RoutineTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutineTimeSlotRepository extends JpaRepository<RoutineTimeSlot, Long> {

    @Query("SELECT rts FROM RoutineTimeSlot rts WHERE rts.school.id = :schoolId AND rts.isActive = true ORDER BY rts.displayOrder")
    List<RoutineTimeSlot> findBySchoolIdAndIsActiveTrue(@Param("schoolId") Long schoolId);

    @Query("SELECT rts FROM RoutineTimeSlot rts WHERE rts.school.id = :schoolId AND rts.slotType = :slotType AND rts.isActive = true ORDER BY rts.displayOrder")
    List<RoutineTimeSlot> findBySchoolIdAndSlotType(@Param("schoolId") Long schoolId, @Param("slotType") String slotType);

    @Query("SELECT COUNT(rts) FROM RoutineTimeSlot rts WHERE rts.school.id = :schoolId AND rts.isActive = true")
    long countActiveSlotsBySchool(@Param("schoolId") Long schoolId);
}
