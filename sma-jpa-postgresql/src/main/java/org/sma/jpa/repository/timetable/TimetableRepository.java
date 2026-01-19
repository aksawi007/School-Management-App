package org.sma.jpa.repository.timetable;

import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.school.AcademicYear;
import org.sma.jpa.model.master.ClassMaster;
import org.sma.jpa.model.master.SectionMaster;
import org.sma.jpa.model.staff.Staff;
import org.sma.jpa.model.timetable.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Timetable entity
 */
@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    
    List<Timetable> findBySchoolAndAcademicYearAndClassMasterAndSection(SchoolProfile school, AcademicYear academicYear, ClassMaster classMaster, SectionMaster section);
    
    List<Timetable> findBySchoolAndAcademicYearAndClassMasterAndSectionAndDayOfWeek(SchoolProfile school, AcademicYear academicYear, ClassMaster classMaster, SectionMaster section, String dayOfWeek);
    
    List<Timetable> findByStaffAndAcademicYear(Staff staff, AcademicYear academicYear);
}
